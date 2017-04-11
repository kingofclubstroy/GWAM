package com.example.my.facebookauth.Activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.event;
import com.example.my.facebookauth.models.eventListAdapter;
import com.example.my.facebookauth.utilities.savedPreferences;
import com.example.my.facebookauth.utilities.scrollFilter.CircularArrayAdapter;
import com.facebook.Profile;
import com.firebase.client.AuthData;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.my.facebookauth.R.id.scrollFilter;


/**
 * Created by Owner on 2016-11-12.
 */

public class event_feed extends AppCompatActivity {

    private String TAG = event_feed.class.getSimpleName();
    private FirebaseAuth mAuth;
    private List<event> eventList;
    private double lat;
    private double lng;
    private DatabaseReference geofireRef;
    private DatabaseReference eventRef;
    private DatabaseReference locationRef;
    private String uid;
    private AuthData mAuthData;
    private GeoFire geofire;
    private eventListAdapter meventListAdapter;
    private Context mContext;
    private GeoQuery geoQuery;
    //private ListView mListView;
    private boolean geoQueryBool;
    private GeoQueryEventListener geoListener;
    private DatabaseReference mRef;
    private String id;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;
    private List<String> eventKeys;
    private String interested_events;
    private HashMap<String, event> hashEventList;
    private RecyclerView scrollFilter;
    private CircularArrayAdapter filterAdapter;
    private LinearLayoutManager manager;
    private RecyclerView mListView;
    private SwipeActionAdapter swipeActionAdapter;
    private HashMap<String, event> discardedEvents;
    private Paint p = new Paint();





//// TODO: 2016-12-04 will have to change into a listview, that is reactive to value listeners,
// so we can just keep track of a list isntead of writing to the event database
    //but may be working for now
    //// TODO: 2017-01-03 need to make everything reactive to events ending


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_feed);

        DateTime dt = new DateTime(System.currentTimeMillis());
        Log.e("dateTime", "" + dt.get(DateTimeFieldType.dayOfMonth()));
        Log.e("dateTime", "" + dt.getMillis());
        Log.e("timezone", "" + dt.getZone());

        String[] categoryList = new String[6];
        categoryList[0] = "Food";
        categoryList[1] = "Music";
        categoryList[2] = "Rec";
        categoryList[3] = "All";
        categoryList[4] = "Games";
        categoryList[5] = "Party";

        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);

        final LinearSnapHelper snapHelper = new LinearSnapHelper();



        scrollFilter = (RecyclerView) findViewById(R.id.scrollFilter);

        snapHelper.attachToRecyclerView(scrollFilter);

        filterAdapter = new CircularArrayAdapter(getApplicationContext(), categoryList);

        scrollFilter.setAdapter(filterAdapter);

        manager.scrollToPosition(Integer.MAX_VALUE/2);


        scrollFilter.setLayoutManager(manager);


        //initializes list view which holds each event in the feed
        mListView = (RecyclerView)findViewById(R.id.event_feed_listview);
        mListView.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mListView.setLayoutManager(layoutManager);



        //inits saved preference storage
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

        //holds
        eventList = new ArrayList<>();
        hashEventList = new HashMap<>();

        mRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        id = Profile.getCurrentProfile().getId();

        interested_events = "interested events";



        lat = settings.getFloat("lat", 0);
        lng = settings.getFloat("lng", 0);
        findViewById(R.id.event_feed_create_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDummyEvents();
            }
        });

        eventRef = FirebaseDatabase.getInstance().getReference().child("events");
        geofireRef = FirebaseDatabase.getInstance().getReference().child("eventLocation");
        geofire = new GeoFire(geofireRef);

        meventListAdapter = new eventListAdapter(eventRef, event.class, R.layout.event_feed_list_item, this, eventList);

        mListView.setAdapter(meventListAdapter);





        geoQueryBool = false;

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {

                    event discardedEvent = meventListAdapter.getItemAtPosition(position);

                    String event_id = discardedEvent.getId();

                    discardedEvents.put(event_id, discardedEvent);


                    mRef.child("public_profile").child(id).child("discarded_events").child(event_id).setValue(discardedEvent);
                    meventListAdapter.remove(event_id);

                } else {



                    event interestedEvent = meventListAdapter.getItemAtPosition(position);

                    String event_id = interestedEvent.getId();

                    eventKeys.add(event_id);
                    eventList.add(interestedEvent);

                    mRef.child("public_profile").child(id).child("interested_events").child(event_id).setValue(interestedEvent);
                    meventListAdapter.remove(event_id);


                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height/3;

                    if (dX > 0) {

                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);


                    } else {

                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() - 2*width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawRect(background, p);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mListView);

    }



    //// TODO: 2017-02-08 will be calling for interesting events
    @Override
    protected void onStart() {
        super.onStart();
        eventKeys = savedPreferences.getListString("interested_events_id", settings);

        eventList = savedPreferences.getListevents(interested_events, settings);


        Log.e("savedPreferences events", "" + eventList);
        hashEventList = savedPreferences.getListeventsHash(interested_events, settings);
        Log.e("eventKeys", "=" + eventKeys);

        if (geoQueryBool == false) {
            geoQuery = geofire.queryAtLocation(new GeoLocation(lat, lng), 20.0);


            geoListener = new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    DatabaseReference tempRef = eventRef.child(key);
                    tempRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = dataSnapshot.getKey();
                            if(!eventKeys.contains(key)) {
                                if (!meventListAdapter.exists(key)) {
                                    Log.e(TAG, "item added " + key);
                                    event addedEvent = meventListAdapter.addSingle(dataSnapshot);
                                    hashEventList.put(key, addedEvent);
                                    Log.e("addedEvent", "" + addedEvent.startTime());
                                    meventListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e(TAG, "item updated: " + key);
                                    meventListAdapter.update(dataSnapshot, key);
                                    meventListAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "cancled with error:" + databaseError.getMessage());
                        }
                    });
                }

                @Override
                public void onKeyExited(String key) {
                    Log.e(TAG, "event " + key + " is no longer is search area");
                    meventListAdapter.remove(key);
                    //isListEmpty();
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                }

                @Override
                public void onGeoQueryReady() {
                    Log.e(TAG, "all data is loaded and events fired");
                    //isListEmpty();

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            };
            geoQuery.addGeoQueryEventListener(geoListener);
            mListView.setAdapter(meventListAdapter);

//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    //// TODO: 2016-12-16 fix, adding event id to list, in on pause set it to sharedpreferences
//                    event newEvent = (event) adapterView.getItemAtPosition(i);
//                    String event_id = newEvent.getId();
//
//
//                    event realEvent = hashEventList.get(event_id);
//
//                    Log.e("newEvent", "" + newEvent.startTime());
//
//                    eventKeys.add(event_id);
//                    eventList.add(newEvent);
//
//
//                    mRef.child("public_profile").child(id).child("interested_events").child(event_id).setValue(newEvent);
//                    meventListAdapter.remove(event_id);
//                }
//            });
        }

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Item
//            }
//        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.eventfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.calendarMenu:
                putListEvent(interested_events, eventList);
                Intent intent = new Intent(this, calender_view_base.class);
                startActivity(intent);
                return true;
            case R.id.messagesMenu:
                Intent Messageintent = new Intent(this, MessageActivity.class);
                startActivity(Messageintent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause!!!");
        putListEvent(interested_events, eventList);
        geoQuery.removeAllListeners();

    }

    public void createDummyEvents() {
        DatabaseReference fakeEvents = FirebaseDatabase.getInstance().getReference().child("events");
        DatabaseReference fakeLocation = FirebaseDatabase.getInstance().getReference().child("eventLocation");
        GeoFire geofire = new GeoFire(fakeLocation);
        ArrayList<String> friendList = new ArrayList<>();
        DateTime startCal = new DateTime(System.currentTimeMillis());
        Log.e("startCal", " " + startCal.getDayOfYear());


        ArrayList<String> titles = new ArrayList<>(Arrays.asList("BeerPong HouseParty", "Current Joys Playing @ Logan's", "Grab Lunch?", "Boy's Night In!", "2 Nights in Alice", "Games Nights at the Chews Place",
                "No One Loves You But That's OK", "Swimming in a Lake with Jake", "Kid Cudi Showing his Dick for No Reason", "Bread Can't Cure Saddness",
                "George's Lit Funeral", "Human Hunting at Dusk", "Back to the Future Marathon", "Reggae Gay Olympics", "Kool-ade Doomsday Party",
                "Lonely Hearts Circle Jerk", "Bumper Bots on Alta Lake!", "Farmer's Market", "No Shame No Name", "Spin Class for Seniors",
                "Racists Against Racists March"));


        ArrayList<String> categories = new ArrayList<>(Arrays.asList("Recreation", "Food", "Party", "Games", "Music"));

        int i = (int) Math.round(Math.random() * (titles.size() - 1));
        int j = (int) Math.round(Math.random() * (categories.size() - 1));


        double tempLat = lat;
        double tempLon = lng;
        DateTime endCal = new DateTime(System.currentTimeMillis() + 1000000000);
        //endCal.plusDays(1);
        Log.e("endcal", " " + endCal.getDayOfYear());
        String eventId = fakeEvents.push().getKey();
        event happening = new event("jordan", titles.get(i), "always singing", categories.get(j), friendList, eventId,
                startCal.getMillis(), startCal.getMillis());

        Log.e("event startTime", " " + happening.startTime());


        happening.setId(eventId);
        happening.setTitle(titles.get(i));
        happening.setDescription("party Time");

        geofire.setLocation(eventId, new GeoLocation(tempLat, tempLon));
        fakeEvents.child(eventId).setValue(happening);
        hashEventList.put(eventId, happening);



        DateTime newDate = startCal.plusDays(1);

        eventId = fakeEvents.push().getKey();
        i = (int) Math.round(Math.random() * (titles.size() - 1));
        j = (int) Math.round(Math.random() * (categories.size() - 1));
        happening = new event("jordan", titles.get(i), "always singing", categories.get(j), friendList, eventId,
                newDate.getMillis(), newDate.getMillis());

        Log.e("event startTime", " " + happening.startTime());


        happening.setId(eventId);
        happening.setTitle(titles.get(i));
        happening.setDescription("party Time");

        geofire.setLocation(eventId, new GeoLocation(tempLat, tempLon));
        fakeEvents.child(eventId).setValue(happening);
        hashEventList.put(eventId, happening);

        DateTime newDate2 = startCal.plusDays(2);

        for (int u = 0; u < 8; u++) {

            i = (int) Math.round(Math.random() * (titles.size() - 1));
            j = (int) Math.round(Math.random() * (categories.size() - 1));

            eventId = fakeEvents.push().getKey();
            happening = new event("jordan", titles.get(i), "always singing", categories.get(j), friendList, eventId,
                    newDate2.getMillis(), newDate2.getMillis());

            Log.e("event startTime", " " + happening.startTime());


            happening.setId(eventId);
            happening.setTitle(titles.get(i));
            happening.setDescription("party Time");

            geofire.setLocation(eventId, new GeoLocation(tempLat, tempLon));
            fakeEvents.child(eventId).setValue(happening);
            hashEventList.put(eventId, happening);

        }

        i = (int) Math.round(Math.random() * (titles.size() - 1));
        j = (int) Math.round(Math.random() * (categories.size() - 1));

        DateTime newDate3 = startCal.plusDays(3);

        eventId = fakeEvents.push().getKey();
        happening = new event("jordan", titles.get(i), "always singing", categories.get(j), friendList, eventId,
                newDate3.getMillis(), newDate3.getMillis());

        Log.e("event startTime", " " + happening.startTime());


        happening.setId(eventId);
        happening.setTitle(titles.get(i));
        happening.setDescription("party Time");

        geofire.setLocation(eventId, new GeoLocation(tempLat, tempLon));
        fakeEvents.child(eventId).setValue(happening);
        hashEventList.put(eventId, happening);

        i = (int) Math.round(Math.random() * (titles.size() - 1));
        j = (int) Math.round(Math.random() * (categories.size() - 1));


        eventId = fakeEvents.push().getKey();
        happening = new event("jordan", titles.get(i), "always singing", categories.get(j), friendList, eventId,
                endCal.getMillis(), endCal.getMillis());

        Log.e("event startTime", " " + happening.startTime());


        happening.setId(eventId);
        happening.setTitle(titles.get(i));
        happening.setDescription("party Time");

        geofire.setLocation(eventId, new GeoLocation(tempLat, tempLon));
        fakeEvents.child(eventId).setValue(happening);
        hashEventList.put(eventId, happening);





    }




    public void putListEvent(String key, List<event> stringList) {
        Gson gson = new Gson();
        String jsonEvents = gson.toJson(stringList);

        editor.putString(key, jsonEvents);
        editor.commit();
    }

    public void putListString(String key, List<String> stringList) {
        Gson gson = new Gson();
        String jsonEvents = gson.toJson(stringList);
        editor.putString(key, jsonEvents);
        editor.commit();
    }

    public void checkForNull(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }
}

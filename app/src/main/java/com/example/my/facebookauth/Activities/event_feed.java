package com.example.my.facebookauth.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.event;
import com.example.my.facebookauth.models.eventListAdapter;
import com.facebook.Profile;
import com.firebase.client.AuthData;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.list;

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
    private ListView mListView;
    private boolean geoQueryBool;
    private GeoQueryEventListener geoListener;
    private DatabaseReference mRef;
    private String id;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;
    private List<String> eventKeys;
    private String interested_events;


//// TODO: 2016-12-04 will have to change into a listview, that is reactive to value listeners,
// so we can just keep track of a list isntead of writing to the event database
    //but may be working for now
    //// TODO: 2017-01-03 need to make everything reactive to events ending


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_feed);

        //initializes list view which holds each event in the feed
        mListView = (ListView) findViewById(R.id.event_feed_listview);

        //inits saved preference storage
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

        //holds
        eventList = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        id = Profile.getCurrentProfile().getId();

        interested_events = "interested events";



        lat = 37.4219983333333335;
        lng = -122.0840000000000002;
        findViewById(R.id.event_feed_create_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDummyEvents();
            }
        });

        eventRef = FirebaseDatabase.getInstance().getReference().child("events");
        geofireRef = FirebaseDatabase.getInstance().getReference().child("eventLocation");
        geofire = new GeoFire(geofireRef);
        meventListAdapter = new eventListAdapter(eventRef, event.class, R.layout.event_feed_list_item, this);
        geoQueryBool = false;

    }

    @Override
    protected void onStart() {
        super.onStart();
        eventKeys = getListString("interested_events_id");
        eventList = getListevents(interested_events);
        Log.e("eventKeys", "=" + eventKeys);

        if (geoQueryBool == false) {
            geoQuery = geofire.queryAtLocation(new GeoLocation(lat, lng), 1.0);


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
                                    meventListAdapter.addSingle(dataSnapshot);
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
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //// TODO: 2016-12-16 fix, adding event id to list, in on pause set it to sharedpreferences
                    event newEvent = (event) adapterView.getItemAtPosition(i);
                    String event_id = newEvent.getId();
                    eventKeys.add(event_id);
                    eventList.add(newEvent);
                    //setDataAsArray(eventKeys);
                    for (String key : eventKeys) {
                        Log.e("OnClick", "" + key);
                    }

                    mRef.child("public_profile").child(id).child("interested_events").child(event_id).setValue(newEvent);
                    meventListAdapter.remove(event_id);
                }
            });
        }

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Item
//            }
//        });
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
        event happening = new event("jordan", "kiki is singing", "always singing", "houseLife", friendList);
        for (int i = 0; i < 4; i++) {

            double latRan = 0;
            double lngRan = 0;
            double tempLat = lat + latRan;
            double tempLon = lng + lngRan;

            String eventId = fakeEvents.push().getKey();
            happening.setId(eventId);
            happening.setTitle(eventId);
            happening.setDescription("lat + " + latRan + " long + " + lngRan);

            geofire.setLocation(eventId, new GeoLocation(tempLat, tempLon));
            fakeEvents.child(eventId).setValue(happening);


        }
    }

    public List<event> getListevents(String key) {
        Gson gson = new Gson();
        String json = settings.getString(key, "");
        Type type = new TypeToken<List<event>>(){}.getType();
        List<event> eventList = gson.fromJson(json, type);
        return eventList;
        //return new ArrayList<String>(Arrays.asList(TextUtils.split(settings.getString(key, ""), "‚‗‚")));

    }

    public List<String> getListString(String key) {
        Gson gson = new Gson();
        String json = settings.getString(key, "");
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> list = gson.fromJson(json, type);
        return list;

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

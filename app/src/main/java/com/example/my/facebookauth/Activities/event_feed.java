package com.example.my.facebookauth.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.event;
import com.example.my.facebookauth.models.eventListAdapter;
import com.example.my.facebookauth.utilities.ObjectSerializer;
import com.facebook.Profile;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.attr.id;
import static android.R.attr.key;

/**
 * Created by Owner on 2016-11-12.
 */

public class event_feed extends AppCompatActivity {

    private String TAG = event_feed.class.getSimpleName();
    private FirebaseAuth mAuth;
    private ArrayList<event> eventList;
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
    private int interested_events;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;
    private List<String> eventKeys;



//// TODO: 2016-12-04 will have to change into a listview, that is reactive to value listeners,
// so we can just keep track of a list isntead of writing to the event database
    //but may be working for now



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_feed);
        mListView = (ListView) findViewById(R.id.event_feed_listview);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        eventList = new ArrayList<>();
        mContext = this;
        mRef = FirebaseDatabase.getInstance().getReference();
        id = Profile.getCurrentProfile().getId();




       
        //// TODO: 2016-11-25 im a bad boy, cardcoded to test, lat and long needs to be put into savedpreferences 
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
        settings = getSharedPreferences("event_preferences", 0);
        interested_events = settings.getInt("interested_events", 0);
        Log.e("onStart: ", "interested events: " + interested_events);
        editor = settings.edit();
        //eventKeys = getDataAsArray();

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

//                            if(!eventKeys.contains(key)) {
                                if (!meventListAdapter.exists(key)) {
                                    Log.e(TAG, "item added " + key);
                                    meventListAdapter.addSingle(dataSnapshot);
                                    meventListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e(TAG, "item updated: " + key);
                                    meventListAdapter.update(dataSnapshot, key);
                                    meventListAdapter.notifyDataSetChanged();
                                }
                            //}
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

//    private List<String> getDataFromSharedPreferences() {
//        Gson gson = new Gson();
//        List<String> eventKeysFromShared = new ArrayList<>();
//        SharedPreferences sharedPref = getSharedPreferences("event_preferences", 0);
//        String jsonPreference = sharedPref.getString("interested_events", "");
//
//        Type type = new TypeToken<List<String>>() {}.getType();
//        eventKeysFromShared = gson.fromJson(jsonPreference, type);
//
//        return eventKeysFromShared;
//    }
//
//    private void setDataFromSharedPreferences(String key) {
//        Gson gson = new Gson();
//        String jsonCurProduct = gson.toJson(key);
//
//        SharedPreferences sharedPref = getSharedPreferences("event_preferences", 0);
//        SharedPreferences.Editor editor = sharedPref.edit();
//
//        editor.putString("interested_events", jsonCurProduct);
//        editor.commit();
//    }
//
//    private void setDataAsArray(List eventList) {
//        SharedPreferences.Editor editor = settings.edit();
//        Set<String> eventSet = new HashSet<String>(eventList);
//        editor.putStringSet("interested_events", eventSet);
//        editor.commit();
//    }
//
//    //// TODO: 2016-12-16 cant turn list into set;
//    private List<String> getDataAsArray() {
//        Set<String> eventSet = settings.getStringSet("interested_events", new HashSet<String>());
//        List<String> eventList = new ArrayList<String>(eventSet);
//        return eventList;
//    }
}

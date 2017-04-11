package com.example.my.facebookauth.Activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.event;
import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;



/**
 * Created by Owner on 2016-12-04.
 */

public class CalendarActivity extends AppCompatActivity {

    private ArrayList<event> eventList;
    private DatabaseReference mRef;
    private SharedPreferences settings;
    private int interested_events;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_calendar);
        settings = getSharedPreferences("event_preferences", 0);
        editor = settings.edit();
        interested_events = settings.getInt("interested_events", 0);
        String id = Profile.getCurrentProfile().getId();
        eventList = new ArrayList<>();
        //todo store interested events in saved preferences if possible
        mRef = FirebaseDatabase.getInstance().getReference().child("public_profile").child(id).child("interested_events");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                event newEvent = dataSnapshot.getValue(event.class);
                eventList.add(newEvent);


            }

            //// TODO: 2016-12-15 cant leave these blank
            // each of these must call a function that redraws the calendar events
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                event removedEvent = dataSnapshot.getValue(event.class);
                eventList.remove(removedEvent);
                Log.e("onChildRemoved", "number of interested events = " + eventList.size());
                interested_events -= 1;
                editor.putInt("interested_events", interested_events);
                editor.commit();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.addChildEventListener(childEventListener);

    }
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                event newEvent = dataSnapshot.getValue(event.class));
//
//                for (int i = 0; i < eventList.size(); i++) {
//                    Log.e("eventsListener: ", "" + eventList.get(i).getTitle());
//                }
//                bus.post(eventList);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("calendar :", "Error, eventsListener canceled");
//            }
//        };
//        mRef.addValueEventListener(eventsListener);
//
//        Log.e("onCreate: ", "i should be called first");



    @Override
    protected void onStart() {
        super.onStart();

        Log.e("calendarActivity: ", "" + interested_events);
    }
}

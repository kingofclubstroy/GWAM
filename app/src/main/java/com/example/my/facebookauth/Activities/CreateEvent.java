package com.example.my.facebookauth.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.User;
import com.example.my.facebookauth.models.event;
import com.example.my.facebookauth.models.location;
import com.facebook.Profile;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Owner on 2016-11-22.
 */
//// TODO: 2017-01-03 need to add dropdowns to set start and end time and convert those times to UTC 
public class CreateEvent extends AppCompatActivity {


    private String name;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editCategory;
    private Button inviteFriends;
    private Button createEvent;
    private DatabaseReference mRef;
    private String id;
    private ArrayList<String> invites;
    private String description;
    private String category;
    private String title;
    private DatabaseReference profileRef;
    private GeoFire geoFire;
    private DatabaseReference eventLocation;
    private FirebaseAuth mAuth;
    private String uid;
    private double lat;
    private double lng;
    String wheelMenu1[] = new String[]{"name 1", "name2", "name3", "name4"};
    String wheelMenu2[] = new String[]{"age1", "age2", "age3"};
    String wheelMenu3[] = new String[]{"10", "20", "30"};

    // Wheel scrolled flag
    private boolean wheelScrolled = false;

    private TextView text;
    private EditText text1;
    private EditText text2;
    private EditText text3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        id = Profile.getCurrentProfile().getId();
        Intent intent = getIntent();
        invites = (ArrayList<String>) intent.getSerializableExtra("friendList");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("events");
        eventLocation = FirebaseDatabase.getInstance().getReference().child("eventLocation");
        geoFire = new GeoFire(eventLocation);

        editTitle = (EditText) findViewById(R.id.editTitle);
        editCategory = (EditText) findViewById(R.id.editCategory);
        editDescription = (EditText) findViewById(R.id.editdescription);
        inviteFriends = (Button) findViewById(R.id.inviteFriends);
        createEvent = (Button) findViewById(R.id.create_event_complete);


        profileRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);







        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), inviteFriendsActivity.class);
                startActivity(intent);
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTheEvent();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //// TODO: 2016-11-24 name name and everything in saved preferences, too many calls to database for stupid shit
        ValueEventListener nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ValueEventListener locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location loc = dataSnapshot.getValue(location.class);
                lat = loc.getLat();
                lng = loc.getLon();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        profileRef.child("info").addValueEventListener(nameListener);
        profileRef.child("location").addValueEventListener(locationListener);
    }

    public void createTheEvent() {
        String eventId = mRef.push().getKey();
        title = editTitle.getText().toString();
        description = editDescription.getText().toString();
        category = editCategory.getText().toString();
        if (title.equals("") || description.equals("") || category.equals("")) {
            Toast.makeText(this, "need to fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        event createdEvent = new event(name, title, description, category, invites);

        mRef.child(eventId).setValue(createdEvent);
        geoFire.setLocation(eventId, new GeoLocation(lat, lng));
        editTitle.setText("");
        editCategory.setText("");
        editDescription.setText("");
        invites = new ArrayList<>();
    }
}

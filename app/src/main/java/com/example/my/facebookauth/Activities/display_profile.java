package com.example.my.facebookauth.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.User;
import com.example.my.facebookauth.utilities.ImageLoadTask;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Owner on 2016-11-13.
 */

public class display_profile extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "facebookAuth";
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private TextView displayName;
    private String uid;
    private Button button;
    private double lat, lng;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_profile);

        profilePic = (ImageView) findViewById(R.id.profile_pic);
        displayName = (TextView) findViewById(R.id.display_name);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        uid = mAuth.getCurrentUser().getUid();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        findViewById(R.id.eventFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), event_feed.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), getLocationActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.calenderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        DatabaseReference userRef = mRef.child("users").child(uid);


        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Log.e(TAG, " " + user);
                    Log.e(TAG, "" + user.getName() + user.getPhoto() + user.getEmail());
                    String imageUrl = user.getPhoto();
                    new ImageLoadTask(imageUrl, profilePic).execute();
                    String name = user.getName();
                    displayName.setText(name);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "profileListener: cancelled");
            }
        };

        userRef.child("info").addValueEventListener(profileListener);


    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.button:
                signOut();
                break;
            case R.id.friendsButton:
                showFriends();
                break;
            case R.id.create_event:
                createEvent();
                break;
        }
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        startActivity(intent);
    }

    //// TODO: 2016-11-17 not permanent
    public void showFriends() {
        Intent intent = new Intent(this, FriendsList.class);
        startActivity(intent);
    }

    public void createEvent() {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }


}

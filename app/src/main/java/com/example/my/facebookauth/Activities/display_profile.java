package com.example.my.facebookauth.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.User;
import com.example.my.facebookauth.models.event;
import com.example.my.facebookauth.utilities.ImageLoadTask;
import com.example.my.facebookauth.utilities.savedPreferences;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 2016-11-13.
 */
//This activity is the temporary functional hub for testing, and the first activity if signed on
//// TODO: 2017-01-03 gut this whole thing, initial activity will be event feed in the final shit

public class display_profile extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "facebookAuth";
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private TextView displayName;
    private String uid;
    private Button button, scrollButton;
    private double lat, lng;
    private List<event> interestedEventList;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String interested_events = "interested events";
    private List<String> interestedEventListId;
    private String interested_events_id = "interested_events_id";
    private DatabaseReference userRef;
    DatabaseReference publicRef;
    private Context context;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_profile);

        //sets up shared preference instance to safe information recieved here forother activities to access
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

        //stores full object data of events the user is interested in
        interestedEventList = new ArrayList<>();

        //stores only the unique id of interested events, for quicker lookup/check
        interestedEventListId = new ArrayList<>();

        //set up views
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        displayName = (TextView) findViewById(R.id.display_name);

        setUpButtons();

        //get private account id
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        //set up parent database reference
        mRef = FirebaseDatabase.getInstance().getReference();

        //private database reference
        userRef = mRef.child("users").child(uid);

        //public database reference
        publicRef = mRef.child("public_profile").child(Profile.getCurrentProfile().getId());

        privateProfileListener();

        publicProfileListener();

        context = getApplicationContext();

    }

    //does other buttons, sloppy but not permanent
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

    /**
     * signs out of current account and sends back to login page
     */
    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        startActivity(intent);
    }

    //// TODO: 2016-11-17 not permanent

    /**
     * goes to friend page
     */
    public void showFriends() {
        Intent intent = new Intent(this, FriendsList.class);
        startActivity(intent);
    }

    /**
     * goes to create event page
     */
    public void createEvent() {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }


    //todo is sloppy but will be entirely replaced, so tollerable for now
    /**
     * creates buttons and sets up the on click functionality
     */
    public void setUpButtons() {
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        eventFeed();

        getLocationActivity();

        calanderSetUp();

        //remove
        scrollViewSetUp();

        emojiKeyboard();
    }

    /**
     * recieves the profile picture and public name from the database at sets to appropriate views
     */
    public void privateProfileListener() {

        //looks at the value contained at the database reference supplied to it
        ValueEventListener profileListener = new ValueEventListener() {

            //runs for every value within, and when any changes
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String imageUrl = user.getPhoto();
                    //
                    new ImageLoadTask(imageUrl, profilePic).execute();
                    String name = user.getName();
                    displayName.setText(name);
                }

            }

            //// TODO: 2017-01-06 send user an error, check if the problem is internet connection
            //error if something goes wrong
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "profileListener: cancelled");
            }
        };

        //sets the listener to the database reference
        userRef.child("info").addValueEventListener(profileListener);
    }

    /**
     * Sets up event feed button and stores necessary data
     */
    public void eventFeed() {
        findViewById(R.id.eventFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 2017-01-06  will probably store this data onPause so there is no issue transfering from every view
                savedPreferences.putListString(interested_events_id, interestedEventListId, editor);
                savedPreferences.putListEvent(interested_events, interestedEventList,editor);

                Intent intent = new Intent(getApplicationContext(), event_feed.class);
                startActivity(intent);
            }
        });
    }


    //// TODO: This will be removed, will set up automatically with no activity
    /**
     * sets up location button to send to location activity
     */
    public void getLocationActivity(){

        findViewById(R.id.location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), getLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * sets up calander button
     */
    public void calanderSetUp() {

        findViewById(R.id.calenderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), calendar_view_activity.class);
                startActivity(intent);
            }
        });
    }

    public void scrollViewSetUp() {

//        new SingleDateAndTimePickerDialog.Builder(context)
//                //.bottomSheet()
//                //.curved()
//                //.minutesStep(15)
//                .title("Simple")
//                .listener(new SingleDateAndTimePickerDialog.Listener() {
//                    @Override
//                    public void onDateSelected(Date date) {
//
//                    }
//                }).display();
        findViewById(R.id.scrollviewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), scrollViewActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * gets event data from public profile to be stored later
     */
    public void publicProfileListener() {

        // child event listeners take all the children from the specified database reference
        ChildEventListener publicEventListener = new ChildEventListener() {

            //runs once for every child, and every time a child is added
            //gets interested event data and adds it lists
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                event newevent = dataSnapshot.getValue(event.class);
                interestedEventListId.add(newevent.getId());
                interestedEventList.add(newevent);

            }
            //runs everytime a child is changed
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //// TODO: 2017-01-03 add
            }

            //everytime child is removed
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //// TODO: 2017-01-03 add if works
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            //// TODO: 2017-01-06 maybe more of an error report
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("child listener", "canceled");
            }
        };

        //this is the database reference the listener run on
        publicRef.child("interested_events").addChildEventListener(publicEventListener);
    }

    public void emojiKeyboard() {
        findViewById(R.id.emojiKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), keyboardActivity.class);
                startActivity(intent);
            }
        });
    }
}

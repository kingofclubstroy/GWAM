package com.example.my.facebookauth.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.User;
import com.example.my.facebookauth.models.privateAccessMessages;
import com.example.my.facebookauth.models.user_messages;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.id;


/**
 * Created by Owner on 2016-11-17.
 */

public class MessageActivity extends AppCompatActivity {

    private String TAG = "facebookAuth";
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private String id;
    private String FriendId;
    private String profileId;
    public String primaryId;
    public String secondrayId;
    public DatabaseReference mMessageRef;
    private ListView mListView;
    private EditText mEditText;
    private String FriendName;
    private String UserName;
    private Button mMessageButton;
    private DatabaseReference mUserRef;
    private DatabaseReference mFriendMessageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

        mAuth = FirebaseAuth.getInstance();
        profileId = Profile.getCurrentProfile().getId();

        id = Profile.getCurrentProfile().getId();
        Intent getintent = getIntent();
        FriendId = getintent.getStringExtra("id");
        FriendName = getintent.getStringExtra("name");
        mListView = (ListView) findViewById(R.id.messages_listView);
        mEditText = (EditText) findViewById(R.id.messages_EditText);
        findViewById(R.id.messages_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        mUserRef = FirebaseDatabase.getInstance().getReference().child("public_profile").child(id).child("info");

        ValueEventListener userNamelistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                UserName = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "error: " + databaseError);
            }
        };
        mUserRef.addValueEventListener(userNamelistener);


        mRef = FirebaseDatabase.getInstance().getReference().child("public_profile").child(id).child("messages").child(FriendId);
        mFriendMessageRef = FirebaseDatabase.getInstance().getReference().child("public_profile").child(FriendId).child("messages").child(id);
        //// TODO: 2016-11-17 change to childevent listener if applicable 
        final ValueEventListener messageValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    privateAccessMessages privateMessage = dataSnapshot.getValue(privateAccessMessages.class);
                    if (privateMessage.getPrimary() == true) {
                        primaryId = profileId;
                        secondrayId = FriendId;
                        Log.e(TAG, "shit called itself!");
                        mMessageRef = FirebaseDatabase.getInstance().getReference().child("user_messages").child(primaryId).child(secondrayId);
                        setupListView();
                    } else {
                        primaryId = FriendId;
                        secondrayId = profileId;
                        mMessageRef = FirebaseDatabase.getInstance().getReference().child("user_messages").child(primaryId).child(secondrayId);
                        setupListView();
                    }
                } else {

                    privateAccessMessages privatemessage = new privateAccessMessages(true, true);
                    privateAccessMessages friendPrivateMessage = new privateAccessMessages(false, true);
                    mRef.setValue(privatemessage);
                    mFriendMessageRef.setValue(friendPrivateMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "error: " + databaseError);
            }


        };
        Log.e(TAG, "this should be the message ref" + mMessageRef);
        Log.e(TAG, "primaryID: " + primaryId);
        Log.e(TAG, "secondaryID: " + primaryId);
        mRef.addValueEventListener(messageValue);

        //// TODO: 2016-11-17 finish this shit
        //// TODO: 2016-11-27 figure out what i meant by this 
    }

    public void setupListView() {

        Log.e(TAG, "mMessageRef: " + mMessageRef);

        FirebaseListAdapter<user_messages> firebaseListAdapter = new FirebaseListAdapter<user_messages>(
                this,
                user_messages.class,
                R.layout.message_item,
                mMessageRef
        ) {
            @Override
            protected void populateView(View v, user_messages model, int position) {
                TextView username = (TextView) v.findViewById(R.id.display_name);
                TextView text = (TextView) v.findViewById(R.id.body);
                username.setText(model.getFrom());
                text.setText(model.getBody());

            }
        };
        mListView.setAdapter(firebaseListAdapter);

    }


    public void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messages_EditText);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            user_messages message = new user_messages(UserName, input);
            mMessageRef.push().setValue(message);
            inputText.setText("");
        }
    }
}

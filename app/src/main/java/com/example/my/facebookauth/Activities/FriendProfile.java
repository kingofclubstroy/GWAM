package com.example.my.facebookauth.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.User;
import com.example.my.facebookauth.utilities.ImageLoadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Owner on 2016-11-17.
 */

public class FriendProfile extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "facebookAuth";
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private TextView name;
    private ImageView photo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_profile);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        mRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userProfile = mRef.child("public_profile").child(id).child("info");

        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String nameText = user.getName();
                    name = (TextView) findViewById(R.id.name);
                    name.setText(nameText);
                    photo = (ImageView) findViewById(R.id.photo);
                    String imageUrl = user.getPhoto();
                    new ImageLoadTask(imageUrl, photo).execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userProfile.addValueEventListener(profileListener);


    }

    public void message() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Intent MessageIntent = new Intent(this, MessageActivity.class);
        MessageIntent.putExtra("id", id );
        startActivity(MessageIntent);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.messageButton) {
            message();
        }
    }
}

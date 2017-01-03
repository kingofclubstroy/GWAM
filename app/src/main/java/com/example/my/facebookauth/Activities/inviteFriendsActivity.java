package com.example.my.facebookauth.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.my.facebookauth.Activities.CreateEvent;
import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.friend;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Owner on 2016-11-22.
 */

public class inviteFriendsActivity extends AppCompatActivity {

    private String id;
    private DatabaseReference mRef;
    private ListView invite_friends;
    private ArrayList<String> invited_friends;
    Button invite_button;
    private DatabaseReference inviteRef;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends);

        id = Profile.getCurrentProfile().getId();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("friends").child(id);
        invite_friends = (ListView) findViewById(R.id.invite_listview);
        invited_friends = new ArrayList<>();
        invite_button = (Button) findViewById(R.id.send_invite_button);



        FirebaseListAdapter<friend> firebaseListAdapter = new FirebaseListAdapter<friend>(
                this,
                friend.class,
                R.layout.invite_friends_item,
                mRef
        ) {
            @Override
            protected void populateView(View v, friend model, int position) {
                TextView textView = (TextView) v.findViewById(R.id.invite_name);
                textView.setText(model.getName());
            }
        };

        invite_friends.setAdapter(firebaseListAdapter);
        invite_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                friend user = (friend) adapterView.getItemAtPosition(i);
                String userId = user.getId();
                Log.e("facebookAuth", "userId: " + userId);
                if (!invited_friends.contains(userId)) {
                    view.setBackgroundColor(getColor(R.color.colorAccent));
                    invited_friends.add(userId);
                }
                else {
                    view.setBackgroundColor(getColor(R.color.white));
                    invited_friends.remove(userId);
                }

            }
        });

        invite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateEvent.class);
                intent.putExtra("friendList", invited_friends);
                startActivity(intent);
            }
        });


    }
}

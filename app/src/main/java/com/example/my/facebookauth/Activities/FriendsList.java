package com.example.my.facebookauth.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.my.facebookauth.Activities.FriendProfile;
import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.friend;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Owner on 2016-11-17.
 */

public class FriendsList extends AppCompatActivity {

    private String TAG = "facebookAuth";
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private String id;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://facebookauth-2483e.firebaseio.com/friends");

        mAuth = FirebaseAuth.getInstance();
        id = Profile.getCurrentProfile().getId();
        DatabaseReference friendRef = mRef.child(id);

        mListView = (ListView) findViewById(R.id.listView);

        Log.e(TAG, "" + id);

        FirebaseListAdapter<friend> firebaseListAdapter = new FirebaseListAdapter<friend>(
                this,
                friend.class,
                android.R.layout.simple_list_item_1,
                friendRef
        ) {
            @Override
            protected void populateView(View v, friend model, int position) {

                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model.getName());
            }

        };

        mListView.setAdapter(firebaseListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                friend user = (friend) adapterView.getItemAtPosition(i);
                String id = user.getId();
                String name = user.getName();
                Intent intent = new Intent(getApplicationContext(), FriendProfile.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });


    }
}

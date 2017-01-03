package com.example.my.facebookauth.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.User;
import com.example.my.facebookauth.models.friend;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class FacebookLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "FacebookLogin";
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    public User user;

    private ProgressDialog mProgressDialog;

    private Firebase mDatabase;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_facebook_login);

        //Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        findViewById(R.id.button_facebook_signout).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = new Firebase("https://facebookauth-2483e.firebaseio.com/");

        FirebaseUser Fb_user = FirebaseAuth.getInstance().getCurrentUser();
        if (Fb_user != null) {
            //user is signed in, send to event feed
            //// TODO: 2016-11-18 this

        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser muser = firebaseAuth.getCurrentUser();
                if (muser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + muser.getUid());

                    Intent intent = new Intent(getApplicationContext(), display_profile.class);

                    startActivity(intent);
                }
                else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

                updateUI(muser);
            }
        };

        // Initialize facebook login button
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" +loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                getLoginDetails(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        final String uid = task.getResult().getUser().getUid();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }

                        else {
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();
                            String image = task.getResult().getUser().getPhotoUrl().toString();
                            String id = Profile.getCurrentProfile().getId();


                            user = new User(uid, name, null, email, null, image, true, id);
                            mDatabase.child("users").child(uid).child("info").setValue(user);
                            mDatabase.child("public_profile").child(id).child("info").setValue(user);
//                            GraphRequestBatch batch = new GraphRequestBatch(
//                                    GraphRequest.newMyFriendsRequest(
//                                            AccessToken.getCurrentAccessToken(),
//                                            new GraphRequest.GraphJSONArrayCallback() {
//                                                @Override
//                                                public void onCompleted(
//                                                        JSONArray jsonArray,
//                                                        GraphResponse response) {
//                                                    // Application code for users friends
//
//                                                    try {
//
//                                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                                            JSONObject jsondataArray = jsonArray.getJSONObject(0);
//                                                            String nameArray = jsondataArray.getString("name");
//                                                            String idArray = jsondataArray.getString("id");
//
//                                                        }
//
//
//
//
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            })
//
//                            );
//                            batch.addCallback(new GraphRequestBatch.Callback() {
//                                @Override
//                                public void onBatchCompleted(GraphRequestBatch graphRequests) {
//                                    // Application code for when the batch finishes
//                                }
//                            });
//                            batch.executeAsync();
//
//                            Bundle parameters = new Bundle();
//                            parameters.putString("fields", "id,name,link,picture");
////
                       }

                        hideProgressDialog();
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
            mDetailTextView.setText(getString(R.string.facebook_status_fmt, user.getUid()));

            findViewById(R.id.button_facebook_login).setVisibility(View.GONE);
            findViewById(R.id.button_facebook_signout).setVisibility(View.VISIBLE);
        }
        else {
            mStatusTextView.setText(R.string.sign_out);
            mDetailTextView.setText(null);

            findViewById(R.id.button_facebook_login).setVisibility(View.VISIBLE);
            findViewById(R.id.button_facebook_signout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.button_facebook_signout) {
            signOut();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    protected void getLoginDetails(AccessToken accessToken) {
        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                accessToken,
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        String uid = Profile.getCurrentProfile().getId();
                        try {
                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            Log.e(TAG, "data length = " + data.length());
                            Log.e(TAG, "data: " + data);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject friendData = data.getJSONObject(i);
                                String name = friendData.getString("name");
                                String id = friendData.getString("id");
                                Log.e(TAG, "friendData: " + friendData);
                                Log.e(TAG, "name: " + name);
                                Log.e(TAG, "id: " + id);
                                friend newfriend = new friend(name, id);
                                //// TODO: 2016-11-17 fix this, dont think its right, but can move on as is
                                mDatabase.child("friends").child(uid).child(id).setValue(newfriend);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}

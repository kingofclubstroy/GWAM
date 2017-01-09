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

//this is the first activity to run if they are not logged on, sign into facebook, and get relivent information
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

    private FirebaseUser Fb_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facebookSDKInitialize();

        setContentView(R.layout.activity_facebook_login);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = new Firebase("https://facebookauth-2483e.firebaseio.com/");

        Fb_user = FirebaseAuth.getInstance().getCurrentUser();

        setUpViews();

        userChangedListener();


        facebookLoginButton();
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

    /**
     * tells facebook what to do after login result
     * @param requestCode code detailing what is requested
     * @param resultCode shows if accepted or rejected
     * @param data data recieved from facebook
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * signs into facebook and gets personal info, like profile picture and name and saves to database
     * @param token token indicating logged on user
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }

                        else {

                            saveUserToDatabase(task);

                       }

                        hideProgressDialog();
                    }
                });
    }

    /**
     * logs out of facebook and takes back to sign in page
     */
    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    /**
     * shows the proper ui, depending on if signed in or not
     * @param user firebase user
     */
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
        //signs out on click
        if (i == R.id.button_facebook_signout) {
            signOut();
        }
    }

    /**
     * shows a loading spinner
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    /**
     * hides loading spinner
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * initializes facebook sdk
     */
    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    /**
     * Gets facebook info from the logged in user, async task
     * @param accessToken token indicating user logged in
     */
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
                                mDatabase.child("friends").child(uid).child(id).setValue(newfriend);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     * initializes views and buttons
     */
    public void setUpViews() {
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        findViewById(R.id.button_facebook_signout).setOnClickListener(this);
    }

    /**
     * sets up login button to sign into facebook
     */
    public void facebookLoginButton() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);

        //asks to recieve profile, email and friends from facebook on login
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

        //sets up callback to login button and gets details
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

    /**
     * saves user data to database
     * @param task login task for facebook
     */
    public void saveUserToDatabase(Task<AuthResult> task) {

        String name = task.getResult().getUser().getDisplayName();
        String email = task.getResult().getUser().getEmail();
        String image = task.getResult().getUser().getPhotoUrl().toString();
        String id = Profile.getCurrentProfile().getId();
        final String uid = task.getResult().getUser().getUid();


        user = new User(uid, name, null, email, null, image, true, id);
        mDatabase.child("users").child(uid).child("info").setValue(user);
        mDatabase.child("public_profile").child(id).child("info").setValue(user);
    }


    /**
     * sets up listener that runs if firebase user has changed, and either sends to profile page if signed in or shows login page if signed out
     */
    public void userChangedListener() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser muser = firebaseAuth.getCurrentUser();
                if (muser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + muser.getUid());

                    //send to display profile activity
                    Intent intent = new Intent(getApplicationContext(), display_profile.class);

                    startActivity(intent);
                }
                else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                //pretty redundant, but will send to login page
                updateUI(muser);
            }
        };
    }
}

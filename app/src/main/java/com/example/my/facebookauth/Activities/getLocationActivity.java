package com.example.my.facebookauth.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Owner on 2016-11-18.
 */

public class getLocationActivity extends AppCompatActivity {

    LocationManager locationManager;
    double longitudeBest, laditudeBest;
    double longitudeGPS, laditudeGPS;
    double longitudeNetwork, latitudeNetwork;
    String locationLocalityGPS;
    TextView longitudeValueBest, latitudeValueBest;
    TextView longitudeValueGPS, latitudeValueGPS;
    TextView longitudeValueNetwork, latitudeValueNetwork;
    TextView cityText;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private String uid;
    String locationCountryNameGPS;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    private static final String[] INITIAL_PERMS= {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int INITIAL_REQUEST=1337;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.my.facebookauth.R.layout.get_location);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        longitudeValueBest = (TextView) findViewById(R.id.longitudeValueBest);
        latitudeValueBest = (TextView) findViewById(R.id.latitudeValueBest);
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
        longitudeValueNetwork = (TextView) findViewById(R.id.longitudeValueNetwork);
        latitudeValueNetwork = (TextView) findViewById(R.id.latitudeValueNetwork);
        cityText = (TextView) findViewById(R.id.cityText);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("location");
        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }




    }

    private boolean checkLocation() {
        if(!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable location")
                .setMessage("Your location settings is set to 'Off'. \nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
    }

    public void toggleGPSUpdates(View view) {

        if(!checkLocation())
            return;
        Button button = (Button) view;
        if(button.getText().equals("pause")) {
            locationManager.removeUpdates(locationListenerGPS);
            button.setText("resume");
        }
        else {
            locationManager.requestLocationUpdates(
                    locationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);
            button.setText("pause");
        }
    }

    public void toggleBestUpdates(View view) {
        if(!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals("pause")) {
            locationManager.removeUpdates(locationListenerBest);
            button.setText("resume");
        }
        else {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider,2 * 60 * 1000, 10, locationListenerBest);
                button.setText("pause");
                Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void toggleNetworkUpdates(View view) {
        if(!checkLocation()) {
            return;
        }
        Button button = (Button) view;
        if (button.getText().equals("pause")) {
            locationManager.removeUpdates(locationListenerNetwork);
            button.setText("resume");
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 60 * 1000, 10, locationListenerNetwork);
            Toast.makeText(this, "Network provider started running", Toast.LENGTH_SHORT).show();
            button.setText("pause");
        }
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            laditudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueBest.setText(longitudeBest + "");
                    latitudeValueBest.setText(laditudeBest + "");
                    Toast.makeText(getLocationActivity.this, "Best provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueNetwork.setText(longitudeNetwork + "");
                    latitudeValueNetwork.setText(latitudeNetwork + "");
                    Toast.makeText(getLocationActivity.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            laditudeGPS = location.getLatitude();

            try {
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(laditudeGPS, longitudeGPS, 1);
                if (addresses.size() > 0) {
                    locationLocalityGPS = addresses.get(0).getLocality();
                    locationCountryNameGPS = addresses.get(0).getCountryName();
                }
            }
            catch (IOException e) {
                Log.e("facebookAuth", "error io exception: ", e);
            }

            location location1 = new location(laditudeGPS, longitudeGPS, locationLocalityGPS, locationCountryNameGPS);
            mRef.setValue(location1);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(laditudeGPS + "");
                    cityText.setText(locationLocalityGPS);

                    Toast.makeText(getLocationActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }


    //todo fix location settings by moving this into event feed
    @Override
    protected void onPause() {
        super.onPause();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putFloat("lat", (float) laditudeGPS);
        editor.putFloat("lng", (float) longitudeGPS);
        editor.commit();
    }
}

//public class getLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
//GoogleApiClient.OnConnectionFailedListener {
//
//    private GoogleApiClient mGoogleApiClient;
//    Location mLastLocation;
//    private TextView text1;
//    private TextView text2;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.get_location);
//
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//
//    }
//
//    protected void onStart() {
//        mGoogleApiClient.connect();
//        super.onStart();
//    }
//
//    protected void onStop() {
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        try {
//
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                    mGoogleApiClient);
//            if (mLastLocation != null) {
//                text1 = (TextView) findViewById(R.id.text1);
//                text1.setText("longitude: " + mLastLocation.getLongitude());
//                text2 = (TextView) findViewById(R.id.text2);
//                text2.setText("laditude: " + mLastLocation.getLatitude());
//
//
//            }
//        } catch (SecurityException e) {
//
//        }
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }
//}





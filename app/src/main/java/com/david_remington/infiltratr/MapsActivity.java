package com.david_remington.infiltratr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private static int mRefreshCount;
    private ArrayList<LatLng> mSavedLocations;
    @Bind(R.id.fab) FloatingActionButton mFab;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /* defines location request for onLocationChange,
       locates map fragment,
       issues callback for GoogleMap object*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mRefreshCount = 0;


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20000)
                .setFastestInterval(5000);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /* listens for the result of callback for GoogleMap object,
       defines member variable as result of callback,
       checks permissions then enables play location services
       builds query then issues callback to play location services */

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        }
        else {
            String warning = "You must enable location services in app permissions to view this screen";
            Toast.makeText(this, warning , Toast.LENGTH_LONG)
                    .show();
        }


         /* allow user to drop pin on screen */

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setOnMapClickListener(MapsActivity.this);
                mMap.setOnMarkerClickListener(MapsActivity.this);

            }
        });

    }

    /* listens for results of callback to play services
       checks permissions then checks for last known location
       if last known location is void it calls the method that finds new location*/


    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(mLastLocation != null) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                handleNewLocation(mLastLocation);
            } else {
               startLocationUpdates();
            }
        }
        else {
            String warning = "You must enable location services in app permissions to view this screen";
            Toast.makeText(this, warning , Toast.LENGTH_LONG)
                    .show();
        }
    }

    /* listens to see if connection to play services has been lost*/

    @Override
    public void onConnectionSuspended(int i) {
        String warning = "Connection to play services has been lost";
        Toast.makeText(this, warning , Toast.LENGTH_LONG)
                .show();
    }

    /* listens to see if connection to play services fails and catches errors*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            String warning = "You must enable google play services in order to view this screen";
            Toast.makeText(this, warning , Toast.LENGTH_LONG)
                    .show();
        }

    }

    /* listens for results of location update callback
       redefines location member variable based on results
       invokes location handler method to move center map view and zoom if first refresh*/

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
            handleNewLocation(location);

    }

    /* handles activity lifestyle */

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /* centers the map view on update
       zooms camera on 1st location update */

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        if (mRefreshCount == 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            mRefreshCount ++;
            drawLocations();
        }
    }

    /* constructs the play services callback client*/

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    /* issues callback to play services based on location request builder*/

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /* end connection with play services to save batteries*/

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options=new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cave_2))
                .anchor(0.5f, 0.5f);
        options.position(latLng);
        mMap.addMarker(options);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Marker currentMarker = marker;
        final Map coordinates = new HashMap();
        Double lat = currentMarker.getPosition().latitude;
        Double lng = currentMarker.getPosition().longitude;
        coordinates.put("latitude",lat);
        coordinates.put("longitude",lng);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.display_pin_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Pin");
        builder.setMessage("Would you like to save this pin?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveLocationToFirebase(coordinates);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentMarker.remove();
                        dialog.cancel();
                    }
                })
                .setView(view)
                .show();
        return false;
    }

    public void saveLocationToFirebase(Map coordinates) {
        Firebase savedLocationRef = new Firebase(Constants.FIREBASE_URL_SAVED_LOCATION);
        savedLocationRef.push().setValue(coordinates);
    }

    public void drawLocations() {
        Firebase savedLocationRef = new Firebase(Constants.FIREBASE_URL_SAVED_LOCATION);
        savedLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Map data = (HashMap) postSnapshot.getValue();
                    Double latitude = (Double) (data.get("latitude"));
                    Double longitude = (Double) (data.get("longitude"));
                    LatLng latLng = new LatLng(latitude, longitude);
                    MarkerOptions options=new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.cave_2))
                            .anchor(0.5f, 0.5f);
                    options.position(latLng);
                    mMap.addMarker(options);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}

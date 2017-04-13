package com.davidremington.infiltratr.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.davidremington.infiltratr.services.FirebaseService;
import com.davidremington.infiltratr.fragments.AddMarkerDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.davidremington.infiltratr.R;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{

    @Bind(R.id.fab) FloatingActionButton mFab;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private int mRefreshCount;
    private static FirebaseService sFirebaseService;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mRefreshCount = 0;
        sFirebaseService = FirebaseService.getInstance();
        mLocationRequest = buildLocationRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, getString(R.string.location_services_warning), Toast.LENGTH_LONG)
                    .show();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setOnMapClickListener(MapsActivity.this);
                mMap.setOnMarkerClickListener(MapsActivity.this);

            }
        });

    }


    /*---------------------- PLAY SERVICES CONNECTION MANAGEMENT ----------------------*/


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
            Toast.makeText(this, getString(R.string.location_services_warning), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, getString(R.string.connection_suspended) , Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.location_services_warning) , Toast.LENGTH_LONG)
                    .show();
        }

    }


    /*----------------------------- LOCATION MANAGEMENT -------------------------------*/


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        handleNewLocation(location);

    }

    private void handleNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mRefreshCount == 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            mRefreshCount ++;
            drawLocations();
        }
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }


    /*----------------------------- CLICK LISTENERS -------------------------------*/


    @Override
    public void onMapClick(LatLng latLng) {
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_marker");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        AddMarkerDialogFragment dialogFragment = AddMarkerDialogFragment.newInstance(latLng);
        dialogFragment.show(manager, "fragment_add_marker");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Map coordinates = new HashMap();
        Double lat = marker.getPosition().latitude;
        Double lng = marker.getPosition().longitude;
        coordinates.put("latitude",lat);
        coordinates.put("longitude",lng);
        return false;
    }

    /*----------------------------- HELPER METHODS -------------------------------*/


    private void drawLocations() {
        MarkerOptions[] options = sFirebaseService.retrieveMarkersFromFirebase();
        if(options[0] != null) {
            mMap.addMarker(options[0]);
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private LocationRequest buildLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20000)
                .setFastestInterval(5000);
    }
}

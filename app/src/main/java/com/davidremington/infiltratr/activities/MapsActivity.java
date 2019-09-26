package com.davidremington.infiltratr.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import com.davidremington.infiltratr.fragments.MapDisabledFragment;
import com.davidremington.infiltratr.services.FirebaseService;
import com.davidremington.infiltratr.fragments.AddMarkerDialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import com.davidremington.infiltratr.R;

import static com.davidremington.infiltratr.utils.Constants.MAP_DISABLED_FRAGMENT_TAG;
import static com.davidremington.infiltratr.utils.Constants.MAP_FRAGMENT;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        MapDisabledFragment.OnMapDisabledFragmentInteractionListener {


    @BindView(R.id.addMarkerFab) FloatingActionButton addMarkerFab;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private int refreshCounter;
    private static FirebaseService firebaseService;
    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) { return; }
            currentLocation = locationResult.getLastLocation();
            handleNewLocation(currentLocation);
        }
    };


    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1776;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        refreshCounter = 0;
        firebaseService = FirebaseService.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = buildLocationRequest();
        SupportMapFragment mapFragment = new SupportMapFragment();
        addFragmentToTop(mapFragment, MAP_FRAGMENT);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAppPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(googleApiClient != null && googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.map.setMyLocationEnabled(true);
        }

        addMarkerFab.setOnClickListener((view) -> {
            map.setOnMapClickListener(MapsActivity.this);
            map.setOnMarkerClickListener(MapsActivity.this);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToGoogleApiClient();
            } else {
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (!shouldShowRationale) {
                    addFragmentToTop(new MapDisabledFragment(), MAP_DISABLED_FRAGMENT_TAG);
                    addMarkerFab.hide();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.alert_location_services_title)
                            .setMessage(R.string.alert_location_services_message)
                            .setPositiveButton(R.string.btn_settings_txt,
                                    (dialog, which) -> {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 0);
                                    })
                            .setNegativeButton(R.string.btn_cancel_text,
                                    ((dialog, which) -> {
                                        addFragmentToTop(new MapDisabledFragment(), MAP_DISABLED_FRAGMENT_TAG);
                                        addMarkerFab.hide();
                                    }))
                            .show();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if(currentLocation != null) {
                    currentLocation = location;
                } else {
                    startLocationUpdates();
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.alert_location_services_message), Toast.LENGTH_LONG)
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
            Toast.makeText(this, getString(R.string.alert_location_services_message) , Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void handleNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (refreshCounter == 0) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            refreshCounter++;
            drawLocations();
        }
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    protected void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_marker");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        AddMarkerDialogFragment dialogFragment = AddMarkerDialogFragment.newInstance(latLng);
        dialogFragment.show(manager, "fragment_add_marker");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Map<String, Double> coordinates = new HashMap<>();
        Double lat = marker.getPosition().latitude;
        Double lng = marker.getPosition().longitude;
        coordinates.put("latitude",lat);
        coordinates.put("longitude",lng);
        return false;
    }

    private void drawLocations() {
        Subject<MarkerOptions, MarkerOptions> updateObserver = PublishSubject.create();
        updateObserver.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((MarkerOptions option) -> {
                    if(option != null) {
                        map.addMarker(option);
                    }
                }, Timber::e);
        firebaseService.retrieveMarkersFromFirebase(updateObserver);

    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
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

    private void checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            connectToGoogleApiClient();
        }
    }

    private void connectToGoogleApiClient() {
        buildGoogleApiClient();
        googleApiClient.connect();
    }

    private void addFragmentToTop(Fragment fragment, String tab) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(tab)
                .commit();
    }

    private void popFragmentFromTop() {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onUserReturnedFromSettings() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            popFragmentFromTop();
            addMarkerFab.show();
        }
    }
}

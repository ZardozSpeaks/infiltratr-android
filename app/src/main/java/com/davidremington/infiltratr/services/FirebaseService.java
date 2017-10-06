package com.davidremington.infiltratr.services;


import com.davidremington.infiltratr.models.LocationMarker;
import com.davidremington.infiltratr.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.Subject;

public class FirebaseService {
    private static FirebaseService sInstance = null;
    private static Firebase sFirebase = new Firebase(Constants.FIREBASE_URL_SAVED_LOCATION);

    public FirebaseService() {
        //method to prevent instantiation
    }

    public static FirebaseService getInstance() {
        if(sInstance == null) {
            sInstance = new FirebaseService();
        }
        return sInstance;
    }

    public void saveLocationToFirebase(LocationMarker locationMarker) {
        sFirebase.push().setValue(locationMarker);
    }

    public void retrieveMarkersFromFirebase(Subject<MarkerOptions, MarkerOptions> updateObserver) {
        sFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Map data = (HashMap) postSnapshot.getValue();
                    Double latitude = (Double) (data.get("latitude"));
                    Double longitude = (Double) (data.get("longitude"));
                    String title = (String) (data.get("locationTitle"));
                    String snippet = (String) (data.get("snippet"));
                    LocationMarker locationMarker = new LocationMarker(latitude, longitude, title, snippet);
                    MarkerOptions currentOption = locationMarker.buildLocationMarker();
                    updateObserver.onNext(currentOption);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}

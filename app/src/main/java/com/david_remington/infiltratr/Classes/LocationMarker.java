package com.david_remington.infiltratr.Classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcel;

@Parcel
public class LocationMarker {
    private LatLng mCoordinates;
    private String mLocationTitle;
    private String mSnippet;

    public LocationMarker(){}

    public LocationMarker(LatLng coordinates, String locationTitle, String snippet) {
        this.mCoordinates = coordinates;
        this.mLocationTitle = locationTitle;
        this.mSnippet = snippet;
    }

    public LatLng getCoodinates() {
        return mCoordinates;
    }

    public String getLocationTitle() {
        return mLocationTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }

    public MarkerOptions buildLocationMarker() {
        return new MarkerOptions()
                .position(this.mCoordinates)
                .title(this.mLocationTitle)
                .snippet(this.mSnippet)
                .anchor(0.5f, 0.5f);
    }
}



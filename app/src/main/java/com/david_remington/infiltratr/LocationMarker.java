package com.david_remington.infiltratr;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMarker {
    private LatLng mCoodinates;
    private BitmapDescriptor mLocationIcon;
    private String mLocationTitle;
    private String mSnippet;

    public LatLng getCoodinates() {
        return mCoodinates;
    }

    public BitmapDescriptor getLocationIcon() {
        return mLocationIcon;
    }

    public String getLocationTitle() {
        return mLocationTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }

    public MarkerOptions buildLocationMarker() {
        return new MarkerOptions()
                .position(this.mCoodinates)
                .icon(this.mLocationIcon)
                .title(this.mLocationTitle)
                .snippet(this.mSnippet)
                .anchor(0.5f, 0.5f);
    }
}



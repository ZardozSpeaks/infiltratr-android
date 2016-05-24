package com.david_remington.infiltratr.Classes;

import com.david_remington.infiltratr.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMarker {
    private LatLng mCoordinates;
    private Double mLatitude;
    private Double mLongitude;
    private String mLocationTitle;
    private String mSnippet;
    private BitmapDescriptor mLocationIcon;


    public LocationMarker(Double latitude, Double longitude, String locationTitle, String snippet) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mLocationTitle = locationTitle;
        this.mSnippet = snippet;
        this.mLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.cave_2);
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {return mLongitude;}

    public String getLocationTitle() {
        return mLocationTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }


    public MarkerOptions buildLocationMarker() {
        mCoordinates = new LatLng(mLatitude, mLongitude);

        return new MarkerOptions()
                .position(this.mCoordinates)
                .title(this.mLocationTitle)
                .snippet(this.mSnippet)
                .anchor(0.5f, 0.5f)
                .icon(this.mLocationIcon);
    }
}



package com.davidremington.infiltratr.models;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.davidremington.infiltratr.R;

public class LocationMarker {
    private LatLng coordinates;
    private Double latitude;
    private Double longitude;
    private String locationTitle;
    private String snippet;
    private BitmapDescriptor locationIcon;


    public LocationMarker(Double latitude, Double longitude, String locationTitle, String snippet) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTitle = locationTitle;
        this.snippet = snippet;
        this.locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.cave_2);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {return longitude;}

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getSnippet() {
        return snippet;
    }

    public LatLng getCoordinates() { return coordinates; }

    public MarkerOptions buildLocationMarker() {
        coordinates = new LatLng(latitude, longitude);

        return new MarkerOptions()
                .position(this.coordinates)
                .title(this.locationTitle)
                .snippet(this.snippet)
                .anchor(0.5f, 0.5f)
                .icon(this.locationIcon);
    }
}



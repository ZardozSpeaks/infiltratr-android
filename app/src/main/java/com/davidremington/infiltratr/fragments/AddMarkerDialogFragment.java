package com.davidremington.infiltratr.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.davidremington.infiltratr.models.LocationMarker;
import com.davidremington.infiltratr.R;

import com.davidremington.infiltratr.services.FirebaseService;
import com.google.android.gms.maps.model.LatLng;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddMarkerDialogFragment extends DialogFragment {

    private static FirebaseService firebaseService;
    private static final String ARGUMENT_LAT_LNG = "latLng";

    @BindView(R.id.titleEditText) EditText titleEditText;
    @BindView(R.id.descriptionEditText) EditText descriptionEditText;

    private LatLng latLng;

    public static AddMarkerDialogFragment newInstance (LatLng latLng) {
        AddMarkerDialogFragment addMarkerDialogFragment = new AddMarkerDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_LAT_LNG, Parcels.wrap(latLng));
        addMarkerDialogFragment.setArguments(args);
        return addMarkerDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseService = FirebaseService.getInstance();
        Bundle args = getArguments();
        if (args != null) {
            latLng = Parcels.unwrap(args.getParcelable(ARGUMENT_LAT_LNG));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_marker, container, false);
        ButterKnife.bind(this, view);
        titleEditText.requestFocus();
        return view;
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        this.dismiss();
    }

    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;
        LocationMarker locationMarker = new LocationMarker(latitude, longitude, title, description);
        firebaseService.saveLocationToFirebase(locationMarker);
        this.dismiss();
    }
}
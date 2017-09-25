package com.davidremington.infiltratr.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.davidremington.infiltratr.models.LocationMarker;
import com.davidremington.infiltratr.R;

import com.davidremington.infiltratr.services.FirebaseService;
import com.google.android.gms.maps.model.LatLng;
import org.parceler.Parcels;


import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMarkerDialogFragment extends DialogFragment implements Button.OnClickListener {

    private static final String TAG = AddMarkerDialogFragment.class.getSimpleName();
    private static FirebaseService sFirebaseService;

    private LatLng mLatLng;
    @BindView(R.id.titleEditText) EditText mTitleEditText;
    @BindView(R.id.descriptionEditText) EditText mDescriptionEditText;
    @BindView(R.id.saveButton) Button mSaveButton;
    @BindView(R.id.cancelButton) Button mCancelButton;


    public static AddMarkerDialogFragment newInstance (LatLng latLng) {
        AddMarkerDialogFragment addMarkerDialogFragment = new AddMarkerDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("latLng", Parcels.wrap(latLng));
        addMarkerDialogFragment.setArguments(args);
        return addMarkerDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sFirebaseService = FirebaseService.getInstance();
        mLatLng = Parcels.unwrap(getArguments().getParcelable("latLng"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_marker, container, false);
        ButterKnife.bind(this, view);
        mSaveButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mTitleEditText.requestFocus();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                String title = mTitleEditText.getText().toString();
                String description = mDescriptionEditText.getText().toString();
                Double latitude = mLatLng.latitude;
                Double longitude = mLatLng.longitude;
                LocationMarker locationMarker = new LocationMarker(latitude, longitude, title, description);
                sFirebaseService.saveLocationToFirebase(locationMarker);
                AddMarkerDialogFragment.this.dismiss();
                break;
            case R.id.cancelButton:
                AddMarkerDialogFragment.this.dismiss();
                break;
        }

    }

}
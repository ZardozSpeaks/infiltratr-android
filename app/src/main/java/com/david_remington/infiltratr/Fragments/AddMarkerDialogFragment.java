package com.david_remington.infiltratr.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.david_remington.infiltratr.Classes.LocationMarker;
import com.david_remington.infiltratr.R;
import com.google.android.gms.maps.model.BitmapDescriptor;

import org.parceler.Parcels;


import butterknife.Bind;
import butterknife.ButterKnife;

public class AddMarkerDialogFragment extends DialogFragment implements Button.OnClickListener {
    private LocationMarker mLocationMarker;
    private BitmapDescriptor mLocationIcon;
    @Bind(R.id.titleEditText) EditText mTitleEditText;
    @Bind(R.id.descriptionEditText) EditText mDescriptionEditText;
    @Bind(R.id.saveButton) Button mSaveButton;
    @Bind(R.id.cancelButton) Button mCancelButton;


    public static AddMarkerDialogFragment newInstance (LocationMarker locationMarker) {
        AddMarkerDialogFragment addMarkerDialogFragment = new AddMarkerDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("locationMarker", Parcels.wrap(locationMarker));
        addMarkerDialogFragment.setArguments(args);
        return addMarkerDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationMarker = Parcels.unwrap(getArguments().getParcelable("locationMarker"));
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

    }
}
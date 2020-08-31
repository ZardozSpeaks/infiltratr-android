package com.davidremington.infiltratr;

import android.annotation.SuppressLint;
import android.app.Application;
import android.provider.Settings;

import com.davidremington.infiltratr.utils.DeviceDetails;
import com.davidremington.infiltratr.utils.TimberRemoteTree;
import com.firebase.client.Firebase;

import timber.log.Timber;

public class InfiltratrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            @SuppressLint("HardwareIds")
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            DeviceDetails deviceDetails = new DeviceDetails(deviceId);
            TimberRemoteTree remoteTree = new TimberRemoteTree(deviceDetails);

            Timber.plant(remoteTree);
        }
    }
}

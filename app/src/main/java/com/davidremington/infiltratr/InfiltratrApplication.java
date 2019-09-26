package com.davidremington.infiltratr;

import android.app.Application;

import com.firebase.client.Firebase;

import timber.log.Timber;

public class InfiltratrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}

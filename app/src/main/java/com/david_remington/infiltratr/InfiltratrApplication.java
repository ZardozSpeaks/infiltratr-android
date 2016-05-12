package com.david_remington.infiltratr;

import android.app.Application;

import com.firebase.client.Firebase;

public class InfiltratrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

package com.app.rideshare.activity;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by rlogical-dev-19 on 07-Jun-2017.
 */

public class RideShareApp extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}

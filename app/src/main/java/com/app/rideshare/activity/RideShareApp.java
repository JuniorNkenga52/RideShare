package com.app.rideshare.activity;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class RideShareApp extends Application
{
    private String mUserType="";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);
    }

    public String getmUserType() {
        return mUserType;
    }

    public void setmUserType(String mUserType) {
        this.mUserType = mUserType;
    }
}

package com.app.rideshare.activity;

import android.app.Application;
import android.location.Location;
import android.support.multidex.MultiDex;

import com.app.rideshare.utils.PrefUtils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class RideShareApp extends Application {
    public static String mUserType = "";
    public static int mHomeTabPos = 0;

    public static Location mLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);
        PrefUtils.initPreference(this);
    }

    public static String getmUserType() {
        return mUserType;
    }

    public void setmUserType(String mUserType) {
        this.mUserType = mUserType;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}

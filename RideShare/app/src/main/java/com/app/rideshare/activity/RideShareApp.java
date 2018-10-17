package com.app.rideshare.activity;

import android.app.Application;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.app.rideshare.service.LocationProvider;
import com.app.rideshare.utils.PrefUtils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class RideShareApp extends Application implements LocationProvider.LocationCallback{
    public static String mUserType = "";
    public static int mHomeTabPos = 0;

    LocationProvider mLocationProvider;
    public static Location mLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);
        PrefUtils.initPreference(this);

        //mLocationProvider = new LocationProvider(this, this);
       // mLocationProvider.connect();
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

    @Override
    public void handleNewLocation(Location location) {

    }
}

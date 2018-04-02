package com.app.rideshare.activity;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.app.rideshare.utils.PrefUtils;
import com.facebook.FacebookSdk;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class RideShareApp extends Application
{
    private String mUserType="";
    public static int mHomeTabPos = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);
        PrefUtils.initPreference(this);
    }

    public String getmUserType() {
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

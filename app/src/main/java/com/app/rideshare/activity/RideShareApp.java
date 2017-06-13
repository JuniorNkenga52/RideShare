package com.app.rideshare.activity;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;


public class RideShareApp extends Application
{
    private String mUserType="";

    @Override
    public void onCreate() {
        super.onCreate();
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

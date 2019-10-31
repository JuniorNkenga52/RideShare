package com.app.rideWhiz.activity;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private boolean appInCreate = false;
    private boolean appInPaused = false;
    private boolean appInForeground = false;
    private boolean appIsClosed = false;
    private LifeCycleDelegate lifeCycleDelegate;

    AppLifecycleHandler(LifeCycleDelegate lifeCycleDelegate) {
        this.lifeCycleDelegate = lifeCycleDelegate;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!appInCreate) {
            appInCreate = true;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!appInForeground) {
            appInForeground = true;
            lifeCycleDelegate.onAppForegrounded();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (!appInPaused) {
            appInPaused = true;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (!appIsClosed) {
            appIsClosed = true;
            lifeCycleDelegate.onAppClosed();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            appInForeground = false;
            lifeCycleDelegate.onAppBackgrounded();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}

interface LifeCycleDelegate {
    void onAppBackgrounded();

    void onAppForegrounded();

    void onAppClosed();
}
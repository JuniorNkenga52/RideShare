package com.app.rideshare.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.rideshare.activity.LongOperation;
import com.app.rideshare.activity.StartRideActivity;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int counter = 0;
    BroadcastReceiver receiver;

    User bean;

    @Override
    public void onCreate() {
        super.onCreate();
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startForeground(1, new Notification());*/
        PrefUtils.initPreference(this);
        intent = new Intent(BROADCAST_ACTION);
        bean = PrefUtils.getUserInfo();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MessageUtils.showFailureMessage(getApplicationContext(), "Permission Denied");
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        try {
            new LongOperation(getApplicationContext()).execute(bean.getmUserId(), "0").get();
        } catch (Exception ignore) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MessageUtils.showFailureMessage(getApplicationContext(), "Permission Denied");
        }
        try {
            locationManager.removeUpdates(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("*********************", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {

                previousBestLocation = loc;
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                Intent RTReturn = new Intent(StartRideActivity.RECEIVE_JSON);
                RTReturn.putExtra("Latitude", loc.getLatitude());
                RTReturn.putExtra("Longitude", loc.getLongitude());
                RTReturn.putExtra("Provider", loc.getProvider());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(RTReturn);
            }
        }

        public void onProviderDisabled(String provider) {
            MessageUtils.showFailureMessage(getApplicationContext(), "Gps Disabled");
        }

        public void onProviderEnabled(String provider) {
            MessageUtils.showFailureMessage(getApplicationContext(), "Gps Enabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
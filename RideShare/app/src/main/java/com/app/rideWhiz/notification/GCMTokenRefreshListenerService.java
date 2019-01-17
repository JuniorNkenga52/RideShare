package com.app.rideWhiz.notification;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    //If the token is changed registering the device again 
    @Override
    public void onTokenRefresh() {
       /* Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this,new Intent(getBaseContext(), GCMRegistrationIntentService.class));
        } else {
            startService(new Intent(getBaseContext(), GCMRegistrationIntentService.class));
        }*/
        startService(new Intent(getBaseContext(), GCMRegistrationIntentService.class));
    }
}
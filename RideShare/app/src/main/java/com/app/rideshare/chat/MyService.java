package com.app.rideshare.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.app.rideshare.model.User;
import com.app.rideshare.utils.Constants;
import com.app.rideshare.utils.PrefUtils;

public class MyService extends Service {

    public static MyXMPP xmpp;

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<MyService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PrefUtils.initPreference(getApplicationContext());

        User user = PrefUtils.getUserInfo();

        try {
            // xmpp = MyXMPP.getInstance(CheckBGService.this, user.getmUserId());
            xmpp = MyXMPP.getInstance(MyService.this, Constants.intentKey.jabberPrefix + user.getmUserId());
            xmpp.connect("onCreate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return Service.START_STICKY;
    }

}
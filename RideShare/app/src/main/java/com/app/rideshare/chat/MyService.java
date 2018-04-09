package com.app.rideshare.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.app.rideshare.model.User;
import com.app.rideshare.utils.Constant;
import com.app.rideshare.utils.PrefUtils;

public class MyService extends Service {

    public static MyXMPP xmpp;

    private Messenger messageHandler;

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
            xmpp = MyXMPP.getInstance(MyService.this, user.getmUserId());
//            xmpp = MyXMPP.getInstance(MyService.this, Constant.intentKey.jabberPrefix + user.getmUserId());
            xmpp.connect("onCreate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return Service.START_STICKY;
    }

    /* protected void onHandleIntent(Intent intent) {
         // handle intents passed using startService()
     }

     @Override
     public boolean onUnbind(final Intent intent) {
         return super.onUnbind(intent);
     }

     @Override
     public void onDestroy() {
         super.onDestroy();
         xmpp.connection.disconnect();
     }
 */
    public void sendMessage(int i) {
        Message message = Message.obtain();
        switch (i) {
            case 1:
                message.arg1 = 1;
                break;
        }
        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
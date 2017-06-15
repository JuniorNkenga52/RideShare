package com.sukaree.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sukaree.R;
import com.sukaree.activity.DashBoardActivity;
import com.sukaree.activity.NavigationActivity;

import org.json.JSONObject;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("m");

        try {
            JSONObject jobj = new JSONObject(message.toString());
            String id = jobj.getString("id");
            String medicine = jobj.getString("msg");

            sendNotification(medicine, id);

        } catch (Exception e) {
            Log.d("Error",e.toString());
        }


    }

    private void sendNotification(String message, String id) {

        int currenttime=(int) System.currentTimeMillis();

        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("fromNotification", "0");
        intent.putExtra("id", id);
        intent.putExtra("currenttime", currenttime);
        int requestCode = 0;
        String msg = "Hay, this is time to take " + message + " medicine. Have you taken your medicine?";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(sound)
                .addAction(0, "ok", null)
                .setAutoCancel(true)
                .addAction(0, "i have taken", pendingIntent);
        //   .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(currenttime, noBuilder.build());
    }
}
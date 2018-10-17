package com.app.rideshare.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.rideshare.R;
import com.app.rideshare.activity.MyGroupSelectionActivity;
import com.app.rideshare.activity.NotificationActivity;
import com.app.rideshare.activity.RideRateActivity;
import com.app.rideshare.activity.StartRideActivity;
import com.app.rideshare.utils.PrefUtils;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("m");

        try {

            JSONObject jobj = new JSONObject(message);

            if (jobj.getString("type").equals("1")) {
                JSONArray jarrMsg = jobj.getJSONArray("msg");
                JSONObject jobjmessage = jarrMsg.getJSONObject(0);
                openActivity(jobjmessage.toString());

            } else if (jobj.getString("type").equals("2")) {
                Intent intent = new Intent("request_status");
                intent.putExtra("int_data", jobj.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else if (jobj.getString("type").equals("3")) {
                Intent intent = new Intent("request_notification");
                intent.putExtra("int_data", jobj.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else if (jobj.getString("type").equals("4")) {
                Intent intent = new Intent("start_ride");
                intent.putExtra("int_data", "1");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendNotification("Ride Started", "4");
            } else if (jobj.getString("type").equals("5")) {
                Intent intent = new Intent("start_ride");
                intent.putExtra("int_data", "2");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendNotification("Ride Finished", "5");

            } else if (jobj.getString("type").equals("6")) {
                //JSONObject jObjUser = jobj.getJSONObject("result");
                PrefUtils.putString("AdminID", jobj.getJSONObject("result").getString("admin_id"));
                Intent intent = new Intent("new_user");
                intent.putExtra("int_data", "2");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendNotification(jobj.getString("msg"), "6");
            } else if (jobj.getString("type").equals("7")) {
                Intent intent = new Intent("new_user_req");
                intent.putExtra("int_data", "2");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendNotification(jobj.getString("msg"), "7");
            }

        } catch (Exception e) {
            Log.d("error", e.toString());
        }
    }

    public void openActivity(String json) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("data", json);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendNotification(String message, String type) {

        int currenttime = (int) System.currentTimeMillis();

        Intent intent;
        int requestCode = 0;

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = null;
        String NOTIF_CHANNEL_ID = "my_notification_channel";
        if (type.equals("4")) {
            StartRideActivity.RideStatus = "inProgress";
        }
        if (type.equals("5")) {
            StartRideActivity.RideStatus = "finished";
            /*Intent rateride = new Intent(this, RideRateActivity.class);
            startActivity(rateride);*/

        }
        if (type.equals("6")) {
            if(!message.equals("Request Declined")){
                PrefUtils.putString("isBlank", "false");
            }
            intent = new Intent(this, MyGroupSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
            notifBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentText(message)
                    .setContentTitle("RideShare")
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent);

            PrefUtils.putBoolean("firstTime", true);
        } else {
            if (PrefUtils.getBoolean("islogin")) {
                notifBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentText(message)
                        .setContentTitle("RideShare")
                        .setAutoCancel(true)
                        .setSound(sound);
            }
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Configure the notification channel.
            NotificationChannel notifChannel = new NotificationChannel(NOTIF_CHANNEL_ID, message, NotificationManager.IMPORTANCE_DEFAULT);
            notifChannel.setDescription(message);
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.GREEN);
            notifChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notifChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notifChannel);
        }

        notificationManager.notify(currenttime, notifBuilder.build());
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
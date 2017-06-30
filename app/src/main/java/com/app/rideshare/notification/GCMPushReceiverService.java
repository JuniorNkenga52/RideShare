package com.app.rideshare.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.rideshare.R;
import com.app.rideshare.activity.HomeActivity;
import com.app.rideshare.activity.NotificationActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("m");

        try{

            JSONObject jobj=new JSONObject(message);

            if(jobj.getString("type").equals("1"))
            {
                JSONArray jarrMsg=jobj.getJSONArray("msg");
                JSONObject jobjmessage=jarrMsg.getJSONObject(0);
                openActivity(jobjmessage.toString());

            }else if(jobj.getString("type").equals("2"))
            {
                Intent intent = new Intent("request_status");
                intent.putExtra("int_data",jobj.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }else if(jobj.getString("type").equals("3"))
            {
                Intent intent = new Intent("request_notification");
                intent.putExtra("int_data",jobj.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }else if(jobj.getString("type").equals("4"))
            {
                Intent intent = new Intent("start_ride");
                intent.putExtra("int_data","1");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendNotification("Your Ride start");
            }
            else if(jobj.getString("type").equals("5"))
            {
                Intent intent = new Intent("start_ride");
                intent.putExtra("int_data","2");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendNotification("Your Ride Finish");
            }
        }catch (Exception e){
            Log.d("error",e.toString());
        }
    }
    public void openActivity(String json)
    {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("data",json);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendNotification(String message) {

        int currenttime=(int) System.currentTimeMillis();

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build());
    }
}
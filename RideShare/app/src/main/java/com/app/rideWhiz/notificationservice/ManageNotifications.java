package com.app.rideWhiz.notificationservice;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.MyGroupSelectionActivity;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.model.InProgressRide;
import com.app.rideWhiz.utils.PrefUtils;

public class ManageNotifications {

    public static void sendNotification(Activity activity, AcceptRider rider, String message, String req_type) {

        int currenttime = (int) System.currentTimeMillis();
        Intent intent = null;
        int requestCode = 0;
        PendingIntent pendingIntent;

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = null;
        String NOTIF_CHANNEL_ID = "my_notification_channel";

        if (req_type.equals("1006")) {
            if (rider != null) {
                intent = setRideData(rider, activity);
                pendingIntent = PendingIntent.getActivity(activity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                intent = new Intent(activity, MyGroupSelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(activity, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
            }
        } else {
            intent = new Intent(activity, MyGroupSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(activity, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        }


        notifBuilder = new NotificationCompat.Builder(activity, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(message)
                .setContentTitle("RideWhiz")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
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

    public static void sendGroupNotification(MyFirebaseMessagingService activity, String message, String req_type) {

        int currenttime = (int) System.currentTimeMillis();
        Intent intent = null;
        int requestCode = 0;
        PendingIntent pendingIntent;

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = null;
        String NOTIF_CHANNEL_ID = "my_notification_channel";

        intent = new Intent(activity, MyGroupSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(activity, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

        if (req_type.equals("6")) {
            if (!message.equals("Request Declined")) {
                PrefUtils.putString("isBlank", "false");
            }
            PrefUtils.putBoolean("firstTime", true);
        }
        notifBuilder = new NotificationCompat.Builder(activity, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(message)
                .setContentTitle("RideWhiz")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
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


    public static Intent setRideData(AcceptRider acRider, Activity activity) {
        Intent intent = null;
        try {
            InProgressRide inProgressRide = new InProgressRide();
            inProgressRide.setmRideId(acRider.getRide_id());
            inProgressRide.setmRideType(acRider.getU_ride_type());

            inProgressRide.setmStartingAddress(acRider.getStarting_address());
            inProgressRide.setmEndingAddress(acRider.getEnding_address());
            inProgressRide.setmStartLat(acRider.getStart_lati());
            inProgressRide.setmStartLang(acRider.getStart_long());
            inProgressRide.setmEndLat(acRider.getEnd_lati());
            inProgressRide.setmEndLang(acRider.getEnd_long());
            inProgressRide.setmRequestStatus(acRider.getRequest_status());

            inProgressRide.setmFromRider(acRider.getFromRider());
            inProgressRide.setmToRider(acRider.getToRider());

            intent = new Intent(activity, MyGroupSelectionActivity.class);
            intent.putExtra("inprogress", "busy");
            intent.putExtra("rideprogress", inProgressRide);
            intent.putExtra("rideUserID", acRider.getToRider().getnUserId());
            intent.putExtra("Is_driver", "1");
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }
}

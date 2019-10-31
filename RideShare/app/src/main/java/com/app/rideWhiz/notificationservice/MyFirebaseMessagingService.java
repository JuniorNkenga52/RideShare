package com.app.rideWhiz.notificationservice;

import android.app.ActivityManager;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.ChatActivity;
import com.app.rideWhiz.activity.MyGroupSelectionActivity;
import com.app.rideWhiz.activity.NotificationActivity;
import com.app.rideWhiz.activity.StartRideActivity;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.model.InProgressRide;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.utils.PrefUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
        registerGCM(s);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.w("Check==>", "Get Notification" + isAppRunning(this));
        PrefUtils.initPreference(this);
        if (!isAppRunning(this)) {
            PrefUtils.putBoolean("isAppRunning", false);
        } else {
            PrefUtils.putBoolean("isAppRunning", true);
        }

        String message = remoteMessage.getData().get("m");
        try {
            JSONObject jobj = new JSONObject(message);
            if (PrefUtils.getBoolean("islogin")) {
                switch (jobj.getString("type")) {
                    case "1": {
                        JSONArray jarrMsg = jobj.getJSONArray("msg");
                        JSONObject jobjmessage = jarrMsg.getJSONObject(0);
                        if (PrefUtils.getBoolean("isAppRunning")) {
                            openActivity(jobjmessage.toString());
                        }
                        break;
                    }
                    case "2": {
                        Intent intent = new Intent("request_status");
                        intent.putExtra("int_data", jobj.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    }
                    case "3": {
                        Intent intent = new Intent("request_notification");
                        intent.putExtra("int_data", jobj.toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    }
                    case "4": {
                        JSONArray jarrMsg = jobj.getJSONArray("ride_history");
                        JSONObject jobjmessage = jarrMsg.getJSONObject(0);

                        Intent intent = new Intent("start_ride");
                        intent.putExtra("int_data", "1");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        if (!isAppRunning(this)) {
                            sendNotification(jobjmessage, "Ride Started", "4");
                        }
                        break;
                    }
                    case "5": {

                        Intent intent = new Intent("start_ride");
                        intent.putExtra("int_data", "2");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        sendNotification(new JSONObject(""), "Ride Finished", "5");
                        break;
                    }
                    case "6": {
                        //JSONObject jObjUser = jobj.getJSONObject("result");
                        PrefUtils.putString("AdminID", jobj.getJSONObject("result").getString("admin_id"));
                        Intent intent = new Intent("new_user");
                        intent.putExtra("int_data", "2");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        //sendNotification(new JSONObject(""), jobj.getString("msg"), "6");

                        ManageNotifications.sendGroupNotification(this, jobj.getString("msg"), "6");
                        break;
                    }
                    case "7": {
                        Intent intent = new Intent("new_user_req");
                        intent.putExtra("int_data", "2");
                        ManageNotifications.sendGroupNotification(this, jobj.getString("msg"), "");
                        //sendNotification(new JSONObject(""), jobj.getString("msg"), "7");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }

    }

    // [END receive_message]
    public void openActivity(String json) {
        //Log.d("App State :::: >>>>", String.valueOf(isAppRunning(this,"com.app.rideWhiz")));

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("data", json);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendNotification(JSONObject jsonObject, String message, String type) {

        int currenttime = (int) System.currentTimeMillis();
        Intent intent = null;
        int requestCode = 0;
        PendingIntent pendingIntent;

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = null;
        String NOTIF_CHANNEL_ID = "my_notification_channel";

        intent = new Intent(this, MyGroupSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        if (type.equals("4")) {
            StartRideActivity.RideStatus = "inProgress";
            try {
                intent = setRideData(jsonObject);
                pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (type.equals("5")) {
            StartRideActivity.RideStatus = "finished";
            ActivityManager am = (ActivityManager) this
                    .getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = "com.app.rideWhiz";
            if (am.getRunningTasks(1).get(0).topActivity.getClassName().equals(
                    packageName + ".activity.ChatActivity")) {
                intent = new Intent("start_ride");
                intent.putExtra("int_data", "2");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                ChatActivity.activity.finish();
            }
        }
        if (type.equals("6")) {
            if (!message.equals("Request Declined")) {
                PrefUtils.putString("isBlank", "false");
            }
            PrefUtils.putBoolean("firstTime", true);
        }
        notifBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(message)
                .setContentTitle("RideWhiz")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
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
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    private void registerGCM(String token) {
        //Registration complete intent initially null
        Intent registrationComplete = null;

        //Register token is also null
        //we will get the token on successfull registration
        try {
            Log.w("GCMRegIntentService", "token:" + token);

            //on registration complete creating intent with success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);

            //Putting the token to the intent
            registrationComplete.putExtra("token", token);
        } catch (Exception e) {
            //If any error occurred
            e.printStackTrace();
            Log.w("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }

        //Sending the broadcast that registration is completed
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    public boolean isAppRunning(Context context) {
        boolean appFound = false;
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < recentTasks.size(); i++) {
            if (recentTasks.get(i).baseActivity.getPackageName().equals("com.app.rideWhiz")) {
                appFound = true;
                break;
            }
        }
        return appFound;
    }

    public Intent setRideData(JSONObject jRider) {
        AcceptRider rider = new AcceptRider();
        Intent intent = null;
        try {

            JSONObject jFromRider = jRider.getJSONObject("from_id");
            Rider fromRider = new Rider();
            fromRider.setnUserId(jFromRider.getString("u_id"));
            fromRider.setmFirstName(jFromRider.getString("u_firstname"));
            fromRider.setmLastName(jFromRider.getString("u_lastname"));
            fromRider.setmEmail(jFromRider.getString("u_email"));
            fromRider.setmProfileImage(jFromRider.optString("profile_image"));
            fromRider.setThumb_image(jFromRider.optString("thumb_image"));
            fromRider.setmLatitude(jFromRider.getString("u_lat"));
            fromRider.setmLongitude(jFromRider.getString("u_long"));
            rider.setFromRider(fromRider);

            String rideUserID = "";
            String Is_driver = "";
            JSONObject jToRider = jRider.getJSONObject("to_id");
            Rider toRider = new Rider();
            rideUserID = jToRider.getString("u_id");
            toRider.setnUserId(rideUserID);
            toRider.setmFirstName(jToRider.getString("u_firstname"));
            toRider.setmLastName(jToRider.getString("u_lastname"));
            toRider.setmEmail(jToRider.getString("u_email"));
            toRider.setmProfileImage(jToRider.optString("profile_image"));
            toRider.setThumb_image(jToRider.optString("thumb_image"));
            toRider.setmLatitude(jToRider.getString("u_lat"));
            toRider.setmLongitude(jToRider.getString("u_long"));
            Is_driver = jToRider.getString("is_driver");
            toRider.setIs_driver(Is_driver);
            rider.setToRider(toRider);

            InProgressRide inProgressRide = new InProgressRide();
            inProgressRide.setmRideId(jRider.getString("ride_id"));
            inProgressRide.setmRideType(jRider.getString("u_ride_type"));

            inProgressRide.setmStartingAddress(jRider.getString("starting_address"));
            inProgressRide.setmEndingAddress(jRider.getString("ending_address"));
            inProgressRide.setmStartLat(jRider.getString("start_lati"));
            inProgressRide.setmStartLang(jRider.getString("start_long"));
            inProgressRide.setmEndLat(jRider.getString("end_lati"));
            inProgressRide.setmEndLang(jRider.getString("end_long"));
            inProgressRide.setmRequestStatus(jRider.getString("request_status"));

            inProgressRide.setmFromRider(fromRider);
            inProgressRide.setmToRider(toRider);

            intent = new Intent(this, MyGroupSelectionActivity.class);
            intent.putExtra("inprogress", "busy");
            intent.putExtra("rideprogress", inProgressRide);
            intent.putExtra("rideUserID", rideUserID);
            intent.putExtra("Is_driver", Is_driver);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return intent;
    }
}

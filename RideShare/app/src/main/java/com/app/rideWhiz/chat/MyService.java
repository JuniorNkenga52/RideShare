package com.app.rideWhiz.chat;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.PrefUtils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rideWhiz.fragment.HomeFragment.mDriverLocation;
import static com.app.rideWhiz.fragment.HomeFragment.mPreDriverLocation;
import static com.app.rideWhiz.fragment.HomeFragment.mUserType;
import static com.app.rideWhiz.fragment.HomeFragment.mWebSocketClient;
import static com.app.rideWhiz.fragment.HomeFragment.updateinterval;
import static com.app.rideWhiz.fragment.HomeFragment.userid;

public class MyService extends Service {

    public static MyXMPP xmpp;
    public static Handler mUpdaterHandler = new Handler();

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PrefUtils.initPreference(getApplicationContext());

        User user = PrefUtils.getUserInfo();

        try {
            ActivityManager am = (ActivityManager) this
                    .getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = "com.app.rideWhiz";
            if (am.getRunningTasks(1).get(0).topActivity.getClassName().equals(
                    packageName + ".activity.StartRideActivity")) {
                // xmpp = MyXMPP.getInstance(CheckBGService.this, user.getmUserId());
                xmpp = MyXMPP.getInstance(MyService.this, Constants.intentKey.jabberPrefix + user.getmUserId());
                xmpp.connect("onCreate");
            }
            /*xmpp = MyXMPP.getInstance(MyService.this, Constants.intentKey.jabberPrefix + user.getmUserId());
            xmpp.connect("onCreate");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return Service.START_STICKY;
    }


    public static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //connectionThread.execute();
            try {
                if (mDriverLocation.distanceTo(mPreDriverLocation) >= 0.5f) {
                    mPreDriverLocation = mDriverLocation;
                    JSONObject jMessage = new JSONObject();
                    jMessage.put("chat_message", "" + mDriverLocation.getLatitude() + "`" + mDriverLocation.getLongitude());
                    jMessage.put("chat_user", userid);
                    jMessage.put("sender_user", "1001");
                    jMessage.put("message_type", "chat-box-html");
                    jMessage.put("message_new", " ");

                    if (mWebSocketClient != null) {
                        if (mWebSocketClient.isClosing() || mWebSocketClient.isClosed()) {
                            Log.w("Message", "Closed >>> ");
                            //MessageUtils.showWarningMessage(context, "Please Wait \n Your Socket connection is Lost Try to Reconnect to the Server");
                            mWebSocketClient.reconnect();
                            Log.w("Message", "Sent >>> " + jMessage.toString());

                        }
                        if (mWebSocketClient.isOpen()) {
                            mWebSocketClient.send(jMessage.toString());
                            Log.w("Message", "Sent >>> " + jMessage.toString());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mPreDriverLocation = mDriverLocation;
            }
            mUpdaterHandler.postDelayed(this, updateinterval);
        }
    };


    @Override
    public void onDestroy() {
        if (mWebSocketClient != null) {
            Log.d("Closed", ">>>> Socket Closed");
            mWebSocketClient.close();
            mWebSocketClient = null;

        }
        Log.w("Service", "Service is destroyed");
        if (mUserType.equals("2")) {
            removeDriver(userid);
        }
        super.onDestroy();
    }


    private void removeDriver(String userid) {
        Log.w("Calling", "Calling now...");
        ApiServiceModule.createService(RestApiInterface.class, this).removeDriverFromList(userid).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject resp;
                try {
                    if (response.body() != null) {
                        resp = new JSONObject(response.body().toString());
                        if (resp.optString("status").equals("success")) {
                            Log.w("Success", "Completed API is Called");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("Failure", "Error in API");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.w("Task Remove Service", "Task Remove is destroyed");
        if(mUserType.equals("2")){
            removeDriver(userid);
        }
    }*/
}
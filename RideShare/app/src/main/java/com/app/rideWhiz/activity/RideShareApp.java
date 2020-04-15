package com.app.rideWhiz.activity;

import android.app.Application;
import android.location.Location;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.app.rideWhiz.listner.SocketConnection;
import com.app.rideWhiz.service.LocationProvider;
import com.app.rideWhiz.utils.PrefUtils;
import com.crashlytics.android.Crashlytics;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import io.fabric.sdk.android.Fabric;

import static com.app.rideWhiz.utils.Constants.WEBSOCKET_ENDPOINT;

public class RideShareApp extends Application implements LocationProvider.LocationCallback, LifeCycleDelegate {
    public static String mUserType = "";
    public static int mHomeTabPos = 0;
    public static int mRideTypeTabPos = 0;
    public static Location mLocation;
    public WebSocketClient mWebSocketSendRequest;
    SocketConnection socketConnection;
    RideShareApp activity;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);
        PrefUtils.initPreference(this);
        activity = this;
        registerLifecycleHandler(new AppLifecycleHandler(this));
        //mLocationProvider = new LocationProvider(this, this);
        // mLocationProvider.connect();
    }

    public static String getmUserType() {
        return mUserType;
    }

    public void setmUserType(String mUserType) {
        this.mUserType = mUserType;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.w("APP Closed", ">>>>>>>>>>>>>>>>>>>>>>> :) :) :) :) :) :) :) :) :)  :) :) :) :) ");
    }

    @Override
    public void handleNewLocation(Location location) {

    }

    public void connectRideRequest() {
        URI uri;
        try {
            uri = new URI(WEBSOCKET_ENDPOINT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketSendRequest = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("Websocket", "Opened");
                socketConnection.onConnected();
            }

            @Override
            public void onMessage(String message) {
                try {
                    if (mWebSocketSendRequest.isOpen()) {
                        try {
                            JSONObject jFromRider = new JSONObject(message);
                            Log.w("Json Data", "Received Data  ::: >>> " + jFromRider.optString("sender_user")
                                    + "\n" + jFromRider.toString());

                            String token = jFromRider.optString("sender_user");
                            /*
                               case "1001" : Car Animation for Real time Location Update
                               case "1002" : Connection Ride Request from Rider to Driver
                               case "1003" : Driver Accept Request from Driver to Rider
                               case "1004" : Driver Reject Request from Driver to Rider
                               case "1005" : Rider Cancel Ride Request from Rider to Driver
                               case "1006" : Driver Start and Finish Ride Request from Driver to Rider
                               case "1007" : Driver Send Map ReRoute Data to Connected Users/Riders
                               case "1008" : Socket Notification for User/Driver Cancel AcceptedRide
                               case "1009" : Ride in not in Current Route
                            */
                            if (token.equals("1002") || token.equals("1003") || token.equals("1004") || token.equals("1005")
                                    || token.equals("1006") || token.equals("1007")|| token.equals("1008")|| token.equals("1009")) {
                                socketConnection.onMessageReceived(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("Websocket", "Client Side Closed " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i("Websocket", "Error " + ex.getMessage());
            }

        };
        mWebSocketSendRequest.setConnectionLostTimeout(0);
        mWebSocketSendRequest.connect();
    }

    public void setSocketConnection(SocketConnection socketConnection) {
        this.socketConnection = socketConnection;
    }

    private void registerLifecycleHandler(AppLifecycleHandler lifeCycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler);
        registerComponentCallbacks(lifeCycleHandler);
    }

    @Override
    public void onAppBackgrounded() {
        Log.w("App is in ", " >>>>>>>>>>>>>>>> Background ");
    }

    @Override
    public void onAppForegrounded() {
        Log.w("App is in ", " >>>>>>>>>>>>>>>> Foreground ");
    }

    @Override
    public void onAppClosed() {
        Log.w("App is in ", " >>>>>>>>>>>>>>>> Closed ");
    }
}

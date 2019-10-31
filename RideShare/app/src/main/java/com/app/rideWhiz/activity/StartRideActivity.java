package com.app.rideWhiz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.RideUsersAdapter;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.AcceptRequest;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.api.response.StartRideResponse;
import com.app.rideWhiz.chat.CommonMethods;
import com.app.rideWhiz.chat.MyXMPP;
import com.app.rideWhiz.listner.CallbackFinishRider;
import com.app.rideWhiz.listner.CallbackStartRider;
import com.app.rideWhiz.listner.OnStartRideListener;
import com.app.rideWhiz.listner.SocketConnection;
import com.app.rideWhiz.model.Directions;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.model.Route;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.notificationservice.ManageNotifications;
import com.app.rideWhiz.service.BatteryInfoReceiver;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.DialogUtils;
import com.app.rideWhiz.utils.MapDirectionAPI;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rideWhiz.activity.ChatActivity.chat_sender_id;
import static com.app.rideWhiz.chat.MyService.xmpp;
import static com.app.rideWhiz.chat.MyXMPP.chatCreated;
import static com.app.rideWhiz.utils.Constants.WEBSOCKET_ENDPOINT;


public class StartRideActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public static String RideStatus = "";
    private final Handler mUpdaterHandler = new Handler();
    public Location previousBestLocation = null;
    Activity activity;
    Context context;
    AcceptRider mRider;
    AcceptRider markerRider;
    User mUserbean;
    User fromUser;
    LatLng currentlthg;
    LatLng pickuplocation;
    LatLng droppfflocation;
    Location mDriverLocation;
    Location mPreDriverLocation;
    Double Latitude, Longitude;
    RideShareApp mApp;
    LatLng DriverLocation;
    LatLng CustomerLocaton;
    Marker DriverMarker;

    WebSocketClient mWebSocketClient;
    CustomProgressDialog mProgressDialog;
    float zoomLevel = 16f;
    int updateinterval = 5000;
    CameraPosition cameraPosition;
    RideShareApp application;
    List<Route> routes;
    String strJsonRoutes;
    List<Route> new_routes;
    LinearLayout layout_new_notification;
    TextView item_txt_counts_notification;
    ImageView ivStartChat;
    MaterialDialog mMaterialDialog;
    String time = "";
    String distance = "";
    boolean isDestroy = false;
    boolean isRejectRide = false;
    ArrayList<LatLng> markerPoints;
    ArrayList<AcceptRider> usersRidersList;
    ArrayList<User> userRidesList;
    ArrayList<LatLng> droplngList = new ArrayList<>();
    CommonMethods commonMethods;
    boolean isSetMarker;
    private ArrayList<Marker> DropoffMarker;
    private ArrayList<Marker> curLocMarker;
    private GoogleMap mGoogleMap;
    private Polyline directionLine;
    private TextView mNameTv;
    private TextView mEmailTv;
    private CircularImageView mProfileIv;
    private RecyclerView list_users;
    private RecyclerView.LayoutManager mLayoutManager;
    private RideUsersAdapter mAdapter;
    private ImageView img_btn_chat;
    private LinearLayout layout_rider;
    private LinearLayout mStartRideLi;
    private Button mStartRideBtn;
    private Button mFinishRideBtn;
    private TextView item_txt_counts;
    private RelativeLayout layout_unreadmsgs;
    private String TabelName;
    private ArrayList<User> newReqList;
    private Dialog dialogFinishRide;
    BatteryInfoReceiver batteryInfoReceiver = new BatteryInfoReceiver();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (mDriverLocation.distanceTo(mPreDriverLocation) >= 0.5f) {
                    mPreDriverLocation = mDriverLocation;
                    animateMarkerNew(DriverMarker, new LatLng(Latitude, Longitude));
                    JSONObject jMessage = new JSONObject();
                    jMessage.put("chat_message", "" + Latitude + "`" + Longitude);
                    jMessage.put("chat_user", "RideWhiz");
                    jMessage.put("sender_user", mRider.getRide_id());
                    jMessage.put("message_type", "chat-box-html");
                    jMessage.put("message_new", " ");
                    if (mWebSocketClient != null) {
                        if (mApp.mWebSocketSendRequest.isClosing() || mApp.mWebSocketSendRequest.isClosed()) {
                            Log.w("Message", "Closed >>> ");
                        }
                        if (mApp.mWebSocketSendRequest.isOpen()) {
                            mWebSocketClient.send(jMessage.toString());
                            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
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
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d("Error", e.toString());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                Log.d("Response for Json", ">>>>>>>>>>>>> \n\n" + json);
                updateLineDestination(json);

            }
        }
    };
    private okhttp3.Callback updateRouteCallback2 = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d("Error", e.toString());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                String json = response.body().string();
                try {
                    Directions directions = new Directions(activity);
                    final List<Route> routes = directions.parse(json);
                    if (routes.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (directionLine != null) directionLine.remove();
                                LatLng riderEndLatLon = new LatLng(Double.parseDouble(mRider.getEnd_lati()), Double.parseDouble(mRider.getEnd_long()));
                                ArrayList<LatLng> routesList = new ArrayList<>();
                                for (LatLng latLngs : routes.get(0).getOverviewPolyLine()) {
                                    if (!RideShareApp.getmUserType().equals("2")) {
                                        if (!AppUtils.checkDistance(riderEndLatLon, latLngs)) {
                                            routesList.add(latLngs);
                                        } else {
                                            routesList.add(riderEndLatLon);
                                            break;
                                        }
                                    } else {
                                        routesList.add(latLngs);
                                    }
                                }
                                directionLine = mGoogleMap.addPolyline((new PolylineOptions())
                                        .addAll(routesList)
                                        .color(ContextCompat.getColor(activity, R.color.blacltext))
                                        .width(10));
                                directionLine.setClickable(true);
                            }
                        });

                    } else {
                        Toast.makeText(context, json, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private okhttp3.Callback updateRouteCallback3 = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d("Error", e.toString());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                Log.d("Response for Json", ">>>>>>>>>>>>> \n\n" + json);
                updateLineDestination2(json);
            }
        }
    };
    private okhttp3.Callback updateRouteCallback4 = new okhttp3.Callback() {

        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            mProgressDialog.dismiss();
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                mProgressDialog.dismiss();
                final String json = response.body().string();
                Log.w(">>>>>>>", "json :: >>>>>>>>>>>>>>>>>>" + json);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Directions directions = new Directions(activity);
                            final List<Route> routes = directions.parse(json);
                            //if (directionLine != null) directionLine.remove();
                            if (routes.size() > 0) {
                                time = routes.get(0).getLegs().get(0).getDuration().getText();
                                distance = routes.get(0).getLegs().get(0).getDistance().getText();
                                showStartRidesListDialog(markerRider);
                                //driver_distance_tv.setText(duration);
                            } else {
                                Toast.makeText(context, json, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
    };
    private BroadcastReceiver mUpdateMessageReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("DESTROY", "2");
            String Sender_ID = intent.getStringExtra("Sender_ID");
            getMessages(Sender_ID);
        }
    };
    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                previousBestLocation = RideShareApp.mLocation;
                Latitude = RideShareApp.mLocation.getLatitude();
                Longitude = RideShareApp.mLocation.getLongitude();

                mDriverLocation = new Location("");
                mDriverLocation.setLatitude(Latitude);
                mDriverLocation.setLongitude(Latitude);
                DriverLocation = new LatLng(Latitude, Longitude);
                setMarker();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_ride_layout);
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                    .findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(this);

            initViews();
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            list_users.setLayoutManager(mLayoutManager);
            newReqList = new ArrayList<>();

            curLocMarker = new ArrayList<>();

            mStartRideBtn.setOnClickListener(this);
            mFinishRideBtn.setOnClickListener(this);

            layout_new_notification.setOnClickListener(this);

            mRider = (AcceptRider) getIntent().getExtras().getSerializable("rideobj");
            pickuplocation = new LatLng(Double.parseDouble(mRider.getStart_lati()), Double.parseDouble(mRider.getStart_long()));
            droppfflocation = new LatLng(Double.parseDouble(mRider.getEnd_lati()), Double.parseDouble(mRider.getEnd_long()));

            // Set Route as per User Type (Driver/Rider)
            requestRoute(pickuplocation, droppfflocation);

            if (getIntent().hasExtra("Is_driver")) {
                if (getIntent().getStringExtra("Is_driver").equals("1")) {
                    ivStartChat.setVisibility(View.GONE);
                    mFinishRideBtn.setVisibility(View.VISIBLE);
                } else {
                    mFinishRideBtn.setVisibility(View.GONE);

                }
            }
            setUserInfo();
            connectWebSocket();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        init();
    }

    private void initViews() {
        activity = this;
        context = this;
        application = (RideShareApp) getApplicationContext();
        commonMethods = new CommonMethods(getApplicationContext());

        PrefUtils.initPreference(this);
        mUserbean = PrefUtils.getUserInfo();
        mProgressDialog = new CustomProgressDialog(this);

        mApp = (RideShareApp) getApplicationContext();
        markerPoints = new ArrayList<>();

        mNameTv = findViewById(R.id.name_tv);
        mEmailTv = findViewById(R.id.email_tv);
        mProfileIv = findViewById(R.id.user_profile);

        mStartRideLi = findViewById(R.id.li1);
        item_txt_counts = findViewById(R.id.item_txt_counts);
        layout_unreadmsgs = findViewById(R.id.layout_unreadmsgs);

        mStartRideBtn = findViewById(R.id.start_ride_btn);
        mFinishRideBtn = findViewById(R.id.finish_ride_btn);

        layout_new_notification = findViewById(R.id.layout_new_notification);
        item_txt_counts_notification = findViewById(R.id.item_txt_counts_notification);

        layout_rider = findViewById(R.id.layout_rider);
        list_users = findViewById(R.id.list_users);
        ivStartChat = findViewById(R.id.ivStartChat);
        img_btn_chat = findViewById(R.id.img_btn_chat);
        ivStartChat.setOnClickListener(this);

        usersRidersList = new ArrayList<>();

    }

    private void requestRoute(LatLng picklng, LatLng droplng) {
        if (picklng != null && droplng != null) {
            if (RideShareApp.getmUserType().equals("2")) {
                LatLng driverlatlng = new LatLng(Double.parseDouble(mRider.getStart_lati()), Double.parseDouble(mRider.getStart_long()));
                MapDirectionAPI.getDirection(driverlatlng, droplng, context).enqueue(updateRouteCallback3);
            } else {
                MapDirectionAPI.getDirection(picklng, droplng, context).enqueue(updateRouteCallback);
            }

        }
    }

    private void updateLineDestination(final String json) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Directions directions = new Directions(StartRideActivity.this);
                    routes = directions.parse(json);

                    if (directionLine != null) {
                        directionLine.remove();
                    }
                    Log.d("Routes Size", ">>>>>>>>>>>>>>>>>>" + routes.size());
                    if (routes.size() > 0) {
                        directionLine = mGoogleMap.addPolyline((new PolylineOptions())
                                .addAll(routes.get(0).getOverviewPolyLine())
                                .color(ContextCompat.getColor(StartRideActivity.this, R.color.blacltext))
                                .width(10));
                    } else {
                        Toast.makeText(context, json, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateLineDestination2(final String json) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Directions directions = new Directions(StartRideActivity.this);
                    if (newReqList.size() > 0) {

                        new_routes = directions.parse(json);
                        if (!checkRoute(new_routes.get(0).getOverviewPolyLine())) {
                            User user = newReqList.get(newReqList.size() - 1);

                            String strdur = new_routes.get(0).getLegs().get(0).getDuration().getText();
                            String strdis = new_routes.get(0).getLegs().get(0).getDistance().getText();

                            float duration = Float.parseFloat(strdur.split("\\s")[0]);
                            float distance;
                            if (strdis.split("\\s")[1].equals("m")) {
                                distance = Float.parseFloat(strdis.split("\\s")[0]) / 1000;
                            } else {
                                distance = Float.parseFloat(strdis.split("\\s")[0]);
                            }
                            float dur = 0;
                            float dis = 0;

                            float lastdur = 0.0f;
                            float lastdis = 0.0f;

                            for (int i = 0; i < userRidesList.size(); i++) {
                                if (dur < Float.parseFloat(userRidesList.get(i).getRide_time())) {
                                    dur += (Float.parseFloat(userRidesList.get(i).getRide_time()));
                                    dis += (Float.parseFloat(userRidesList.get(i).getRide_distance()));
                                    lastdur = Float.parseFloat(userRidesList.get(i).getRide_time());
                                    lastdis = Float.parseFloat(userRidesList.get(i).getRide_distance());
                                } else {
                                    dur = Float.parseFloat(userRidesList.get(i).getAdd_ride_time());
                                    dis = Float.parseFloat(userRidesList.get(i).getAdd_ride_distance());
                                }
                            }

                            float finaldur = duration > lastdur ? duration + dur : 0.0f;
                            float finaldes = distance > lastdis ? distance + dis : 0.0f;

                            user.setAdd_ride_time(String.valueOf(finaldur));
                            user.setRide_time(String.valueOf(lastdur));

                            user.setAdd_ride_distance(String.valueOf(finaldes));
                            user.setRide_distance(String.valueOf(lastdis));

                            newReqList.set(newReqList.size() - 1, user);
                            layout_new_notification.setVisibility(View.VISIBLE);
                            item_txt_counts_notification.setText(String.valueOf(newReqList.size()));
                            AppUtils.playSound(getApplicationContext());
                        }
                    } else {

                        if (!commonMethods.isTableExists("Ploy")) {
                            commonMethods.createTablePloy("Ploy");
                            commonMethods.insertIntoTablePoly("Ploy", json);
                            strJsonRoutes = json;
                        } else {
                            strJsonRoutes = commonMethods.fetchPolyData("Ploy");
                            commonMethods.updateTablePoly("Ploy", json);
                        }

                        routes = directions.parse(strJsonRoutes);

                        LatLng pickuplat = new LatLng(Double.parseDouble(PrefUtils.getString("picklng").split("'")[0]), Double.parseDouble(PrefUtils.getString("picklng").split("'")[1]));
                        LatLng dropplat = new LatLng(Double.parseDouble(PrefUtils.getString("droplng").split("'")[0]), Double.parseDouble(PrefUtils.getString("droplng").split("'")[1]));


                        //PolyUtil.isLocationOnPath(pickuplat, routes.get(0).getOverviewPolyLine() , true)
                        userRidesList = commonMethods.fetchRides("UserRides", false);
                        float oldDistance = AppUtils.getDistance(pickuplat, dropplat);
                        for (int i = 0; i < userRidesList.size(); i++) {
                            User user = userRidesList.get(i);
                            LatLng pickup = new LatLng(Double.parseDouble(user.getStart_lat()), Double.parseDouble(user.getStart_long()));
                            LatLng drop = new LatLng(Double.parseDouble(user.getEnd_lat()), Double.parseDouble(user.getEnd_long()));
                            droplngList.add(oldDistance < AppUtils.getDistance(pickup, drop) ? dropplat : drop);
                        }

                        LatLng finalDestination;
                        if (PrefUtils.getString("finalDestination") == null || PrefUtils.getString("finalDestination").equals("")) {
                            finalDestination = new LatLng(Double.parseDouble(PrefUtils.getString("droplng").split("'")[0]), Double.parseDouble(PrefUtils.getString("droplng").split("'")[1]));
                        } else {
                            finalDestination = new LatLng(Double.parseDouble(PrefUtils.getString("finalDestination").split("'")[0]), Double.parseDouble(PrefUtils.getString("finalDestination").split("'")[1]));
                        }

                        // Update the User Side MAP with New Requested MAP
                        Type listType = new TypeToken<ArrayList<LatLng>>() {
                        }.getType();
                        Gson gson = new Gson();
                        String droplngListJson = gson.toJson(droplngList, listType);

                        Type pickUp = new TypeToken<LatLng>() {
                        }.getType();
                        Gson gson2 = new Gson();
                        String pickUpGson = gson2.toJson(pickuplat, pickUp);

                        Type finalDes = new TypeToken<LatLng>() {
                        }.getType();
                        Gson gson3 = new Gson();
                        String finalDesGson = gson3.toJson(finalDestination, finalDes);
                        if (usersRidersList.size() > 1) {
                            sendSocketUserReRouteMap(droplngListJson + "'" + "" + pickUpGson + "'" + finalDesGson);
                        }

                        requestMultipleRoute(pickuplat, droplngList, finalDestination);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDestroy = false;

        newReqList.clear();
        layout_new_notification.setVisibility(View.GONE);

        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationReceiver, new IntentFilter("update-location"));
        LocalBroadcastManager.getInstance(StartRideActivity.this).registerReceiver(mUpdateMessageReciver, new IntentFilter("update_message"));
        if (!chat_sender_id.equals("")) {
            getMessages(chat_sender_id);
        } else {
            getMessages("");
        }

        try {
            if (batteryInfoReceiver != null) {
                registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mContentReceiver != null) {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mContentReceiver, new IntentFilter("ContentResponse"));
        }
        setListener();
    }


    @Override
    public void onBackPressed() {

        //Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
        if (mRider.getRequest_status().equals("3")) {

            mMaterialDialog = new MaterialDialog(this)
                    .setTitle("Warning")
                    .setMessage("You have currently 1 Ride active.Are you want to continue?")
                    .setPositiveButton("Yes i'm in", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();

                        }
                    })
                    .setNegativeButton("no Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mUpdaterHandler.removeCallbacks(runnable);
                            mUpdaterHandler.removeCallbacksAndMessages(null);
                            if (RideShareApp.getmUserType().equals("2")) {
                                if (usersRidersList.size() > 1) {
                                    showFinishRidesListDialog(usersRidersList, "Finish Ride");
                                } else {
                                    endRide(mRider.getRide_id(), "1", "4", usersRidersList.get(0).getFromRider().getnUserId(), usersRidersList.get(0).getToRider().getnUserId(), 0, "2");
                                }
                            } else {
                                endRide(mRider.getRide_id(), "1", "4", mRider.getFromRider().getnUserId(), mRider.getToRider().getnUserId(), 0, "1");
                            }
                            mMaterialDialog.dismiss();
                        }
                    });

            mMaterialDialog.show();
        } else {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(StartRideActivity.this);
            builder.setTitle("Cancel Ride")
                    .setMessage("Are you sure you want to cancel ride?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        acceptOrRejectRequest(mRider.getRide_id(), "2", context);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(StartRideActivity.this).unregisterReceiver(mUpdateMessageReciver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
        }
        MyXMPP.destroy_connect();
        Log.w("DESTROY", RideShareApp.getmUserType());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        try {
            currentlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
            Log.d("Bearing", "" + RideShareApp.mLocation.getBearing());
            if (zoomLevel <= 4.0f) {
                zoomLevel = 16.0f;
            }
            cameraPosition = new CameraPosition.Builder().target(currentlthg).zoom(zoomLevel).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mDriverLocation = new Location("");
            mDriverLocation.setLatitude(DriverLocation.latitude);
            mDriverLocation.setLongitude(DriverLocation.longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DropoffMarker == null) {
            DropoffMarker = new ArrayList<>();
            for (AcceptRider rider : usersRidersList) {
                DropoffMarker.add(mGoogleMap.addMarker(new MarkerOptions().title(rider.getFromRider().getmFirstName() + "'s Drop off Location").snippet(rider.getEnding_address()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_start))
                        .position(new LatLng(Double.parseDouble(rider.getEnd_lati()), Double.parseDouble(rider.getEnd_long())))));
            }

            if (!RideShareApp.getmUserType().equals("2")) {
                DropoffMarker.add(mGoogleMap.addMarker(new MarkerOptions().title("Drop off Location").snippet(mRider.getEnding_address()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_start))
                        .position(new LatLng(Double.parseDouble(mRider.getEnd_lati()), Double.parseDouble(mRider.getEnd_long())))));
            }
        }


        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoomLevel = mGoogleMap.getCameraPosition().zoom;
            }
        });

        if (RideShareApp.getmUserType().equals("2")) {
            mUpdaterHandler.post(runnable);
        }
    }

    private void setListener() {
        if (RideShareApp.getmUserType().equals("2")) {
            if (mApp.mWebSocketSendRequest == null) {
                mApp.connectRideRequest();
            } else if (mApp.mWebSocketSendRequest.isClosed() || mApp.mWebSocketSendRequest.isClosing()) {
                mApp.connectRideRequest();
            }
        }
        mApp.setSocketConnection(new SocketConnection() {
            @Override
            public void onMessageReceived(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (mRider.getRide_id().equals(jsonObject.getString("username"))) {
                        if (jsonObject.optString("sender_user").equals("1007")) {
                            if (!RideShareApp.getmUserType().equals("2")) {
                                String data = jsonObject.optString("chat_message");

                                Type listType = new TypeToken<ArrayList<LatLng>>() {
                                }.getType();
                                Type pickUp = new TypeToken<LatLng>() {
                                }.getType();
                                Type finalDes = new TypeToken<LatLng>() {
                                }.getType();

                                Gson gsonData = new Gson();
                                ArrayList<LatLng> listLatLng = gsonData.fromJson(data.split("'")[0], listType);
                                LatLng pickup = gsonData.fromJson(data.split("'")[1], pickUp);
                                LatLng finalLatLng = gsonData.fromJson(data.split("'")[2], finalDes);
                                if (pickup != null && finalLatLng != null) {
                                    markerPoints = new ArrayList<>();
                                    markerPoints.clear();
                                    markerPoints.add(pickup);
                                    markerPoints.addAll(listLatLng);
                                    MapDirectionAPI.getWayPointsDirection(pickup, finalLatLng, context, markerPoints).enqueue(updateRouteCallback2);
                                }
                            }
                        } else if (jsonObject.optString("sender_user").equals("1008")) {

                            final String[] data = jsonObject.optString("chat_message").split(",");
                            if (mRider.getRide_id().equals(jsonObject.getString("username"))) {
                                if (RideShareApp.getmUserType().equals("2")) {
                                    if (commonMethods.fetchRidersList("Riders").size() > 1) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.updateData(data[1]);
                                                commonMethods.deleteRideRecord("UserRides", mUserbean.getmUserId());
                                                commonMethods.deleteRidersRecord("Riders", mUserbean.getmUserId());
                                                usersRidersList = commonMethods.fetchRidersList("Riders");
                                                PrefUtils.putString("finalDestination", "");
                                                commonMethods.deletePolyTable("Ploy");
                                            }
                                        });
                                    } else {
                                        clearRideData();
                                        Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                                        i.putExtra("inprogress", "free");
                                        startActivity(i);
                                        finish();
                                        //MyXMPP.disconnect();
                                    }

                                } else {
                                    if (data[0].equals("2")) {
                                        clearRideData();
                                        Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                                        i.putExtra("inprogress", "free");
                                        startActivity(i);
                                        finish();
                                        //MyXMPP.disconnect();
                                    }
                                }

                            }
                        }
                    } else if (mUserbean.getmUserId().equals(jsonObject.getString("username"))) {
                        switch (jsonObject.optString("sender_user")) {
                            case "1002":
                                if (mRider.getToRider().getIs_driver().equals("1")) {
                                    // New Ride Request Arrives to Driver
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<User>() {
                                    }.getType();
                                    fromUser = gson.fromJson(jsonObject.getString("chat_message"), type);
                                    fromUser.setIs_new_request(true);
                                    newReqList.add(fromUser);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LatLng pickuplat = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
                                            LatLng dropplat = new LatLng(Double.parseDouble(fromUser.getEnd_lat()), Double.parseDouble(fromUser.getEnd_long()));
                                            MapDirectionAPI.getDirection(pickuplat, dropplat, context).enqueue(updateRouteCallback3);
                                        }
                                    });
                                }
                                break;
                            case "1005":
                                Gson gson = new Gson();
                                Type type = new TypeToken<User>() {
                                }.getType();
                                User user = gson.fromJson(jsonObject.getString("chat_message"), type);
                                for (int i = 0; i < newReqList.size(); i++) {
                                    if (newReqList.get(i).getmUserId().equals(user.getmUserId())) {
                                        newReqList.remove(i);
                                        break;
                                    }
                                }
                                if (newReqList.size() > 0) {
                                    layout_new_notification.setVisibility(View.VISIBLE);
                                    item_txt_counts_notification.setText(String.valueOf(newReqList.size()));
                                } else {
                                    layout_new_notification.setVisibility(View.GONE);
                                }
                                break;

                            case "1006":
                                if (jsonObject.optString("chat_message").split("'")[0].equals("RideFinished")) {
                                    // Driver/User Finish the Ride Driver/User Get the Notification
                                    ManageNotifications.sendNotification(activity, null, "Ride Finished", "1006");
                                    setUserStartFinishRide("4", jsonObject.optString("chat_message").split("'")[1]);
                                } else {
                                    // Driver Start the Ride User Get the Notification form Driver
                                    ManageNotifications.sendNotification(activity, mRider, "Ride Started", "1006");
                                    setUserStartFinishRide("3", "");
                                }
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnected() {

            }
        });
    }

    public void setMarker() {
        try {
            if (DriverMarker == null) {
                DriverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin)).position(DriverLocation));
            }

            if (!isSetMarker) {
                isSetMarker = true;
                if (usersRidersList.size() > 0) {
                    for (Marker m : curLocMarker) {
                        m.remove();
                    }
                    curLocMarker.clear();
                    for (AcceptRider rider : usersRidersList) {
                        setcutommarker(rider);
                    }
                } else {
                    setcutommarker(mRider);
                }
            } else {
                curLocMarker.get(curLocMarker.size() - 1).setPosition(CustomerLocaton);
            }
        } catch (Exception e) {
            //mPreDriverLocation = mDriverLocation;
        }
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(WEBSOCKET_ENDPOINT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                try {
                    final JSONObject jobj = new JSONObject(s);
                    //if (mApp.getmUserType().equals("1")) {
                    if (!jobj.getString("message_type").equals("chat-connection-ack")) {
                        if (!jobj.getString("chat_message").equals("null") && jobj.getString("sender_user").equals(mRider.getRide_id())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // MessageUtils.showSuccessMessage((StartRideActivity.this, "received");
                                        String[] updatedlocation = jobj.getString("chat_message").split("`");
                                        double mlet = Double.parseDouble(updatedlocation[0]);
                                        double mlong = Double.parseDouble(updatedlocation[1]);

                                        mDriverLocation = new Location("");
                                        mDriverLocation.setLatitude(mlet);
                                        mDriverLocation.setLongitude(mlong);

                                        //if (!mRider.getRequest_status().equals("3"))
                                        animateMarkerNew(DriverMarker, new LatLng(mlet, mlong));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }
                    //}
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                /*if (!isDestroy)
                    mWebSocketClient.reconnect();*/
            }

            @Override
            public void onError(final Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                //if (!isDestroy)
                    /*if (mWebSocketClient != null)
                        mWebSocketClient.reconnect();*/
            }
        };
        mWebSocketClient.connect();
    }


    /********************** XMPP **********************/

    private void init() {
        xmpp = MyXMPP.getInstance(activity, Constants.intentKey.jabberPrefix + mUserbean.getmUserId());
        xmpp.connect("onCreate");
    }

    private void getMessages(String sender_id) {
        Rider toRider = null;

        if (usersRidersList != null && usersRidersList.size() > 0 && !sender_id.equals("")) {
            for (int i = 0; i < usersRidersList.size(); i++) {
                if (usersRidersList.get(i).getFromRider().getnUserId().equals(sender_id)) {
                    toRider = usersRidersList.get(i).getFromRider();
                }
            }
        } else if (PrefUtils.getUserInfo().getmUserId().equals(mRider.getFromRider().getnUserId()))
            toRider = mRider.getToRider();
        else
            toRider = mRider.getFromRider();

        TabelName = Constants.intentKey.jabberPrefix + (toRider != null ? toRider.getnUserId() : "");

        if (commonMethods.isTableExists(TabelName.toLowerCase())) {
            String unreadCount = getUnreadMessages(TabelName.toLowerCase());
            if (!unreadCount.equals("")) {
                if (usersRidersList != null) {
                    for (int i = 0; i < usersRidersList.size(); i++) {
                        if (usersRidersList.get(i).getFromRider().getnUserId()
                                .equals(toRider != null ? toRider.getnUserId() : "") && toRider.getIs_new_request()) {
                            usersRidersList.get(i).getFromRider().setRideCount(Integer.parseInt(unreadCount));
                        }
                    }
                }
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (ivStartChat.getVisibility() == View.VISIBLE) {
                    if (Integer.parseInt(unreadCount) > 0) {
                        layout_unreadmsgs.setVisibility(View.VISIBLE);
                        item_txt_counts.setText(unreadCount);
                    } else {
                        layout_unreadmsgs.setVisibility(View.GONE);
                    }
                }
            } else {
                layout_unreadmsgs.setVisibility(View.GONE);
            }

        }
    }

    private float getBearing(LatLng begin, LatLng end) {

        double PI = 3.14159;
        double lat1 = begin.latitude * PI / 180;
        double long1 = begin.longitude * PI / 180;
        double lat2 = end.latitude * PI / 180;
        double long2 = end.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return (float) brng;
    }

    private void animateMarkerNew(final Marker marker, final LatLng newlatlng) {

        final Location destination = new Location(LocationManager.GPS_PROVIDER);
        destination.setLatitude(newlatlng.latitude);
        destination.setLongitude(newlatlng.longitude);

        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(updateinterval); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(getBearing(startPosition, endPosition));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(newlatlng).zoom(zoomLevel).build()));

                }
            });
            valueAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_ride_btn:
                RideStatus = "finished";

                if (RideShareApp.getmUserType().equals("2")) {
                    if (usersRidersList.size() > 1) {
                        showFinishRidesListDialog(usersRidersList, "Finish Ride");
                    } else {
                        endRide(mRider.getRide_id(), "1", "4", usersRidersList.get(0).getFromRider().getnUserId(), usersRidersList.get(0).getToRider().getnUserId(), 0, "2");
                    }
                } else {
                    endRide(mRider.getRide_id(), "1", "4", mRider.getFromRider().getnUserId(), mRider.getToRider().getnUserId(), 0, "1");
                }


                break;
            case R.id.start_ride_btn:
                for (int i = 0; i < usersRidersList.size(); i++) {
                    Gson gson = new Gson();
                    mAdapter.updateIcon(usersRidersList.get(i).getFromRider().getnUserId());
                    //Update Riders Database
                    usersRidersList.get(i).getFromRider().setIs_new_request(false);
                    commonMethods.updateTableRiders("Riders", usersRidersList.get(i).getFromRider().getnUserId(), gson.toJson(usersRidersList.get(i)));
                    startRide(usersRidersList.get(i));
                }

                break;
            case R.id.layout_new_notification:
                Gson gson = new Gson();
                for (User users : newReqList) {
                    commonMethods.insertIntoTableRides("UserRides", users.getmUserId(), gson.toJson(users));
                }
                startActivity(new Intent(context, NotificationViewActivity.class));

                break;
            case R.id.ivStartChat:
                isRejectRide = true;
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.intentKey.SelectedChatUser, mRider);
                startActivity(intent);

                break;
        }
    }

    public String getUnreadMessages(String tableName) {
        String tblName = "'" + tableName + "'";
        SQLiteDatabase myDb;
        myDb = openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);
        Cursor allRows = myDb.rawQuery("SELECT * FROM " + tblName + " WHERE msgtype = 'false' AND who = 'r'", null);
        System.out.println("COUNT : " + allRows.getCount());
        return String.valueOf(allRows.getCount());
    }

    private void setcutommarker(AcceptRider rider) {
        try {
            LatLng currentDriverPos = new LatLng(Double.parseDouble(rider.getStart_lati()), Double.parseDouble(rider.getStart_long()));
            getMarkerBitmapFromView(activity, rider, currentDriverPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMarkerBitmapFromView(final Activity activity, final AcceptRider rider, final LatLng currentDriverPos) {

        try {
            final View customMarkerView = activity.getLayoutInflater().inflate(R.layout.item_custom_marker, null);

            CircleImageView markerImageView = customMarkerView.findViewById(R.id.user_dp);
            String userimage = "";

            userimage = rider.getFromRider().getThumb_image();
            if (userimage == null) {
                userimage = "";
            }

            if (!userimage.equals("")) {
                Glide
                        .with(activity)
                        .load(userimage)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                try {
                                    curLocMarker.add(mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider))
                                            .position(currentDriverPos).anchor(0.5f, 1f)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                                            .rotation(0f)
                                            .flat(true)));
                                } catch (Exception ew) {
                                    ew.printStackTrace();
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Bitmap icon = AppUtils.drawableToBitmap(resource);
                                Bitmap bmImg;
                                try {
                                    if (RideShareApp.getmUserType().equals("2")) {
                                        if (rider.getFromRider().getIs_new_request()) {
                                            bmImg = AppUtils.getMarkerBitmapFromView(activity, icon, false, false, "");
                                        } else {
                                            bmImg = AppUtils.getMarkerBitmapFromView(activity, icon, true, false, "");
                                        }
                                        LatLng markerLatLon = new LatLng(Double.parseDouble(rider.getStart_lati()), Double.parseDouble(rider.getStart_long()));
                                        curLocMarker.add(mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider))
                                                //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                                .position(markerLatLon).anchor(0.5f, 1f)
                                                .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                                // Specifies the anchor to be at a particular point in the marker image.
                                                .rotation(0f)
                                                .flat(true)));
                                    } else {
                                        bmImg = AppUtils.getMarkerBitmapFromView(activity, icon, false, false, "");
                                        curLocMarker.add(mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider))
                                                //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                                .position(currentDriverPos).anchor(0.5f, 1f)
                                                .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                                // Specifies the anchor to be at a particular point in the marker image.
                                                .rotation(0f)
                                                .flat(true)));
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                return false;
                            }

                        }).error(R.drawable.ic_user_pin).placeholder(R.drawable.ic_user_pin).dontAnimate().into(markerImageView);
            } else {
                curLocMarker.add(mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                        .position(currentDriverPos)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void acceptOrRejectRequest(final String mRideId, String acceptOrreject, Context context) {
        Log.w("DESTROY", "8");
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).declineRequestNotification(mUserbean.getmUserId(), mRideId, acceptOrreject, RideShareApp.getmUserType()).enqueue(new Callback<AcceptRequest>() {
            @Override
            public void onResponse(Call<AcceptRequest> call, Response<AcceptRequest> response) {
                Log.w("ONBACK-S", "N");
                removeDriver(mUserbean.getmUserId());
            }

            @Override
            public void onFailure(Call<AcceptRequest> call, Throwable t) {
                Log.w("ONBACK-F", "N");
            }
        });
    }

    //Send Socket Notification for User/Driver Cancel AcceptedRide
    private void sendSocketCancelAcceptedRide(String is_driver) {
        final JSONObject jMessage = new JSONObject();
        try {
            jMessage.put("chat_message", is_driver + "," + mUserbean.getmUserId());
            jMessage.put("chat_user", mRider.getRide_id());
            jMessage.put("sender_user", "1008");
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");

            if (mApp.mWebSocketSendRequest != null) {
                if (mApp.mWebSocketSendRequest.isClosing() || (mApp.mWebSocketSendRequest.isClosed())) {
                    Log.w("Message", "Closed >>> ");
                    mApp.connectRideRequest();
                    mApp.setSocketConnection(new SocketConnection() {
                        @Override
                        public void onMessageReceived(String response) {


                        }

                        @Override
                        public void onConnected() {
                            mApp.mWebSocketSendRequest.send(jMessage.toString());
                        }
                    });
                    Log.w("Message", "Sent >>> " + jMessage.toString());
                }
                mApp.mWebSocketSendRequest.send(jMessage.toString());
            }

            mProgressDialog.cancel();
            clearRideData();

            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
            i.putExtra("inprogress", "free");
            startActivity(i);
            finish();
            MyXMPP.disconnect();
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Send Socket Notification for Start and Finish Ride
    private void sendSocketStartFinishRide(String mType, String userid, String msg, int pos) {
        final JSONObject jMessage = new JSONObject();
        try {
            jMessage.put("chat_message", msg);
            jMessage.put("chat_user", userid);
            jMessage.put("sender_user", "1006");
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");

            if (mApp.mWebSocketSendRequest != null) {
                if (mApp.mWebSocketSendRequest.isClosing() || (mApp.mWebSocketSendRequest.isClosed())) {
                    Log.w("Message", "Closed >>> ");
                    mApp.connectRideRequest();
                    mApp.setSocketConnection(new SocketConnection() {
                        @Override
                        public void onMessageReceived(String response) {

                        }

                        @Override
                        public void onConnected() {
                            mApp.mWebSocketSendRequest.send(jMessage.toString());
                        }
                    });
                    Log.w("Message", "Sent >>> " + jMessage.toString());
                }
                if (mApp.mWebSocketSendRequest.isOpen()) {
                    mApp.mWebSocketSendRequest.send(jMessage.toString());

                } else {
                    mApp.connectRideRequest();
                    mApp.setSocketConnection(new SocketConnection() {
                        @Override
                        public void onMessageReceived(String response) {

                        }

                        @Override
                        public void onConnected() {
                            mApp.mWebSocketSendRequest.send(jMessage.toString());
                        }
                    });
                }
            }
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());

            if (mType.equals("3")) { // for Start Ride
                ivStartChat.setVisibility(View.GONE);
                mStartRideBtn.setVisibility(View.GONE);
                mFinishRideBtn.setVisibility(View.VISIBLE);
                mRider.setRequest_status("3");
                for (Marker m : curLocMarker) {
                    m.remove();
                }
                curLocMarker.clear();
                for (AcceptRider rider : usersRidersList) {
                    setcutommarker(rider);
                }
            } else if (mType.equals("4")) { // for Finish Ride

                mUpdaterHandler.removeCallbacks(runnable);
                mUpdaterHandler.removeCallbacksAndMessages(null);
                //if (mRider.getToRider().getIs_driver().equals("1")) {
                if (RideShareApp.getmUserType().equals("2")) {
                    //Driver
                    if (commonMethods.fetchRidersList("Riders").size() > 1) {
                        PrefUtils.putString("finalDestination", "");
                        commonMethods.deletePolyTable("Ploy");
                        mAdapter.updateData(userid);
                        commonMethods.deleteRideRecord("UserRides", userid);
                        commonMethods.deleteRidersRecord("Riders", userid);
                        usersRidersList = commonMethods.fetchRidersList("Riders");
                        if (userRidesList.size() == 1) {
                            if (userRidesList.get(0).getIs_new_request()) {
                                mFinishRideBtn.setVisibility(View.GONE);
                            } else {
                                mFinishRideBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        clearRideData();

                        Intent i = new Intent(StartRideActivity.this, HomeNewActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        activity.finish();
                    }

                    Marker marker = curLocMarker.get(pos);
                    marker.remove();
                    curLocMarker.remove(pos);
                } else {
                    //User
                    if (mApp.mWebSocketSendRequest != null) {
                        mApp.mWebSocketSendRequest.close();
                        mApp.mWebSocketSendRequest = null;
                    }
                    Intent rateride = new Intent(StartRideActivity.this, RideRateActivity.class);
                    rateride.putExtra("riderate", mRider.getRide_id());
                    rateride.putExtra("driverid", mRider.getFromRider().getnUserId());
                    startActivity(rateride);
                    activity.finish();
                }
                mProgressDialog.cancel();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Send Socket Notification for User ReRoute Map
    private void sendSocketUserReRouteMap(String jsonData) {
        final JSONObject jMessage = new JSONObject();
        try {
            jMessage.put("chat_message", jsonData);
            jMessage.put("chat_user", mRider.getRide_id());
            jMessage.put("sender_user", "1007");
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");

            if (mApp.mWebSocketSendRequest != null) {
                if (mApp.mWebSocketSendRequest.isClosing() || (mApp.mWebSocketSendRequest.isClosed())) {
                    Log.w("Message", "Closed >>> ");
                    mApp.connectRideRequest();
                    mApp.setSocketConnection(new SocketConnection() {
                        @Override
                        public void onMessageReceived(String response) {

                        }

                        @Override
                        public void onConnected() {
                            mApp.mWebSocketSendRequest.send(jMessage.toString());
                        }
                    });
                    Log.w("Message", "Sent >>> " + jMessage.toString());
                }
                Log.w("Data to be Sent", "DONE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + jsonData);
                mApp.mWebSocketSendRequest.send(jMessage.toString());
            }
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update UI when Start or Finish Notification Arrives.
    private void setUserStartFinishRide(String type, final String userid) {
        if (type.equals("3")) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivStartChat.setVisibility(View.GONE);
                        mStartRideLi.setVisibility(View.VISIBLE);
                        mFinishRideBtn.setVisibility(View.VISIBLE);
                        MessageUtils.showSuccessMessage(StartRideActivity.this, "Ride Started");
                        mRider.setRequest_status("3");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            //RideShareApp.getmUserType().equals("2")
            //if (mRider.getToRider().getIs_driver().equals("1")) {
            if (RideShareApp.getmUserType().equals("2")) {
                if (commonMethods.fetchRidersList("Riders").size() > 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.updateData(userid);
                            commonMethods.deleteRideRecord("UserRides", userid);
                            commonMethods.deleteRidersRecord("Riders", userid);
                            usersRidersList = commonMethods.fetchRidersList("Riders");
                            PrefUtils.putString("finalDestination", "");
                            commonMethods.deletePolyTable("Ploy");
                        }
                    });
                } else {
                    clearRideData();
                    Intent i = new Intent(StartRideActivity.this, HomeNewActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    mUpdaterHandler.removeCallbacks(runnable);
                    mUpdaterHandler.removeCallbacksAndMessages(null);
                    activity.finish();
                }
                /*Marker marker = curLocMarker.get(pos);
                marker.remove();
                curLocMarker.remove(pos);*/
            } else {
                Intent rateride = new Intent(StartRideActivity.this, RideRateActivity.class);
                rateride.putExtra("riderate", mRider.getRide_id());
                rateride.putExtra("driverid", mRider.getFromRider().getnUserId());
                startActivity(rateride);
                activity.finish();
            }

            mProgressDialog.cancel();
        }
    }

    // API for Start Ride
    private void startRide(final AcceptRider rider) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(rider.getRide_id(), "1",
                "3", rider.getFromRider().getnUserId(),
                "" + rider.getEnd_lati(), "" + rider.getEnd_long(), "2").enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RideStarted", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + response.body().getMsg());
                    // Driver send Notification to User to Start is Started
                    sendSocketStartFinishRide("3", rider.getFromRider().getnUserId(), "RideStarted" + "'" + "", 0);
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<StartRideResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    // API for End Ride
    private void endRide(String mId, String check_driver, final String mType, final String userid, final String driverID, final int pos, String u_ride_type) {

        mProgressDialog.show();
        //destinationLatLang = new LatLng(Double.parseDouble(location.getmLatitude()), Double.parseDouble(location.getmLongitude()));
        ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver,
                mType, userid,
                "" + RideShareApp.mLocation.getLatitude(), "" + RideShareApp.mLocation.getLongitude(), u_ride_type).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (RideShareApp.getmUserType().equals("2")) {
                        // Update Driver Side
                        sendSocketStartFinishRide("4", userid, "RideFinished" + "'" + driverID, pos);
                    } else {
                        // Update User Side
                        sendSocketStartFinishRide("4", driverID, "RideFinished" + "'" + userid, pos);
                    }

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<StartRideResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });

    }

    // call Google Service To Fetch the New Updated Route using WayPoints
    private void requestMultipleRoute(LatLng picklng, ArrayList<LatLng> droplng, LatLng finalDestination) {
        if (picklng != null && droplng != null) {
            markerPoints = new ArrayList<>();
            markerPoints.clear();
            if (usersRidersList.size() > 1) {
                markerPoints.add(picklng);
                markerPoints.addAll(droplng);
            }
            MapDirectionAPI.getWayPointsDirection(picklng, finalDestination, context, markerPoints).enqueue(updateRouteCallback2);
        }
    }

    // Car Pool Integration

    // if New Request Fells within New Current route then Update the Route
    private void setMultipleRoute(final List<List<HashMap<String, String>>> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.BLACK);
                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    //directionLine.remove();8
                    mGoogleMap.addPolyline(lineOptions);
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }
            }
        });
    }

    // Check New Request fells within the Current Route
    private boolean checkRoute(List<LatLng> newRoute) {
        boolean exceededTolerance = false;

        List<LatLng> gpsPoints = routes.get(0).getOverviewPolyLine();
        int size = gpsPoints.size() < newRoute.size() ? gpsPoints.size() : newRoute.size();

        String destination1 = mRider.getEnd_lati() + "'" + mRider.getEnd_long();//Ketal
        String destination2 = fromUser.getEnd_lat() + "'" + fromUser.getEnd_long();//law garden
        String finalDestination = gpsPoints.size() > newRoute.size() ? destination1 : destination2;

        PrefUtils.putString("finalDestination", finalDestination);
        for (int i = 0; i < size; i++) {
            if (AppUtils.getDistance(gpsPoints.get(i), newRoute.get(i)) > 1000) {
                exceededTolerance = true;
                break;
            }
        }
        if (exceededTolerance) {
            sendCancelSocketRideRequest(fromUser.getmUserId());
            Toast.makeText(context, "Route Not In the Current Ride !!", Toast.LENGTH_LONG).show();
            Log.w("", "User deviated from path");
        } else {

            Toast.makeText(context, "Route Connected !!", Toast.LENGTH_LONG).show();
            Log.w("", "User is in between path");
        }

        return exceededTolerance;
    }

    private void sendCancelSocketRideRequest(String userid) {

        try {
            final JSONObject jMessage = new JSONObject();
            Gson gson = new Gson();
            jMessage.put("chat_message", "Ride in not in Current Route");
            jMessage.put("chat_user", userid);
            jMessage.put("sender_user", "1009");
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");

            if (mApp.mWebSocketSendRequest == null) {
                mApp.connectRideRequest();
                mApp.setSocketConnection(new SocketConnection() {
                    @Override
                    public void onMessageReceived(String response) {

                    }

                    @Override
                    public void onConnected() {
                        mApp.mWebSocketSendRequest.send(jMessage.toString());
                    }
                });
            } else {
                if (mApp.mWebSocketSendRequest.isOpen()) {
                    mApp.mWebSocketSendRequest.send(jMessage.toString());
                }
            }
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Call Finish Ride Selection Dialog
    private void showFinishRidesListDialog(ArrayList<AcceptRider> ridersList, String msg) {

        dialogFinishRide = new DialogUtils(StartRideActivity.this).buildDialogFinishRide(new CallbackFinishRider() {
            @Override
            public void onCreate(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onCancle(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onError(String error, Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onselectCarFeatures(String rideID, String userid, String driverId, int pos, Dialog dialog) {
                Log.e("", "onselectCarFeatures-" + rideID);
                endRide(rideID, "1", "4", userid, driverId, pos, "2");

            }

        }, ridersList, msg, StartRideActivity.this);

        if (dialogFinishRide != null && !dialogFinishRide.isShowing()) {
            dialogFinishRide.show();
        }

    }

    // Call Start Ride Selection Dialog
    private void showStartRidesListDialog(final AcceptRider rider) {

        dialogFinishRide = new DialogUtils(StartRideActivity.this).buildDialogStartRide(new CallbackStartRider() {
            @Override
            public void onCreate(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onCancle(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onStart(Dialog dialog) {
                dialog.dismiss();
                Gson gson = new Gson();
                mAdapter.updateIcon(rider.getFromRider().getnUserId());
                //Update Riders Database
                rider.getFromRider().setIs_new_request(false);
                commonMethods.updateTableRiders("Riders", rider.getFromRider().getnUserId(), gson.toJson(rider));
                startRide(rider);
                dialog.dismiss();
            }

            @Override
            public void onChat(Dialog dialog) {
                isRejectRide = true;
                chatCreated = false;
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.intentKey.SelectedChatUser, rider);
                startActivity(intent);
            }
        }, rider.getFromRider().getmFirstName(), time, distance, StartRideActivity.this);

        if (dialogFinishRide != null && !dialogFinishRide.isShowing()) {
            dialogFinishRide.show();
        }
    }

    // Set Visibility As User Type (Rider/Driver)
    private void setUserInfo() {
        if (RideShareApp.getmUserType().equals("2")) {
            if (mUserbean.getmUserId().equals(mRider.getFromRider().getnUserId())) {
                DriverLocation = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                CustomerLocaton = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
            } else if (mUserbean.getmUserId().equals(mRider.getToRider().getnUserId())) {
                DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
            }
            mStartRideLi.setVisibility(View.VISIBLE);

            list_users.setVisibility(View.VISIBLE);
            usersRidersList = commonMethods.fetchRidersList("Riders");

            for (int i = 0; i < usersRidersList.size(); i++) {
                if (!usersRidersList.get(i).getFromRider().getIs_new_request()) {
                    mFinishRideBtn.setVisibility(View.VISIBLE);
                    break;
                }
            }
            if (mRider.getRequest_status().equals("3")) {
                mStartRideBtn.setVisibility(View.GONE);
                mFinishRideBtn.setVisibility(View.VISIBLE);
            } else {
                mStartRideBtn.setVisibility(View.VISIBLE);
                mFinishRideBtn.setVisibility(View.GONE);
            }
            mAdapter = new RideUsersAdapter(activity, usersRidersList);
            list_users.setAdapter(mAdapter);
            mAdapter.OnStartRide(new OnStartRideListener() {
                @Override
                public void OnChatClick(int pos) {
                    if (usersRidersList.get(pos).getFromRider().getIs_new_request()) {

                        if (curLocMarker.size() > 0) {
                            markerRider = new Gson().fromJson(curLocMarker.get(pos).getSnippet(), AcceptRider.class);
                            /*LatLng source = new LatLng(Double.parseDouble(markerRider.getStart_lati()), Double.parseDouble(markerRider.getStart_long()));*/
                            LatLng source = new LatLng(curLocMarker.get(pos).getPosition().latitude, curLocMarker.get(pos).getPosition().longitude);
                            LatLng dest = new LatLng(Double.parseDouble(markerRider.getEnd_lati()), Double.parseDouble(markerRider.getEnd_long()));
                            if (markerRider.getFromRider().getIs_new_request()) {
                                getRiderInfo(source, dest);
                            }
                        } else {
                            MessageUtils.showSuccessMessage(context, "Getting your Location !");
                        }

                    } else {
                        MessageUtils.showSuccessMessage(context, "You can not chat while your ride stared .!!");
                    }
                }
            });

        } else {
            layout_rider.setVisibility(View.VISIBLE);
            if (mUserbean.getmUserId().equals(mRider.getFromRider().getnUserId())) {
                try {
                    mNameTv.setText(mRider.getToRider().getmFirstName());
                    mEmailTv.setText(mRider.getToRider().getmEmail());
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                    if (mRider.getToRider().getThumb_image() != null) {
                        if (!mRider.getToRider().getThumb_image().equals("")) {
                            Glide.with(this).load(mRider.getToRider().getThumb_image())
                                    .error(R.drawable.user_icon)
                                    .transition(new DrawableTransitionOptions()
                                            .crossFade())
                                    .dontAnimate()
                                    .into(mProfileIv);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (mUserbean.getmUserId().equals(mRider.getToRider().getnUserId())) {
                try {
                    mNameTv.setText(mRider.getFromRider().getmFirstName());
                    mEmailTv.setText(mRider.getFromRider().getmEmail());
                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                    if (mRider.getFromRider().getThumb_image() != null) {
                        if (!mRider.getFromRider().getThumb_image().equals("")) {
                            Glide.with(this).load(mRider.getFromRider().getThumb_image())
                                    .error(R.drawable.user_icon)
                                    .transition(new DrawableTransitionOptions()
                                            .crossFade())
                                    .dontAnimate()
                                    .into(mProfileIv);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mRider.getRequest_status().equals("3")) {
                mStartRideLi.setVisibility(View.VISIBLE);
                mFinishRideBtn.setVisibility(View.VISIBLE);
                ivStartChat.setVisibility(View.GONE);
            } else {
                if (mFinishRideBtn.getVisibility() != View.VISIBLE) {
                    mStartRideLi.setVisibility(View.GONE);
                    ivStartChat.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Call Google API for getting Rider's time and Distance
    private void getRiderInfo(LatLng sourceLatLang, LatLng destinationLatLang) {
        if (sourceLatLang != null && destinationLatLang != null) {
            mProgressDialog.show();
            MapDirectionAPI.getDirection(sourceLatLang, destinationLatLang, context).enqueue(updateRouteCallback4);

        }
    }

    private void clearRideData() {
        if (mApp.mWebSocketSendRequest != null) {
            mApp.mWebSocketSendRequest.close();
            mApp.mWebSocketSendRequest = null;
        }
        if (RideShareApp.getmUserType().equals("2")) {
            commonMethods.deleteRideTable("UserRides");
            commonMethods.deleteRidersTable("Riders");
            commonMethods.deletePolyTable("Ploy");
        }
        PrefUtils.putString("finalDestination", "");
    }

    private void removeDriver(String userid) {
        Log.w("Calling", "Calling now...");
        ApiServiceModule.createService(RestApiInterface.class, this).removeDriverFromList(userid).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject resp = null;
                try {
                    if (response.body() != null) {
                        resp = new JSONObject(response.body().toString());
                        if (resp.optString("status").equals("success")) {
                            Log.w("Success", "Completed API is Called");
                            sendSocketCancelAcceptedRide(RideShareApp.getmUserType());
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

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180)
                    lngDelta -= Math.signum(lngDelta) * 360;

                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    private BroadcastReceiver mContentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {

            int level = intent.getIntExtra("level", 0);
            if (level <= 15) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mRider.getRequest_status().equals("3")) {
                                acceptOrRejectRequest(mRider.getRide_id(), "2", context);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           /* MessageUtils.showSuccessMessage(context, "Battery Level" + level);
            Log.d("Battery Level", "Level" + level);*/

        }
    };

    @Override
    protected void onStop() {
        if (mContentReceiver != null && batteryInfoReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mContentReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(batteryInfoReceiver);
            mContentReceiver = null;
            batteryInfoReceiver = null;
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        }
        super.onStop();
    }
}

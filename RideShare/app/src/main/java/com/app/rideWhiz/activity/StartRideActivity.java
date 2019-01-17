package com.app.rideWhiz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.AcceptRequest;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.api.response.CancelRequest;
import com.app.rideWhiz.api.response.StartRideResponse;
import com.app.rideWhiz.chat.CommonMethods;
import com.app.rideWhiz.chat.LocalBinder;
import com.app.rideWhiz.chat.MyService;
import com.app.rideWhiz.chat.MyXMPP;
import com.app.rideWhiz.model.Directions;
import com.app.rideWhiz.model.InProgressRide;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.model.Route;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.MapDirectionAPI;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String RECEIVE_JSON = "com.thetwon.whereareyou.RECEIVE_JSON";
    private final Handler mUpdaterHandler = new Handler();

    Activity activity;
    Context context;
    LatLng currentlthg;
    AcceptRider mRider;
    LatLng pickuplocation;
    LatLng droppfflocation;
    //BroadcastReceiver receiver;
    Double Latitude, Longitude;
    //String Provider;
    User mUserbean;
    RideShareApp mApp;
    LatLng DriverLocation;
    LatLng CustomerLocaton;
    Marker DriverMarker;

    Location mDriverLocation;
    Location mPreDriverLocation;
    WebSocketClient mWebSocketClient;
    CustomProgressDialog mProgressDialog;
    float zoomLevel = 16f;
    int updateinterval = 5000;
    CameraPosition cameraPosition;
    RideShareApp application;
    List<Route> routes;
    private GoogleMap mGoogleMap;
    private Marker PickupMarker;
    private Marker DropoffMarker;
    private Marker curLocMarker;
    private Polyline directionLine;
    private TextView mNameTv;
    private TextView mEmailTv;
    private CircularImageView mProfileIv;
    private LinearLayout mStartRideLi;
    private Button mStartRideBtn;
    private Button mFinishRideBtn;
    public static String RideStatus = "";
    private String TabelName;
    private TextView item_txt_counts;
    private RelativeLayout layout_unreadmsgs;
    MaterialDialog mMaterialDialog;

    boolean isDestroy = false;
    boolean isRejectRide = false;

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (mDriverLocation.distanceTo(mPreDriverLocation) >= 0.5f) {
                    mPreDriverLocation = mDriverLocation;
                    animateMarkerNew(DriverMarker, new LatLng(Latitude, Longitude));

                    JSONObject jMessage = new JSONObject();
                    jMessage.put("chat_message", "" + Latitude + "`" + Longitude);
                    jMessage.put("chat_user", "RideShare");
                    jMessage.put("sender_user", mRider.getRide_id());
                    jMessage.put("message_type", "chat-box-html");
                    jMessage.put("message_new", " ");
                    mWebSocketClient.send(jMessage.toString());
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("DESTROY", "1");
            try {
                String staus = intent.getStringExtra("int_data");
                if (staus.equals("1")) {
                    mStartRideLi.setVisibility(View.VISIBLE);
                    mFinishRideBtn.setVisibility(View.VISIBLE);
                    mStartRideBtn.setVisibility(View.GONE);
                    MessageUtils.showSuccessMessage(StartRideActivity.this, "Ride Started");
                    mRider.setRequest_status("3");
                } else if (staus.equals("2")) {
                    try {
                        if (mUserbean.getmRideType().equals("1")) {
                            isRejectRide = true;
                            MessageUtils.showSuccessMessage(StartRideActivity.this, "Ride Finished");
                            Intent rateride = new Intent(StartRideActivity.this, RideRateActivity.class);
                            rateride.putExtra("riderate", mRider.getRide_id());
                            rateride.putExtra("driverid", mRider.getFromRider().getnUserId());
                            startActivity(rateride);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    JSONObject jobj = new JSONObject(staus);
                    if (jobj.getString("request_status").equals("2")) {
                        isRejectRide = true;
                        MessageUtils.showFailureMessage(StartRideActivity.this, "Request Rejected");
                        finish();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver mUpdateMessageReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("DESTROY", "2");
            getMessages();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_ride_layout);

        activity = this;
        context = this;
        application = (RideShareApp) getApplicationContext();

        PrefUtils.initPreference(this);
        mUserbean = PrefUtils.getUserInfo();
        mProgressDialog = new CustomProgressDialog(this);

        mApp = (RideShareApp) getApplicationContext();

        mNameTv = (TextView) findViewById(R.id.name_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mProfileIv = (CircularImageView) findViewById(R.id.user_profile);
        mStartRideLi = (LinearLayout) findViewById(R.id.li1);
        item_txt_counts = findViewById(R.id.item_txt_counts);
        layout_unreadmsgs = findViewById(R.id.layout_unreadmsgs);

        mStartRideBtn = (Button) findViewById(R.id.start_ride_btn);
        mFinishRideBtn = (Button) findViewById(R.id.finish_ride_btn);

        mStartRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RideStatus = "inProgress";
                startRide(mRider.getRide_id(), "1", "3", mUserbean.getmUserId());
                mRider.setRequest_status("3");
            }
        });

        mFinishRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RideStatus = "finished";
                endRide(mRider.getRide_id(), "1", "4", mUserbean.getmUserId());
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        try {
            mRider = (AcceptRider) getIntent().getExtras().getSerializable("rideobj");
            pickuplocation = new LatLng(Double.parseDouble(mRider.getStart_lati()), Double.parseDouble(mRider.getStart_long()));
            droppfflocation = new LatLng(Double.parseDouble(mRider.getEnd_lati()), Double.parseDouble(mRider.getEnd_long()));
            requestRoute(pickuplocation, droppfflocation);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        if (getIntent().hasExtra("Is_driver")) {
            if (getIntent().getStringExtra("Is_driver").equals("1")) {
                mFinishRideBtn.setVisibility(View.VISIBLE);
                mStartRideBtn.setVisibility(View.GONE);
            } else {
                mStartRideBtn.setVisibility(View.VISIBLE);
                mFinishRideBtn.setVisibility(View.GONE);
            }
        }

        if (mApp.getmUserType().equals("2")) {
            //startService(new Intent(getBaseContext(), LocationService.class));
        }
        try {
            if (mApp.getmUserType().equals("2")) {
                if (mUserbean.getmUserId().equals(mRider.getFromRider().getnUserId())) {
                    mNameTv.setText(mRider.getToRider().getmFirstName());
                    mEmailTv.setText(mRider.getToRider().getmEmail());

                    try {
                        if (mRider.getToRider().getThumb_image() != null) {
                            if (!mRider.getToRider().getThumb_image().equals("")) {
                                Glide.with(this).load(mRider.getToRider().getThumb_image())
                                        .error(R.drawable.user_icon)
                                        .crossFade()
                                        .into(mProfileIv);
                            }
                        }
                    }catch (Exception e){}

                    DriverLocation = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                } else if (mUserbean.getmUserId().equals(mRider.getToRider().getnUserId())) {
                    mNameTv.setText(mRider.getFromRider().getmFirstName());
                    mEmailTv.setText(mRider.getFromRider().getmEmail());

                    try {
                        if (mRider.getFromRider().getThumb_image() != null) {
                            if (!mRider.getFromRider().getThumb_image().equals("")) {
                                //Picasso.with(StartRideActivity.this).load(mRider.getFromRider().getThumb_image()).into(mProfileIv);
                                Glide.with(this).load(mRider.getFromRider().getThumb_image())
                                        .error(R.drawable.user_icon)
                                        .crossFade()
                                        .into(mProfileIv);
                            }
                        }
                    }catch (Exception e){}

                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                }
                mStartRideLi.setVisibility(View.VISIBLE);
            } else {
                if (mUserbean.getmUserId().equals(mRider.getFromRider().getnUserId())) {
                    mNameTv.setText(mRider.getToRider().getmFirstName());
                    mEmailTv.setText(mRider.getToRider().getmEmail());

                    try {
                        if (mRider.getToRider().getThumb_image() != null) {
                            if (!mRider.getToRider().getThumb_image().equals("")) {
                                //Picasso.with(StartRideActivity.this).load(mRider.getToRider().getThumb_image()).into(mProfileIv);
                                Glide.with(this).load(mRider.getToRider().getThumb_image())
                                        .error(R.drawable.user_icon)
                                        .crossFade()
                                        .into(mProfileIv);
                            }
                        }
                    }catch (Exception e){}

                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                } else if (mUserbean.getmUserId().equals(mRider.getToRider().getnUserId())) {
                    mNameTv.setText(mRider.getFromRider().getmFirstName());
                    mEmailTv.setText(mRider.getFromRider().getmEmail());

                    try {
                        if (mRider.getFromRider().getThumb_image() != null) {
                            if (!mRider.getFromRider().getThumb_image().equals("")) {
                                //Picasso.with(StartRideActivity.this).load(mRider.getFromRider().getThumb_image()).into(mProfileIv);
                                Glide.with(this).load(mRider.getFromRider().getThumb_image())
                                        .error(R.drawable.user_icon)
                                        .crossFade()
                                        .into(mProfileIv);
                            }
                        }
                    } catch (Exception e){}
                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                }
                //if (mFinishRideBtn.getVisibility() != View.VISIBLE) {
                //    mStartRideLi.setVisibility(View.GONE);
                //}
                if (mRider.getRequest_status().equals("3")) {

                    mStartRideLi.setVisibility(View.VISIBLE);
                    mFinishRideBtn.setVisibility(View.VISIBLE);
                    mStartRideBtn.setVisibility(View.GONE);
                } else {
                    if (mFinishRideBtn.getVisibility() != View.VISIBLE) {
                        mStartRideLi.setVisibility(View.GONE);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //initSocket();
        connectWebSocket();

        ImageView ivStartChat = (ImageView) findViewById(R.id.ivStartChat);
        ivStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRejectRide = true;
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.intentKey.SelectedChatUser, mRider);
                startActivity(intent);
            }
        });


        init();
    }


    private void requestRoute(LatLng picklng, LatLng droplng) {
        if (picklng != null && droplng != null) {
            if (mApp.getmUserType().equals("2")) {
                final Location driverlatlon = new Location(LocationManager.GPS_PROVIDER);
                LatLng driverlatlng = new LatLng(Double.parseDouble(mRider.getStart_lati()), Double.parseDouble(mRider.getStart_long()));
                MapDirectionAPI.getDirection(driverlatlng, droplng, context).enqueue(updateRouteCallback);
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

    @Override
    protected void onResume() {
        super.onResume();
        isDestroy = false;

        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationReceiver, new IntentFilter("update-location"));

        LocalBroadcastManager.getInstance(StartRideActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("request_status"));

        LocalBroadcastManager.getInstance(StartRideActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("start_ride"));

        LocalBroadcastManager.getInstance(StartRideActivity.this).registerReceiver(mUpdateMessageReciver, new IntentFilter("update_message"));
        getMessages();
    }

    @Override
    public void onBackPressed() {

        Log.w("DESTROY", "3");
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
                            final InProgressRide inProgressRide = new InProgressRide();
                            inProgressRide.setmRideId(mRider.getRide_id());
                            inProgressRide.setmRideType(mRider.getU_ride_type());

                            inProgressRide.setmStartingAddress(mRider.getStarting_address());
                            inProgressRide.setmEndingAddress(mRider.getEnding_address());
                            inProgressRide.setmStartLat(mRider.getStart_lati());
                            inProgressRide.setmStartLang(mRider.getStart_long());
                            inProgressRide.setmEndLat(mRider.getEnd_lati());
                            inProgressRide.setmEndLang(mRider.getEnd_long());
                            inProgressRide.setmRequestStatus(mRider.getRequest_status());
                            inProgressRide.setmFromRider(mRider.getFromRider());
                            inProgressRide.setmToRider(mRider.getToRider());

                            mUpdaterHandler.removeCallbacks(runnable);
                            mUpdaterHandler.removeCallbacksAndMessages(null);
                            /*if (mWebSocketClient != null) {
                                mWebSocketClient.close();
                                mWebSocketClient = null;
                            }
*/
                            if (mUserbean.getmRideType().equals("2")) {
                                endRide(inProgressRide.getmRideId(), "1", "4", mUserbean.getmUserId());
                            } else {
                                endRide(inProgressRide.getmRideId(), "0", "4", mUserbean.getmUserId());
                            }

                            mMaterialDialog.dismiss();

                            MyXMPP.disconnect();

                        }
                    });

            mMaterialDialog.show();
        } else {

            //if (mApp.getmUserType().equals("1") || mApp.getmUserType().equals("2")) {

                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(StartRideActivity.this);
                    builder.setTitle("Cancel Ride")
                            .setMessage("Are you sure you want to cancel ride?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {

                                        //cancelRequst();

                                        acceptOrRejectRequest(mRider.getRide_id(), "2", context);

                                        Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                                        i.putExtra("inprogress", "free");
                                        startActivity(i);
                                        finish();
                                        //activity.finish();
                                        MyXMPP.disconnect();


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


            //}



        }
    }

    @Override
    public void onPause() {
         LocalBroadcastManager.getInstance(StartRideActivity.this).unregisterReceiver(mMessageReceiver);
         LocalBroadcastManager.getInstance(StartRideActivity.this).unregisterReceiver(mUpdateMessageReciver);
        // LocalBroadcastManager.getInstance(StartRideActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("request_status"));

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

        Log.w("DESTROY", mApp.getmUserType());

        /*if (mApp.getmUserType().equals("1") || mApp.getmUserType().equals("2")) {
            if(!isRejectRide) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(StartRideActivity.this);
                builder.setTitle("Reject Ride")
                        .setMessage("Are you sure you want to reject ride?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    acceptOrRejectRequest(mRider.getRide_id(), "0", context);
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
        }*/



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //handlerUpdateLocation.removeCallbacks(mRunnableUpdateLocation);
        //handlerUpdateLocation.postDelayed(mRunnableUpdateLocation, 1000);

        setMarker();

        if (PickupMarker == null) {
            PickupMarker = mGoogleMap.addMarker(new MarkerOptions().title("Pick Up Location").snippet(mRider.getStarting_address()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_start))
                    .position(pickuplocation));
        }

        if (DropoffMarker == null) {
            DropoffMarker = mGoogleMap.addMarker(new MarkerOptions().title("Drop off Location").snippet(mRider.getEnding_address()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_start))
                    .position(droppfflocation));
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoomLevel = mGoogleMap.getCameraPosition().zoom;
            }
        });

        if (mApp.getmUserType().equals("2")) {
            mUpdaterHandler.post(runnable);
        }
    }

    boolean isSetMarker;
    public Location previousBestLocation = null;

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.w("DESTROY", "7");
            try {

                //if (AppUtils.isBetterLocation(RideShareApp.mLocation, previousBestLocation)) {
                previousBestLocation = RideShareApp.mLocation;

                Latitude = RideShareApp.mLocation.getLatitude();
                Longitude = RideShareApp.mLocation.getLongitude();

                mDriverLocation = new Location("");
                mDriverLocation.setLatitude(Latitude);
                mDriverLocation.setLongitude(Latitude);
                DriverLocation = new LatLng(Latitude, Longitude);
                    /*if(mRider.getRequest_status().equals("3")){
                        DriverLocation = new LatLng(Latitude, Longitude);

                    }*/


                //}

                setMarker();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void setMarker() {
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

            if (DriverMarker == null) {
                DriverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                        .position(DriverLocation));
            } else {
                /*if(mApp.getmUserType().equals("2")) {
                    if (mRider.getRequest_status().equals("3")) {
                        if (mDriverLocation.distanceTo(mPreDriverLocation) >= 0.5f) {
                            mPreDriverLocation = mDriverLocation;
                            mPreDriverLocation = mDriverLocation;
                            animateMarkerNew(DriverMarker, DriverLocation);
                        }
                    }
                }else {
                    if (mRider.getRequest_status().equals("3")) {
                        if (mDriverLocation.distanceTo(mPreDriverLocation) >= 0.5f) {
                            mPreDriverLocation = mDriverLocation;
                            mPreDriverLocation = mDriverLocation;
                            animateMarkerNew(DriverMarker, DriverLocation);
                        }
                    }
                }*/

            }

            if (curLocMarker == null) {
                if (!isSetMarker) {
                    isSetMarker = true;
                    curLocMarker = setcutommarker(CustomerLocaton, mRider);
                }
            } else {
                curLocMarker.setPosition(CustomerLocaton);
            }
        } catch (Exception e) {
            //mPreDriverLocation = mDriverLocation;
        }
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(ApiServiceModule.WEBSOCKET_ENDPOINT);
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

                                        String updatedlocation[] = jobj.getString("chat_message").split("`");
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
                if (!isDestroy)
                    mWebSocketClient.reconnect();


            }

            @Override
            public void onError(final Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                if (!isDestroy)
                    mWebSocketClient.reconnect();
            }
        };
        mWebSocketClient.connect();
    }

    private void startRide(String mId, String check_driver, final String mType, String userid) {
        mProgressDialog.show();
        //destinationLatLang = new LatLng(Double.parseDouble(location.getmLatitude()), Double.parseDouble(location.getmLongitude()));
       /* ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver,
                mType, userid,
                "" + RideShareApp.mLocation.getLatitude(), "" + RideShareApp.mLocation.getLongitude()).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mType.equals("3")) {
                        mFinishRideBtn.setVisibility(View.VISIBLE);
                        mStartRideBtn.setVisibility(View.GONE);
                    } else if (mType.equals("4")) {

                        mUpdaterHandler.removeCallbacks(runnable);
                        mUpdaterHandler.removeCallbacksAndMessages(null);
                        Intent i = new Intent(StartRideActivity.this, HomeNewActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        activity.finish();
                        mProgressDialog.cancel();
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<StartRideResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });*/
        ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver,
                mType, userid,
                "" + mRider.getEnd_lati(), "" + mRider.getEnd_long()).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mType.equals("3")) {

                        mFinishRideBtn.setVisibility(View.VISIBLE);
                        mStartRideBtn.setVisibility(View.GONE);
                    } else if (mType.equals("4")) {
                        mUpdaterHandler.removeCallbacks(runnable);
                        mUpdaterHandler.removeCallbacksAndMessages(null);
                        Intent i = new Intent(StartRideActivity.this, HomeNewActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        activity.finish();
                        mProgressDialog.cancel();
                    }
                } else {

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

    private void endRide(String mId, String check_driver, final String mType, String userid) {
        mProgressDialog.show();
        //destinationLatLang = new LatLng(Double.parseDouble(location.getmLatitude()), Double.parseDouble(location.getmLongitude()));
        ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver,
                mType, userid,
                "" + RideShareApp.mLocation.getLatitude(), "" + RideShareApp.mLocation.getLongitude()).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mType.equals("3")) {
                        mFinishRideBtn.setVisibility(View.VISIBLE);
                        mStartRideBtn.setVisibility(View.GONE);
                    } else if (mType.equals("4")) {

                        mUpdaterHandler.removeCallbacks(runnable);
                        mUpdaterHandler.removeCallbacksAndMessages(null);
                        if (mUserbean.getmRideType().equals("1")) {

                            MessageUtils.showSuccessMessage(StartRideActivity.this, "Ride Finished");
                            Intent rateride = new Intent(StartRideActivity.this, RideRateActivity.class);
                            rateride.putExtra("riderate", mRider.getRide_id());
                            rateride.putExtra("driverid", mRider.getFromRider().getnUserId());
                            startActivity(rateride);
                        } else {
                            Intent i = new Intent(StartRideActivity.this, HomeNewActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            activity.finish();
                            mProgressDialog.cancel();
                        }


                    }
                } else {

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
        /*ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver,
                mType, userid,
                "" + mRider.getEnd_lati(), "" + mRider.getEnd_long()).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mType.equals("3")) {

                        mFinishRideBtn.setVisibility(View.VISIBLE);
                        mStartRideBtn.setVisibility(View.GONE);
                    } else if (mType.equals("4")) {
                        Intent intent = new Intent(StartRideActivity.this, LocationService.class);
                        stopService(intent);
                        mUpdaterHandler.removeCallbacks(runnable);
                        mUpdaterHandler.removeCallbacksAndMessages(null);
                        finish();
                        // rate & rewie
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<StartRideResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });*/
    }

    private void getMessages() {
        CommonMethods commonMethods = new CommonMethods(getApplicationContext());
        Rider toRider;

        if (PrefUtils.getUserInfo().getmUserId().equals(mRider.getFromRider().getnUserId()))
            toRider = mRider.getToRider();
        else
            toRider = mRider.getFromRider();

        TabelName = Constants.intentKey.jabberPrefix + toRider.getnUserId();

        if (commonMethods.isTableExists(TabelName.toLowerCase())) {
            String unreadCount = getUnreadMessages(TabelName.toLowerCase());
            if (!unreadCount.equals("") && !unreadCount.equals("0")) {
                layout_unreadmsgs.setVisibility(View.VISIBLE);
                item_txt_counts.setText(unreadCount);
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

    /********************** XMPP **********************/
    private MyService mService;
    private MyXMPP xmpp;
    private boolean mBounded;

    private void init() {
        doBindService();
        xmpp = new MyXMPP();
    }

    void doBindService() {
        bindService(new Intent(this, MyService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            mService = ((LocalBinder<MyService>) service).getService();
            mBounded = true;
            Log.w(getString(R.string.app_name), "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.w(getString(R.string.app_name), "onServiceDisconnected");
        }
    };

    public String getUnreadMessages(String tableName) {
        String tblName = "'" + tableName + "'";
        SQLiteDatabase myDb;
        myDb = openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);
        Cursor allRows = myDb.rawQuery("SELECT * FROM " + tblName + " WHERE msgtype = 'false' AND who = 'r'", null);
        System.out.println("COUNT : " + allRows.getCount());
        return String.valueOf(allRows.getCount());
    }

    private Marker setcutommarker(LatLng currentDriverPos, AcceptRider rider) {
        String userImage;
        Marker marker = null;
        try {
            getMarkerBitmapFromView(activity, rider, currentDriverPos);

           /* marker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                    .position(currentDriverPos).anchor(0.5f, 1f)
                    .icon(BitmapDescriptorFactory.fromBitmap(AppUtils.getMarkerBitmapFromView(getActivity(), userImage)))
                    // Specifies the anchor to be at a particular point in the marker image.
                    .rotation(0f)
                    .flat(true));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return marker;
    }

    public void getMarkerBitmapFromView(final Activity activity, final AcceptRider rider, final LatLng currentDriverPos) {

        try {
            final View customMarkerView = activity.getLayoutInflater().inflate(R.layout.item_custom_marker, null);

            CircleImageView markerImageView = customMarkerView.findViewById(R.id.user_dp);
            String userimage = "";

            userimage = rider.getFromRider().getThumb_image();
            if(userimage == null){
                userimage = "";
            }

            if(!userimage.equals("")) {
                Glide.with(activity).load(userimage).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                        try {
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider))
                                    //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                    .position(currentDriverPos).anchor(0.5f, 1f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                                    // Specifies the anchor to be at a particular point in the marker image.
                                    .rotation(0f)
                                    .flat(true));
                        } catch (Exception ew) {

                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                        Bitmap icon = AppUtils.drawableToBitmap(resource);

                        Bitmap bmImg = AppUtils.getMarkerBitmapFromView(activity, icon);

                        Marker m = null;
                        try {
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider))
                                    //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                    .position(currentDriverPos).anchor(0.5f, 1f)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                    // Specifies the anchor to be at a particular point in the marker image.
                                    .rotation(0f)
                                    .flat(true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        return false;
                    }
                }).error(R.drawable.ic_user_pin).placeholder(R.drawable.ic_user_pin).into(markerImageView);
            } else {
                curLocMarker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(rider)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                        .position(currentDriverPos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void cancelRequst(String mRideId) {
        ApiServiceModule.createService(RestApiInterface.class, context).cancelRequest(mRideId).enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {


            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    public void acceptOrRejectRequest(String mRideId, String acceptOrreject,Context context) {
        Log.w("DESTROY", "8");
        ApiServiceModule.createService(RestApiInterface.class,context).declineRequestNotification(mUserbean.getmUserId(), mRideId, acceptOrreject).enqueue(new Callback<AcceptRequest>() {
            @Override
            public void onResponse(Call<AcceptRequest> call, Response<AcceptRequest> response) {
                Log.w("ONBACK-S","N");

            }

            @Override
            public void onFailure(Call<AcceptRequest> call, Throwable t) {
                Log.w("ONBACK-F","N");
            }
        });
    }
}

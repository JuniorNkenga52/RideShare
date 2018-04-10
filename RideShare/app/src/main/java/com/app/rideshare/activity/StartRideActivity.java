package com.app.rideshare.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.AcceptRider;
import com.app.rideshare.api.response.StartRideResponse;
import com.app.rideshare.chat.LocalBinder;
import com.app.rideshare.chat.MyService;
import com.app.rideshare.chat.MyXMPP;
import com.app.rideshare.model.Directions;
import com.app.rideshare.model.Route;
import com.app.rideshare.model.User;
import com.app.rideshare.service.LocationService;
import com.app.rideshare.utils.Constant;
import com.app.rideshare.utils.MapDirectionAPI;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String RECEIVE_JSON = "com.thetwon.whereareyou.RECEIVE_JSON";
    private final Handler mUpdaterHandler = new Handler();

    Activity activity;
    LatLng currentlthg;
    AcceptRider mRider;
    LatLng pickuplocation;
    LatLng droppfflocation;
    //Typeface mRobotoMedium;
    BroadcastReceiver receiver;
    Double Latitude, Longitude;
//    Double PreLatitude = 0.0, PreLongitude = 0.0;
    String Provider;
    User mUserbean;
    RideShareApp mApp;
    LatLng DriverLocation;
    LatLng CustomerLocaton;
    Marker DriverMarker;
    Marker CustomerMarker;
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
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (mDriverLocation.distanceTo(mPreDriverLocation) >= 0.5f) {
                    mPreDriverLocation = mDriverLocation;

                    ToastUtils.showShort(StartRideActivity.this, "Send");

                    animateMarkerNew(DriverMarker, new LatLng(Latitude, Longitude));

                    JSONObject jmessage = new JSONObject();
                    jmessage.put("chat_message", "" + Latitude + "`" + Longitude);
                    jmessage.put("chat_user", "RideShare");
                    jmessage.put("sender_user", mRider.getRide_id());
                    jmessage.put("message_type", "chat-box-html");
                    jmessage.put("message_new", "");
                    mWebSocketClient.send(jmessage.toString());
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
                updateLineDestination(json);

            }
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String staus = intent.getStringExtra("int_data");
            if (staus.equals("1")) {
                ToastUtils.showShort(StartRideActivity.this, "Your Ride Started.");
            } else if (staus.equals("2")) {
                ToastUtils.showShort(StartRideActivity.this, "Your Ride Finish.");
                Intent rateride = new Intent(StartRideActivity.this, RideRateActivity.class);
                rateride.putExtra("riderate", mRider.getRide_id());
                rateride.putExtra("driverid", mRider.getFromRider().getnUserId());
                startActivity(rateride);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_ride_layout);

        activity=this;
        application = (RideShareApp) getApplicationContext();

        PrefUtils.initPreference(this);
        mUserbean = PrefUtils.getUserInfo();
        mProgressDialog = new CustomProgressDialog(this);

        mApp = (RideShareApp) getApplicationContext();

        mNameTv = (TextView) findViewById(R.id.name_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mProfileIv = (CircularImageView) findViewById(R.id.user_profile);
        mStartRideLi = (LinearLayout) findViewById(R.id.li1);
        mStartRideLi.setVisibility(View.GONE);
        //mRobotoMedium = TypefaceUtils.getTypefaceRobotoMediam(this);
        /*mNameTv.setTypeface(mRobotoMedium);
        mEmailTv.setTypeface(mRobotoMedium);*/

        mStartRideBtn = (Button) findViewById(R.id.start_ride_btn);
        mFinishRideBtn = (Button) findViewById(R.id.finish_ride_btn);

        mFinishRideBtn.setVisibility(View.GONE);

        mStartRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRide(mRider.getRide_id(), "3", mUserbean.getmUserId());
            }
        });

        mFinishRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRide(mRider.getRide_id(), "4", mUserbean.getmUserId());
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

        if (mRider.getRequest_status().equals("3")) {
            mFinishRideBtn.setVisibility(View.VISIBLE);
            mStartRideBtn.setVisibility(View.GONE);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(RECEIVE_JSON)) {
                    Provider = intent.getStringExtra("Provider");
                    Latitude = (Double) intent.getExtras().get("Latitude");
                    Longitude = (Double) intent.getExtras().get("Longitude");
                    Log.d("Provider", "" + Provider);
                    Log.d("location", "" + Latitude + "," + Longitude);

                    DriverLocation = new LatLng(Latitude, Longitude);
                    mDriverLocation = new Location("");
                    mDriverLocation.setLatitude(Latitude);
                    mDriverLocation.setLongitude(Latitude);
                }
            }
        };
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(receiver, intentFilter);

        if (mApp.getmUserType().equals("2")) {
            Intent intent = new Intent(StartRideActivity.this, LocationService.class);
            startService(intent);
        }
        try {
            if (mApp.getmUserType().equals("2")) {
                if (mUserbean.getmUserId().equals(mRider.getFromRider().getnUserId())) {
                    mNameTv.setText(mRider.getToRider().getmFirstName());
                    mEmailTv.setText(mRider.getToRider().getmEmail());

                    if (!mRider.getToRider().getmProfileImage().equals("")) {
                        Picasso.with(StartRideActivity.this).load(mRider.getToRider().getmProfileImage()).into(mProfileIv);
                    }

                    DriverLocation = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                } else if (mUserbean.getmUserId().equals(mRider.getToRider().getnUserId())) {
                    mNameTv.setText(mRider.getFromRider().getmFirstName());
                    mEmailTv.setText(mRider.getFromRider().getmEmail());
                    if (!mRider.getFromRider().getmProfileImage().equals("")) {
                        Picasso.with(StartRideActivity.this).load(mRider.getFromRider().getmProfileImage()).into(mProfileIv);
                    }

                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                }
                mStartRideLi.setVisibility(View.VISIBLE);
            } else {
                if (mUserbean.getmUserId().equals(mRider.getFromRider().getnUserId())) {
                    mNameTv.setText(mRider.getToRider().getmFirstName());
                    mEmailTv.setText(mRider.getToRider().getmEmail());


                    if (!mRider.getToRider().getmProfileImage().equals("")) {
                        Picasso.with(StartRideActivity.this).load(mRider.getToRider().getmProfileImage()).into(mProfileIv);
                    }

                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                } else if (mUserbean.getmUserId().equals(mRider.getToRider().getnUserId())) {
                    mNameTv.setText(mRider.getFromRider().getmFirstName());
                    mEmailTv.setText(mRider.getFromRider().getmEmail());

                    if (!mRider.getFromRider().getmProfileImage().equals("")) {
                        Picasso.with(StartRideActivity.this).load(mRider.getFromRider().getmProfileImage()).into(mProfileIv);
                    }
                    DriverLocation = new LatLng(Double.parseDouble(mRider.getToRider().getmLatitude()), Double.parseDouble(mRider.getToRider().getmLongitude()));
                    CustomerLocaton = new LatLng(Double.parseDouble(mRider.getFromRider().getmLatitude()), Double.parseDouble(mRider.getFromRider().getmLongitude()));
                }
                mStartRideLi.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectWebSocket();

        ImageView ivStartChat = (ImageView) findViewById(R.id.ivStartChat);
        ivStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constant.intentKey.SelectedChatUser, mRider);
                startActivity(intent);
            }
        });

        init();
    }

    private void requestRoute(LatLng picklng, LatLng droplng) {
        if (picklng != null && droplng != null) {
            MapDirectionAPI.getDirection(picklng, droplng).enqueue(updateRouteCallback);
        }
    }

    private void updateLineDestination(final String json) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Directions directions = new Directions(StartRideActivity.this);
                    routes = directions.parse(json);
                    if (directionLine != null) directionLine.remove();
                    if (routes.size() > 0) {
                        directionLine = mGoogleMap.addPolyline((new PolylineOptions())
                                .addAll(routes.get(0).getOverviewPolyLine())
                                .color(ContextCompat.getColor(StartRideActivity.this, R.color.blacltext))
                                .width(10));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(StartRideActivity.this, LocationService.class);
        stopService(intent);
        mUpdaterHandler.removeCallbacks(runnable);
        mUpdaterHandler.removeCallbacksAndMessages(null);
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
        }

        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        SmartLocation.with(this).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        try {
                            currentlthg = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("Bearing", "" + location.getBearing());
                            if (zoomLevel <= 2.0f) {
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
                                DriverMarker.setPosition(DriverLocation);
                            }

                            if (CustomerMarker == null) {
                                CustomerMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                                        .position(CustomerLocaton));
                                //curLocMarker=setcutommarker(CustomerLocaton,mRider);
                                //CustomerMarker = setcutommarker();
                            } else {
                                CustomerMarker.setPosition(CustomerLocaton);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

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
                    if (mApp.getmUserType().equals("1")) {

                        if (!jobj.getString("message_type").equals("chat-connection-ack")) {
                            if (!jobj.getString("chat_message").equals("null") && jobj.getString("sender_user").equals(mRider.getRide_id())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            ToastUtils.showShort(StartRideActivity.this, "received");

                                            String updatedlocation[] = jobj.getString("chat_message").split("`");
                                            double mlet = Double.parseDouble(updatedlocation[0]);
                                            double mlong = Double.parseDouble(updatedlocation[1]);

                                            mDriverLocation = new Location("");
                                            mDriverLocation.setLatitude(mlet);
                                            mDriverLocation.setLongitude(mlong);

                                            animateMarkerNew(DriverMarker, new LatLng(mlet, mlong));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        }
                    }

                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);


            }

            @Override
            public void onError(final Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();


    }

    private void startRide(String mId, final String mType, String userid) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).mStartRide(mId, mType, userid).enqueue(new Callback<StartRideResponse>() {
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(StartRideActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("start_ride"));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(StartRideActivity.this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
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

    private void init(){
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
}

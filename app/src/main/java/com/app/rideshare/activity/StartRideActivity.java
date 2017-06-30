package com.app.rideshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.AcceptRider;
import com.app.rideshare.api.response.StartRideResponse;
import com.app.rideshare.model.Directions;
import com.app.rideshare.model.Rider;
import com.app.rideshare.model.Route;
import com.app.rideshare.model.User;
import com.app.rideshare.service.LocationService;
import com.app.rideshare.utils.MapDirectionAPI;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
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
import org.json.JSONArray;
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
    private GoogleMap mGoogleMap;
    private Marker PickupMarker;
    private Marker DropoffMarker;
    LatLng currentlthg;
    private Marker curLocMarker;
    AcceptRider mRider;
    LatLng pickuplocation;
    LatLng droppfflocation;

    private Polyline directionLine;

    private TextView mNameTv;
    private TextView mEmailTv;
    private CircularImageView mProfileIv;
    private LinearLayout mStartRideLi;
    private Button mStartRideBtn;
    private Button mFinishRideBtn;


    Typeface mRobotoMedium;
    BroadcastReceiver receiver;
    public static final String RECEIVE_JSON = "com.thetwon.whereareyou.RECEIVE_JSON";
    Double Latitude, Longitude;
    String Provider;
    User mUserbean;
    RideShareApp mApp;


    LatLng DriverLocation;
    LatLng CustomerLocaton;
    Marker DriverMarker;
    Marker CustomerMarker;

    Location mDriverLocation;
    Location mDriverLastLocation;


    WebSocketClient mWebSocketClient;
    CustomProgressDialog mProgressDialog;

    int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_ride_layout);

        PrefUtils.initPreference(this);
        mUserbean = PrefUtils.getUserInfo();
        mProgressDialog = new CustomProgressDialog(this);

        mApp = (RideShareApp) getApplicationContext();

        mNameTv = (TextView) findViewById(R.id.name_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mProfileIv = (CircularImageView) findViewById(R.id.user_profile);
        mStartRideLi = (LinearLayout) findViewById(R.id.li1);
        mStartRideLi.setVisibility(View.GONE);
        mRobotoMedium = TypefaceUtils.getTypefaceRobotoMediam(this);
        mNameTv.setTypeface(mRobotoMedium);
        mEmailTv.setTypeface(mRobotoMedium);

        mStartRideBtn = (Button) findViewById(R.id.start_ride_btn);
        mFinishRideBtn = (Button) findViewById(R.id.finish_ride_btn);

        mFinishRideBtn.setVisibility(View.GONE);
        mStartRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRide(mRider.getRide_id(),"1",mUserbean.getmUserId());
            }
        });

        mFinishRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRide(mRider.getRide_id(),"2",mUserbean.getmUserId());
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

                    if (mDriverLastLocation == null)
                        mDriverLastLocation = mDriverLocation;

                    float angle = mDriverLastLocation.bearingTo(mDriverLocation);
                    Log.d("bearing", "" + angle);

                    mDriverLastLocation = mDriverLocation;


                    if (DriverMarker == null) {
                        DriverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                                .position(DriverLocation));//.flat(true)
                    } else {
                        DriverMarker.setPosition(DriverLocation);
                        DriverMarker.setRotation(angle);

                        // DriverMarker.setAnchor(0.5f, 0.5f);
                        //DriverMarker.setFlat(true);
                    }

                   /* try {
                        JSONObject jmessage = new JSONObject();
                        jmessage.put("chat_message", "" + Latitude + "`" + Longitude);
                        jmessage.put("chat_user", "RideShare");
                        jmessage.put("sender_user", mRider.getRide_id());
                        jmessage.put("message_type", "chat-box-html");
                        jmessage.put("message_new", "");
                        mWebSocketClient.send(jmessage.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }
        };
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(receiver, intentFilter);

        if (mApp.getmUserType().equals("2")) {
           /* Intent intent = new Intent(StartRideActivity.this, LocationService.class);
            startService(intent);*/
        }

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
        }
        //connectWebSocket();
    }

    private void requestRoute(LatLng picklng, LatLng droplng) {
        if (picklng != null && droplng != null) {
            MapDirectionAPI.getDirection(picklng, droplng).enqueue(updateRouteCallback);
        }
    }


    public void drawRoute() {
        for (int i = 0; i < routes.get(0).getOverviewPolyLine().size(); i++) {

           try {
               Thread.sleep(1000);
           }catch (Exception e){
                e.printStackTrace();
           }

            Latitude = routes.get(0).getOverviewPolyLine().get(i).latitude;
            Longitude = routes.get(0).getOverviewPolyLine().get(i).longitude;

            DriverLocation = new LatLng(Latitude, Longitude);
            mDriverLocation = new Location("");
            mDriverLocation.setLatitude(Latitude);
            mDriverLocation.setLongitude(Latitude);

            if (mDriverLastLocation == null)
                mDriverLastLocation = mDriverLocation;

            float angle = mDriverLastLocation.bearingTo(mDriverLocation);
            Log.d("bearing", "" + angle);

            mDriverLastLocation = mDriverLocation;

            if (DriverMarker == null) {
                DriverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                        .position(DriverLocation));//.flat(true)
            } else {
                DriverMarker.setPosition(DriverLocation);
                DriverMarker.setRotation(getBearing(mDriverLastLocation,mDriverLocation));
                // DriverMarker.setAnchor(0.5f, 0.5f);
                //DriverMarker.setFlat(true);
            }

        }
    }


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

    List<Route> routes;

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        SmartLocation.with(this).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        currentlthg = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("Bearing", "" + location.getBearing());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlthg).zoom(16).build();
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
                        } else {
                            CustomerMarker.setPosition(CustomerLocaton);
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
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://192.168.0.30:8090/websocketnew/php-socket.php");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
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

                                            String updatedlocation[] = jobj.getString("chat_message").split("`");
                                            double mlet = Double.parseDouble(updatedlocation[0]);
                                            double mlong = Double.parseDouble(updatedlocation[1]);

                                            mDriverLocation = new Location("");
                                            mDriverLocation.setLatitude(mlet);
                                            mDriverLocation.setLongitude(mlong);

                                            if (mDriverLastLocation == null)
                                                mDriverLastLocation = mDriverLocation;

                                            float angle = mDriverLastLocation.bearingTo(mDriverLocation);
                                            Log.d("bearing", "" + angle);

                                            mDriverLastLocation = mDriverLocation;


                                            if (DriverMarker == null) {
                                                DriverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                                                        .position(DriverLocation));//.flat(true)
                                            } else {
                                                DriverMarker.setPosition(new LatLng(mlet, mlong));
                                                DriverMarker.setRotation(getBearing(mDriverLastLocation,mDriverLocation));

                                                //DriverMarker.setAnchor(0.5f, 0.5f);
                                                //DriverMarker.setFlat(true);
                                            }

                                        } catch (Exception e) {

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
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
    private float getBearing(Location begin, Location end) {

        double lat = Math.abs(begin.getLatitude() - end.getLatitude());
        double lng = Math.abs(begin.getLongitude() - end.getLongitude());

        if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
    private void startRide(String mId,final String mType,String userid) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).mStartRide(mId, mType,userid).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null)
                {

                    if(mType.equals("1"))
                    {
                        mFinishRideBtn.setVisibility(View.VISIBLE);
                        mStartRideBtn.setVisibility(View.GONE);
                    }else{
                        finish();
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


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String staus= intent.getStringExtra("int_data");

            if(staus.equals("1"))
            {
                ToastUtils.showShort(StartRideActivity.this,"Your Ride Started.");
            }else if(staus.equals("2"))
            {
                ToastUtils.showShort(StartRideActivity.this,"Your Ride Finish.");
                finish();
            }

        }
    };

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
}

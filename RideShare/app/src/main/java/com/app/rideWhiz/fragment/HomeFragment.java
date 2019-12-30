package com.app.rideWhiz.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.NotificationViewActivity;
import com.app.rideWhiz.activity.PlaceSerachActivity;
import com.app.rideWhiz.activity.RideShareApp;
import com.app.rideWhiz.activity.RideTypeActivity;
import com.app.rideWhiz.activity.WaitingActivity;
import com.app.rideWhiz.adapter.SelectRadiusAdapter;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.request.ContactRequest;
import com.app.rideWhiz.api.response.ContactResponse;
import com.app.rideWhiz.api.response.RideSelect;
import com.app.rideWhiz.api.response.SendResponse;
import com.app.rideWhiz.api.response.UpdateDestinationAddress;
import com.app.rideWhiz.chat.MyService;
import com.app.rideWhiz.listner.OnBackPressedListener;
import com.app.rideWhiz.listner.SocketConnection;
import com.app.rideWhiz.model.ContactBean;
import com.app.rideWhiz.model.Directions;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.model.Route;
import com.app.rideWhiz.model.SearchPlace;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MapDirectionAPI;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.androidannotations.annotations.App;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rideWhiz.chat.MyService.mUpdaterHandler;
import static com.app.rideWhiz.chat.MyService.runnable;
import static com.app.rideWhiz.utils.Constants.WEBSOCKET_ENDPOINT;
import static java.lang.Math.cos;


public class HomeFragment extends Fragment implements OnMapReadyCallback, OnBackPressedListener {

    public static final int updateinterval = 5000;
    private static final int REQUEST_LOCATION = 11;
    private static final int[] CLUSTER_ICON_BUCKETS = {10, 20, 50, 100, 500, 1000, 5000, 10000, 20000};
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public static String mUserType = "";
    public static Location mDriverLocation;
    public static Location mPreDriverLocation;
    public static WebSocketClient mWebSocketClient;
    public static String userid = "";
    public Context context;
    //RideShareApp application;
    ArrayList<Rider> mlist = new ArrayList<>();
    LatLng currentlthg;
    LatLng startlthg;
    LatLng destinationLatLang;
    String destinationAddress = "";
    LatLng driverLatLang;
    CustomProgressDialog mProgressDialog;
    User mUserBean;
    MaterialDialog mMaterialDialog;
    TextView driver_distance_tv;
    TextView txtHeaderName, txtRole, txt_group_name;
    ProgressBar driver_distance_progress;
    RelativeLayout layout_spinner_radius, rlToolBar, rlToolBar2;
    //========================= Socket Connection ====================//
    float zoomLevel = 16f;
    boolean isDestroy = false;
    RideShareApp mApp;
    boolean isSetMarker;
    private GoogleMap mGoogleMap;
    private Marker curLocMarker;
    private Marker destinationLocationMarker;
    private int padding = 150;
    private LatLngBounds.Builder builder;
    private EditText mLocationSearchAtv;
    private ImageView mClearLocationIv;
    private Polyline directionLine;
    private TextView mSearchCabTv;
    private LinearLayout linearLayout;
    private ArrayList<Marker> mlistMarker;
    private String duration = "";
    private Spinner spinner_radius;
    private List<String> list_radius;
    private List<String> list_miles;
    private List<String> list_seats;
    private boolean isFirstSelected = false;
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {

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
                updateLineDestination(json);
            }
        }
    };
    private okhttp3.Callback updateRouteCallback2 = new okhttp3.Callback() {

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Directions directions = new Directions(getActivity());
                            List<Route> routes = directions.parse(json);
                            //if (directionLine != null) directionLine.remove();
                            if (routes.size() > 0) {
                                duration = routes.get(0).getLegs().get(0).getDuration().getText();
                                driver_distance_tv.setText(duration);
                                driver_distance_progress.setVisibility(View.GONE);
                                driver_distance_tv.setVisibility(View.VISIBLE);
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
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                if (mUserType.equals("2")) {
                    // Call Seats Availability API.
                    if (spinner_radius.getSelectedItemPosition() == 0) {
                        updateDriverDetails(userid, "1");
                    } else {
                        updateDriverDetails(userid, String.valueOf(spinner_radius.getSelectedItemPosition()));
                    }

                } else {
                    if (isFirstSelected) {
                        if (mlist != null) {
                            if (mlist.size() > 0) {
                                Rider rider = mlist.get(position);
                                LatLng latLng = new LatLng(Double.parseDouble(rider.getmLatitude()), Double.parseDouble(rider.getmLongitude()));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                ArrayList<Rider> newlist = new ArrayList<>();
                                newlist.add(rider);
                                createMarker(mUserType, newlist);
                            }
                        }
                    } else {
                        isFirstSelected = true;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // YOU WILL GET DATA HERE...
            //Log.d("Nikunj", RideShareApp.mLocation.toString());

            currentlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
            mDriverLocation = new Location("");
            mDriverLocation.setLatitude(currentlthg.latitude);
            mDriverLocation.setLongitude(currentlthg.longitude);
            if (curLocMarker == null) {
                try {
                    if (!isSetMarker) {
                        isSetMarker = true;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlthg).zoom(16).build();
                        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        setcutommarker(currentlthg, null, mUserBean, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                curLocMarker.setPosition(currentlthg);
            }

            builder.include(currentlthg);
        }
    };
    private PopupWindow popupWindow;
    private ImageView mPopupIv;

    public static HomeFragment newInstance(ArrayList<Rider> mlist) {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();
        bundle.putSerializable("list", mlist);
        fragment.setArguments(bundle);

        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_home, null);

        context = getContext();
        mProgressDialog = new CustomProgressDialog(getActivity());
        PrefUtils.initPreference(getActivity());
        mUserBean = PrefUtils.getUserInfo();

        //HomeActivity.setOnBackPressedListener(this);

        //application = (RideShareApp) getApplicationContext();
        mUserType = RideShareApp.getmUserType();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);


        mlist = (ArrayList<Rider>) getArguments().getSerializable("list");
        mlistMarker = new ArrayList<>();

        builder = new LatLngBounds.Builder();


        //mRobotoReguler = TypefaceUtils.getTypefaceRobotoMediam(getActivity());

        mLocationSearchAtv = rootview.findViewById(R.id.location_search);
        //mLocationSearchAtv.setTypeface(mRobotoReguler);

        mLocationSearchAtv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (AppUtils.isInternetAvailable(getActivity())) {
                        Intent i = new Intent(getActivity(), PlaceSerachActivity.class);
                        startActivityForResult(i, REQUEST_LOCATION);
                        /*Intent i = new Intent(getActivity(), AutoCompleteLocationActivity.class);
                        startActivityForResult(i, REQUEST_LOCATION);*/
                    } else {
                        MessageUtils.showNoInternetAvailable(context);
                    }
                }
                return true;
            }
        });

        mClearLocationIv = rootview.findViewById(R.id.clear_location_iv);
        mClearLocationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUserType.equals("2") && !mLocationSearchAtv.getText().toString().equals(""))
                    updateDestinationAddress(userid, "");
                if (directionLine != null) directionLine.remove();
                if (destinationLocationMarker != null) destinationLocationMarker.remove();
                mLocationSearchAtv.setText("");
            }
        });

        mSearchCabTv = rootview.findViewById(R.id.search_cab_iv);
        linearLayout = rootview.findViewById(R.id.linearLayout);


        userid = mUserBean.getmUserId();
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtils.isInternetAvailable(context)) {
                    if (currentlthg != null) {
                        //float radius_miles = Float.parseFloat(list_miles.get((int) spinner_radius.getSelectedItemId()));
                        //float radius_miles = Float.parseFloat(mlist.get((int) spinner_radius.getSelectedItemId()).getra);
                        if (mUserType.equals("1")) {
                            if (PrefUtils.getBoolean("isFriends")) {
                                selectRide(mUserBean.getmUserId(), "1", "" + currentlthg.latitude, "" + currentlthg.longitude, "1");
                            } else {
                                selectRide(mUserBean.getmUserId(), "2", "" + currentlthg.latitude, "" + currentlthg.longitude, "1");
                            }
                        } else {
                            if (PrefUtils.getBoolean("isFriends")) {
                                selectRide(mUserBean.getmUserId(), "1", "" + currentlthg.latitude, "" + currentlthg.longitude, "2");
                            } else {
                                selectRide(mUserBean.getmUserId(), "2", "" + currentlthg.latitude, "" + currentlthg.longitude, "2");
                            }
                        }
                    }
                } else {
                    MessageUtils.showNoInternetAvailable(context);
                }
            }
        });
        init(rootview);


        mapFragment.getView().setFocusableInTouchMode(true);
        mapFragment.getView().requestFocus();
        mapFragment.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mUpdaterHandler.removeCallbacks(runnable);
                    mUpdaterHandler.removeCallbacksAndMessages(null);
                    if (mWebSocketClient != null) {
                        mWebSocketClient.close();
                        mWebSocketClient = null;
                    }
                    /*Intent intent = new Intent(getActivity(), MyService.class);
                    getActivity().stopService(intent);*/

                    RideShareApp.mHomeTabPos = 0;
                    Intent i = new Intent(getActivity(), RideTypeActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
        getActivity().startService(new Intent(getActivity(), MyService.class));
        //getActivity().startService()
        return rootview;
    }

    private void init(View view) {
        mApp = new RideShareApp();
        txtHeaderName = view.findViewById(R.id.txtHeaderName);
        txt_group_name = view.findViewById(R.id.txt_group_name);
        spinner_radius = view.findViewById(R.id.spinner_radius);
        layout_spinner_radius = view.findViewById(R.id.layout_spinner_radius);
        mPopupIv = view.findViewById(R.id.popup_iv);
        txtRole = view.findViewById(R.id.txtRole);
        rlToolBar2 = view.findViewById(R.id.rlToolBar2);
        rlToolBar = view.findViewById(R.id.rlToolBar);
        if (PrefUtils.getString("isBlank").equals("true")) {
            CardView card_view_pin = view.findViewById(R.id.card_view_pin);
            card_view_pin.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            layout_spinner_radius.setVisibility(View.GONE);
            rlToolBar.setVisibility(View.INVISIBLE);
            rlToolBar2.setVisibility(View.VISIBLE);
        } else {
            rlToolBar.setVisibility(View.VISIBLE);
            rlToolBar2.setVisibility(View.GONE);
            spinner_radius.setOnItemSelectedListener(onItemSelectedListener);
            txt_group_name.setText("Group : " + PrefUtils.getString("SelectedGroup"));
            list_seats = new ArrayList<>();

            if (mUserType.equals("2")) {
                txtRole.setText("Driver");
                mSearchCabTv.setText("Find Rider");
                connectWebSocket();
                mUpdaterHandler.post(runnable);
                int maxSeats = Integer.parseInt(mUserBean.getCar_info().getSeating_capacity());
                for (int i = 0; i <= maxSeats; i++) {
                    list_seats.add(context.getResources().getStringArray(R.array.max_seats)[i]);
                }
            }
            bindSpinner(list_seats);

            mPopupIv = view.findViewById(R.id.popup_iv);
            mPopupIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        showPopup(v);
                    }
                }
            });
        }
    }

    private void updateSpinner() {
        if (mlist.size() > 0) {
            if (mUserType.equals("2")) {
                txtRole.setText("Driver");
                mSearchCabTv.setText("Find Rider");
                connectWebSocket();
                mUpdaterHandler.post(runnable);
                if (mlist.size() > 0) {
                    spinner_radius.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mlist.size() > 0) {
                                spinner_radius.setSelection(mlist.indexOf(mlist.get(0)));
                            }
                        }
                    });
                    spinner_radius.setAdapter(new SelectRadiusAdapter(mlist, null, context));
                }
            }
        } else {
            layout_spinner_radius.setVisibility(View.GONE);
        }
    }

    private void bindSpinner(final List<String> list_data) {
        if (list_data != null && list_data.size() > 0) {
            spinner_radius.post(new Runnable() {
                @Override
                public void run() {
                    if (list_data.size() > 0) {
                        spinner_radius.setSelection(list_data.indexOf(list_data.get(0)));
                    }
                }
            });
            spinner_radius.setAdapter(new SelectRadiusAdapter(null, list_data, context));
        } else if (mlist != null && mlist.size() > 0) {
            spinner_radius.post(new Runnable() {
                @Override
                public void run() {
                    if (mlist.size() > 0) {
                        spinner_radius.setSelection(mlist.indexOf(mlist.get(0)));
                    }
                }
            });
            spinner_radius.setAdapter(new SelectRadiusAdapter(mlist, null, context));
        }
    }

    public void showDialog(String msg) {

        mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle(getResources().getString(R.string.app_name))

                .setMessage(msg)
                .setPositiveButton("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });


        mMaterialDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (RideShareApp.mLocation == null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            currentlthg = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            currentlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
        }

        if (curLocMarker == null) {

            if (!isSetMarker) {
                isSetMarker = true;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlthg).zoom(16).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                setcutommarker(currentlthg, null, mUserBean, 1);
            }
        } else {
            curLocMarker.setPosition(currentlthg);
        }

        builder.include(currentlthg);

        mDriverLocation = new Location("");
        mDriverLocation.setLatitude(currentlthg.latitude);
        mDriverLocation.setLongitude(currentlthg.longitude);
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoomLevel = mGoogleMap.getCameraPosition().zoom;
            }
        });


        initMap();
    }

    public void initMap() {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setIndoorEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getSnippet() != null) {

                    Rider selectedRide = new Gson().fromJson(marker.getSnippet(), Rider.class);

                    if (!mUserType.equals("2") && mLocationSearchAtv.getText().toString().isEmpty()) {
                        MessageUtils.showFailureMessage(getActivity(), "Please select destination location.");
                    } else {
                        if (selectedRide != null) {
                            if (!selectedRide.getnUserId().equals(PrefUtils.getUserInfo().getmUserId())) {
                                if (selectedRide.getU_ride_type().equals("2")) {
                                    /*if (Float.parseFloat(selectedRide.getmDistance()) < Float.parseFloat(list_miles.get((int) spinner_radius.getSelectedItemId()))) {
                                        getRiderInfoDialog(selectedRide);
                                    }*/
                                    getRiderInfoDialog(selectedRide);
                                } else {
                                    getRiderInfoDialog(selectedRide);
                                }
                            }
                        }
                    }

                }
                return false;
            }
        });
    }

    private void createMarker(String mType, ArrayList<Rider> mlist) {

        if (!mlist.isEmpty()) {

            for (Marker m : mlistMarker) {
                m.remove();
            }

            mlistMarker.clear();
            for (int i = 0; i < mlist.size(); i++) {

                Rider driver = mlist.get(i);

                Location locationCurrent = new Location("Current");
                locationCurrent.setLatitude(Double.parseDouble(String.format("%.6f", Double.parseDouble(driver.getmLatitude()))));
                locationCurrent.setLongitude(Double.parseDouble(String.format("%.6f", Double.parseDouble(driver.getmLongitude()))));

                for (Rider driverFilter : mlist) {
                    if (!driverFilter.getnUserId().equals(driver.getnUserId())) {
                        Location locationPrevious = new Location("Previous");
                        locationPrevious.setLatitude(Double.parseDouble(driverFilter.getmLatitude()));
                        locationPrevious.setLongitude(Double.parseDouble(driverFilter.getmLongitude()));
                        double distance = locationCurrent.distanceTo(locationPrevious);
                        if (distance < 25) {

                            Double lat = Double.parseDouble(driver.getmLatitude());
                            Double lon = Double.parseDouble(driver.getmLongitude());

                            //Earth’s radius, sphere
                            double R = 6378137;

                            //offsets in meters
                            double dn = 10;
                            double de = 10;

                            //Coordinate offsets in radians
                            double dLat = dn / R;
                            double dLon = de / (R * cos(Math.PI * lat / 180));

                            //OffsetPosition, decimal degrees
                            double newlat = lat + dLat * 180 / Math.PI;
                            double newlon = lon + dLon * 180 / Math.PI;
                            driver.setmLatitude(String.valueOf(newlat));
                            driver.setmLongitude(String.valueOf(newlon));
                            mlist.set(i, driver);
                            break;
                        }
                    }

                }
                LatLng currentDriverPos = new LatLng(Double.parseDouble(driver.getmLatitude()), Double.parseDouble(driver.getmLongitude()));
                builder.include(currentDriverPos);
                Marker m;
                if (mType.equals("1")) {
                    m = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                            .position(currentDriverPos).anchor(0.5f, 1f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                            .rotation(0f)
                            .flat(true));
                    mlistMarker.add(m);
                } else {
                    getMarkerBitmapFromView(getActivity(), driver, mUserBean, 0, currentDriverPos);
                }
            }


            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cu);
            connectWebSocket();
        } else {
            for (Marker m : mlistMarker) {
                m.remove();
            }
        }
    }

    //======================= APIS =================================//

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && data != null) {
            SearchPlace location = (SearchPlace) data.getSerializableExtra("location");
            mLocationSearchAtv.setText(location.getmArea() + " " + location.getmAddress());
            destinationLatLang = new LatLng(Double.parseDouble(location.getmLatitude()), Double.parseDouble(location.getmLongitude()));

            startlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
            destinationAddress = location.getmArea() + "," + location.getmAddress();

            CameraPosition cameraPosition = new CameraPosition.Builder().target(destinationLatLang).zoom(16).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (destinationLocationMarker != null) {
                destinationLocationMarker.remove();
            }
            destinationLocationMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker())
                    .position(destinationLatLang).title("destination"));
            if (!mUserType.equals("2"))
                updateDestinationAddress(userid, location.getmAddress());

        }
    }

    private void updateDestinationAddress(String userid, final String destination_address) {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).updateDestinationAddress(userid, destination_address).enqueue(new Callback<UpdateDestinationAddress>() {
            @Override
            public void onResponse(Call<UpdateDestinationAddress> call, Response<UpdateDestinationAddress> response) {

                if (response.isSuccessful() && response.body() != null) {
                    requestRoute(false);
                }
            }

            @Override
            public void onFailure(Call<UpdateDestinationAddress> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    private void requestRoute(boolean isClicked) {
        if (currentlthg != null) {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            if (isClicked) {
                MapDirectionAPI.getDirection(currentlthg, driverLatLang, context).enqueue(updateRouteCallback2);
            } else if (destinationLatLang != null) {
                MapDirectionAPI.getAlterNativeDirection(currentlthg, destinationLatLang, context).enqueue(updateRouteCallback);
            }

        }
    }

    private void updateDriverDetails(String userid, String max_seats) {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).updateDriverDetails(userid, max_seats).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    private void updateLineDestination(final String json) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Directions directions = new Directions(getActivity());
                    List<Route> routes = directions.parse(json);
                    if (directionLine != null) directionLine.remove();
                    if (routes.size() > 0) {

                        directionLine = mGoogleMap.addPolyline((new PolylineOptions())
                                .addAll(routes.get(0).getOverviewPolyLine())
                                .color(ContextCompat.getColor(getActivity(), R.color.blacltext))
                                .width(10));
                        directionLine.setClickable(true);
                    } else {
                        Toast.makeText(context, json, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void selectRide(String mId, final String mType, String latitude, String longitude, final String mRideType) {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).getUser(mId, mType, latitude, longitude, mRideType, PrefUtils.getString("SelectedGroupID")).enqueue(new Callback<RideSelect>() {
            @Override
            public void onResponse(Call<RideSelect> call, Response<RideSelect> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMlistUser().size() == 0) {
                        layout_spinner_radius.setVisibility(View.GONE);
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                        }
                        mlist.clear();
                        builder = new LatLngBounds.Builder();
                        builder.include(currentlthg);
                        createMarker(mRideType, mlist);
                        String msg = mRideType.equals("1") ? "No drivers are available" : "No riders are available";
                        showDialog(msg);
                    } else {
                        layout_spinner_radius.setVisibility(View.VISIBLE);
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                        }
                        mlist.clear();

                        mlist.addAll(response.body().getMlistUser());
                        isFirstSelected = false;
                        if (!mUserType.equals("2")) {
                            bindSpinner(null);
                        }

                        /*if (mRideType.equals("1")) {
                            mUpdaterHandler.post(runnable);
                        }*/
                        builder = new LatLngBounds.Builder();
                        builder.include(currentlthg);
                        createMarker(mRideType, mlist);
                    }
                } else {
                    try {
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                        }
                        mlist.clear();
                        builder = new LatLngBounds.Builder();
                        builder.include(currentlthg);
                        createMarker(mRideType, mlist);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MessageUtils.showFailureMessage(getActivity(), "No drivers are available");
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<RideSelect> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    public void getRiderInfoDialog(final Rider rider) {

        final Dialog dialog = new Dialog(getActivity());

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.get_ride_info_dialog);
        CardView mllCustomDialogError = dialog.findViewById(R.id.card_view_pin);

        mllCustomDialogError.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (AppUtils.getDeviceWidth(getActivity()) / 1.2),
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView mDetails_tv = dialog.findViewById(R.id.ride_type_details_tv);
        TextView mNameTv = dialog.findViewById(R.id.name_tv);
        TextView mVahicalTv = dialog.findViewById(R.id.vahical_tv);
        TextView mAddressTv = dialog.findViewById(R.id.address_tv);
        LinearLayout mOther_info = dialog.findViewById(R.id.ride_other_info);
        TextView txt_maxPerson = dialog.findViewById(R.id.txt_maxPerson);
        TextView mGetRideTv = dialog.findViewById(R.id.get_ride_tv);
        TextView mCancelTv = dialog.findViewById(R.id.cancel_ride_tv);
        TextView txt_divider = dialog.findViewById(R.id.txt_divider);
        LinearLayout layout_driver_destination = dialog.findViewById(R.id.layout_driver_destination);
        TextView destination_address_tv = dialog.findViewById(R.id.destination_address_tv);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        RelativeLayout layout_persons = dialog.findViewById(R.id.layout_persons);
        SimpleRatingBar driver_rate = dialog.findViewById(R.id.driver_rate);
        driver_distance_tv = dialog.findViewById(R.id.driver_distance_tv);
        driver_distance_progress = dialog.findViewById(R.id.driver_distance_progress);

        ImageView userImage = dialog.findViewById(R.id.userImage);
        //cancel_driver_tv
        LinearLayout rider_layout = dialog.findViewById(R.id.rider_layout);
        LinearLayout driver_layout = dialog.findViewById(R.id.driver_layout);
        //yourTextView.setText(String.format("Value of a: %.2f", a));
        driverLatLang = new LatLng(Double.parseDouble(rider.getmLatitude()), Double.parseDouble(rider.getmLongitude()));
        driver_distance_tv.setVisibility(View.GONE);

        try {
            mNameTv.setText(rider.getmFirstName());
            //mAddressTv.setText(rider.getmAddress());
            mAddressTv.setText(AppUtils.getAddress(context, Double.parseDouble(rider.getmLatitude()), Double.parseDouble(rider.getmLongitude())));
            txt_maxPerson.setText("0" + rider.getNo_of_seats());
            requestRoute(true);
            driver_distance_tv.setText(duration + " Min");
            if (mUserType.equals("2") || !rider.getmType().equals("2")) {
                mDetails_tv.setText("Rider details");
                if (rider.getDestination_address().equals("")) {
                    txt_divider.setVisibility(View.GONE);
                    layout_driver_destination.setVisibility(View.GONE);
                } else {
                    txt_divider.setVisibility(View.VISIBLE);
                    layout_driver_destination.setVisibility(View.VISIBLE);
                    destination_address_tv.setText(rider.getDestination_address());
                }
                //mGetRideTv.setText("Offer Ride");
                mVahicalTv.setText(rider.getmLastName());

                driver_layout.setVisibility(View.VISIBLE);
                rider_layout.setVisibility(View.GONE);
                layout_persons.setVisibility(View.GONE);
                driver_rate.setVisibility(View.GONE);
                // mVahicalTv
            } else {
                layout_driver_destination.setVisibility(View.GONE);
                driver_rate.setVisibility(View.VISIBLE);
                driver_rate.setFocusable(false);
                driver_rate.setIndicator(true);
                driver_rate.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                driver_rate.setRating(Float.parseFloat(rider.getAverage_rate()));
                layout_persons.setVisibility(View.VISIBLE);
                driver_layout.setVisibility(View.GONE);
                rider_layout.setVisibility(View.VISIBLE);
            }
            Glide.with(this).load(rider.getThumb_image())
                    .error(R.drawable.user_icon)
                    .into(userImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rider.setNo_of_seats("1");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                rider.setNo_of_seats(String.valueOf(radioGroup.indexOfChild(radioButton) + 1));
            }
        });

        driver_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        mGetRideTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                GetRideID(rider);
            }
        });

        dialog.show();
    }

    private void GetRideID(final Rider rider) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).getRideId(rider.getnUserId(), userid).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject resp = null;
                try {
                    if (response.body() != null) {
                        resp = new JSONObject(response.body().toString());
                        if (resp.optString("status").equals("success")) {
                            try {
                                String ride_id = resp.optString("result").equals("") ? "" : resp.getJSONObject("result").optString("ride_id");
                                /*if (!ride_id.equals(""))

                                else {
                                    MessageUtils.showFailureMessage(getActivity(), "Rider's Seats are not Available");
                                    mProgressDialog.dismiss();
                                }*/
                                SendRideRequest(rider.getnUserId(), mUserBean.getmUserId(), rider, ride_id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    public void SendRideRequest(String userid, String fromuserid, final Rider rider, final String ride_id) {
        ApiServiceModule.createService(RestApiInterface.class, context).sendRequest(userid, fromuserid, "" + currentlthg.latitude, "" + currentlthg.longitude, "" + destinationLatLang.latitude, "" + destinationLatLang.longitude, mUserType, "", "", "", ride_id, rider.getNo_of_seats()).enqueue(new Callback<SendResponse>() {
            @Override
            public void onResponse(Call<SendResponse> call, final Response<SendResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getmStatus().equals("success")) {
                        Log.w("Rider ID :: >>>>>>>> ", response.body().getMlist().get(0).getRide_id());
                        final User user = new User();
                        user.setRiderID(response.body().getMlist().get(0).getRide_id());
                        user.setmUserId(mUserBean.getmUserId());
                        user.setThumb_image(mUserBean.getThumb_image());
                        user.setmFirstName(mUserBean.getmFirstName());
                        user.setmLatitude(mUserBean.getmLatitude());
                        user.setmLongitude(mUserBean.getmLongitude());
                        user.setmAddress(mUserBean.getmAddress());
                        user.setmEmail(mUserBean.getmEmail());
                        user.setStart_lat(String.valueOf(startlthg.latitude));
                        user.setStart_long(String.valueOf(startlthg.longitude));
                        user.setEnd_long(String.valueOf(destinationLatLang.longitude));
                        user.setEnd_lat(String.valueOf(destinationLatLang.latitude));
                        user.setStart_address(AppUtils.getAddress(context, startlthg.latitude, startlthg.longitude));
                        user.setEnd_address(destinationAddress);
                        user.setRequest_share_id(response.body().getMlist().get(0).getRequest_share_id());
                        Log.w("Request ID :: >>>>>>>> ", response.body().getMlist().get(0).getRequest_share_id());

                        if (mApp.mWebSocketSendRequest == null) {
                            mApp.connectRideRequest();
                            mApp.setSocketConnection(new SocketConnection() {
                                @Override
                                public void onMessageReceived(String response) {

                                }

                                @Override
                                public void onConnected() {
                                    rider.setRideID(ride_id);
                                    sendDriverRequest(context, user, rider);
                                }
                            });
                        } else {
                            rider.setRideID(ride_id);
                            sendDriverRequest(context, user, rider);
                        }
                        /*if(mApp.mWebSocketSendRequest.isOpen())
                            sendDriverRequest(context, user, rider);
                        else
                            MessageUtils.showFailureMessage(context,"Socket is not Connected please restart.!");*/
                    } else if (response.body().getMessage() != null && !response.body().getMessage().equals("")) {
                        MessageUtils.showFailureMessage(getActivity(), "Rider's Seats are not Available");
                    } else if (response.body().getmStatus().equals("error")) {
                        MessageUtils.showFailureMessage(getActivity(), "Driver is no longer available");

                        if (currentlthg != null) {
                            if (mUserType.equals("1")) {
                                if (PrefUtils.getBoolean("isFriends")) {
                                    selectRide(mUserBean.getmUserId(), "1", "" + currentlthg.latitude, "" + currentlthg.longitude, "1");
                                } else {
                                    selectRide(mUserBean.getmUserId(), "2", "" + currentlthg.latitude, "" + currentlthg.longitude, "1");
                                }
                            } else {
                                if (PrefUtils.getBoolean("isFriends")) {
                                    selectRide(mUserBean.getmUserId(), "1", "" + currentlthg.latitude, "" + currentlthg.longitude, "2");
                                } else {
                                    selectRide(mUserBean.getmUserId(), "2", "" + currentlthg.latitude, "" + currentlthg.longitude, "2");
                                }
                            }
                        }
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SendResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    private void setcutommarker(LatLng currentDriverPos, Rider driver, User user, int type) {
        try {
            getMarkerBitmapFromView(getActivity(), driver, user, type, currentDriverPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMarkerBitmapFromView(final Activity activity, final Rider driver, final User user, final int type, final LatLng currentDriverPos) {

        try {
            final View customMarkerView = activity.getLayoutInflater().inflate(R.layout.item_custom_marker, null);

            CircleImageView markerImageView = customMarkerView.findViewById(R.id.user_dp);
            String userimage = "";
            if (type == 0) {
                //userimage = driver.getmProfileImage();
                userimage = driver.getThumb_image();
            } else {
                userimage = user.getThumb_image();
            }

            final String finalUserimage = userimage;
            if (userimage == null) {
                userimage = "";
            }
            Glide.with(activity).load(userimage).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (type != 0) {

                        if (curLocMarker == null) {
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(user))
                                    //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                    .position(currentDriverPos).anchor(0.5f, 1f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                                    // Specifies the anchor to be at a particular point in the marker image.
                                    .rotation(0f)
                                    .flat(true));
                            curLocMarker.showInfoWindow();
                        } else {
                            curLocMarker.setPosition(currentDriverPos);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    Bitmap icon = AppUtils.drawableToBitmap(resource);

                    Marker m = null;
                    if (type == 0) {
                        Bitmap bmImg = AppUtils.getMarkerBitmapFromView(getActivity(), icon, false, true, driver.getDestination_address());
                        IconGenerator iconFactory = new IconGenerator(activity);
                        iconFactory.setColor(Color.CYAN);

                        m = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                                .position(currentDriverPos).anchor(0.5f, 1f)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                .rotation(0f)
                                .flat(true)
                                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
                        mlistMarker.add(m);
                    } else {
                        try {
                            Bitmap bmImg = AppUtils.getMarkerBitmapFromView(getActivity(), icon, false, false, "");
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(user))
                                    //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                    .position(currentDriverPos).anchor(0.5f, 1f)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                    // Specifies the anchor to be at a particular point in the marker image.
                                    .rotation(0f)
                                    .flat(true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    return false;
                }
            }).error(R.drawable.ic_user_pin).placeholder(R.drawable.ic_user_pin).dontAnimate().into(markerImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doBack() {
        getActivity().finish();
    }

    public void showPopup(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);


        final RadioButton mFriendRb = popupView.findViewById(R.id.fridnds_rb);
        final RadioButton mAllRb = popupView.findViewById(R.id.all_rb);

        if (PrefUtils.getBoolean("isFriends")) {
            mFriendRb.setChecked(true);
        } else if (PrefUtils.getBoolean("isAll")) {
            mAllRb.setChecked(true);
        } else {
            PrefUtils.putBoolean("isFriends", false);
            PrefUtils.putBoolean("isAll", true);
            mAllRb.setChecked(true);
        }

        mFriendRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFriendRb.setChecked(true);
                    mAllRb.setChecked(false);
                    PrefUtils.putBoolean("isFriends", true);
                    PrefUtils.putBoolean("isAll", false);
                    syncContact();
                    //updateFriendListType(mUserBean.getmUserId(),"1");
                } else {
                    mFriendRb.setChecked(false);
                    mAllRb.setChecked(true);
                    PrefUtils.putBoolean("isFriends", false);
                    PrefUtils.putBoolean("isAll", true);
                }
                popupWindow.dismiss();

            }
        });
        mAllRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFriendRb.setChecked(false);
                    mAllRb.setChecked(true);
                    PrefUtils.putBoolean("isAll", true);
                    PrefUtils.putBoolean("isFriends", false);
                    //updateFriendListType(mUserBean.getmUserId(),"2");
                } else {
                    mFriendRb.setChecked(true);
                    mAllRb.setChecked(false);
                    PrefUtils.putBoolean("isAll", false);
                    PrefUtils.putBoolean("isFriends", true);
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(v);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (popupWindow != null)
                popupWindow.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isDestroy = false;
        SocketListener();
        try {
            if (popupWindow != null)
                popupWindow.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }


        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationReceiver, new IntentFilter("update-location"));
    }


    //==================== Connect to Web Socket =========================//1005

    private void SocketListener() {
        if (mUserType.equals("2")) {
            if (mApp.mWebSocketSendRequest == null) {
                mApp.connectRideRequest();
            }
        }
        mApp.setSocketConnection(new SocketConnection() {
            @Override
            public void onMessageReceived(String response) {
                JSONObject jobj;
                try {
                    jobj = new JSONObject(response);
                    if (!jobj.getString("chat_message").equals("null") && jobj.getString("sender_user").equals("1002")) {
                        Gson gson = new Gson();
                        try {
                            Type type = new TypeToken<User>() {
                            }.getType();
                            User fromUser = gson.fromJson(jobj.getString("chat_message"), type);
                            if (mUserBean.getmUserId().equals(jobj.getString("username"))) {
                                mApp.mWebSocketSendRequest.close();
                                mApp.mWebSocketSendRequest = null;
                                Intent intent = new Intent(context, NotificationViewActivity.class);
                                intent.putExtra("fromUSerData", fromUser);
                                startActivity(intent);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
            public void onMessage(final String s) {
                try {

                    final JSONObject jobj = new JSONObject(s);
                    //if (mApp.getmUserType().equals("1")) {
                    Log.d("Message ", "Received :::>>> " + jobj.toString());
                    if (!jobj.getString("message_type").equals("chat-connection-ack")) {
                        if (!jobj.getString("chat_message").equals("null") && jobj.getString("sender_user").equals("1001")) {
                            getActivity().runOnUiThread(new Runnable() {
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
                                        if (mlist != null) {
                                            if (mlist.size() > 0) {
                                                for (int i = 0; i < mlist.size(); i++) {
                                                    if (jobj.getString("username").equals(mlist.get(i).getnUserId())) {
                                                        animateMarkerNew(i, mlistMarker.get(i),
                                                                new LatLng(mDriverLocation.getLatitude(),
                                                                        mDriverLocation.getLongitude()));

                                                    }
                                                }
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
               Log.i("Websocket", "Closed " + s);
                /*if (!isDestroy) {
                    if (mWebSocketClient != null) {
                        mWebSocketClient.reconnect();
                    }
                }*/


            }

            @Override
            public void onError(final Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                /*if (!isDestroy) {
                    if (mWebSocketClient != null) {
                        if (!mWebSocketClient.isClosed()) {
                            // mWebSocketClient.reconnect();
                        }
                    }
                }*/
            }
        };
        mWebSocketClient.setConnectionLostTimeout(0);
        mWebSocketClient.connect();
    }


    private void animateMarkerNew(int pos, final Marker marker, final LatLng newlatlng) {

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

            try {
                JSONObject jFromRider = new JSONObject(marker.getSnippet());
                jFromRider.put("u_lat", String.valueOf(newlatlng.latitude));
                jFromRider.put("u_long", String.valueOf(newlatlng.longitude));
                //new MarkerOptions().snippet(new Gson().toJson(rider));
                marker.setSnippet(jFromRider.toString());
                mlistMarker.set(pos, marker);
                //UpdateLocation(rider.getnUserId(),rider.getmLatitude(),rider.getmLongitude());
                UpdateUserLocation(jFromRider.getString("u_id"), String.valueOf(newlatlng.latitude),
                        String.valueOf(newlatlng.longitude));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private float getBearing(LatLng begin, LatLng end) {

        double PI = 3.14159;
        double lat1 = begin.latitude * PI / 180;
        double long1 = begin.longitude * PI / 180;
        double lat2 = end.latitude * PI / 180;
        double long2 = end.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * cos(lat2);
        double x = cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * cos(lat2) * cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return (float) brng;
    }

    public void UpdateUserLocation(String userid, String u_lat, String u_long) {
        //mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).updateUserLocation(userid, u_lat, u_long).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Success", "");
                } else {
                    Log.d("Failed", "");
                }
                //mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                //mProgressDialog.cancel();
            }
        });
    }

    public void sendDriverRequest(Context context, User mUserBean, Rider rider) {
        JSONObject jMessage = new JSONObject();
        try {
            Gson gson = new Gson();

            //gson.toJson();
            jMessage.put("chat_message", gson.toJson(mUserBean));
            jMessage.put("chat_user", rider.getnUserId());
            jMessage.put("sender_user", "1002");
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");

            if (mApp.mWebSocketSendRequest != null) {
                if (mApp.mWebSocketSendRequest.isClosing() || mApp.mWebSocketSendRequest.isClosed()) {
                    Log.w("Message", "Closed >>> ");
                    //MessageUtils.showWarningMessage(context, "Please Wait \n Your Socket connection is Lost Try to Reconnect to the Server");
                    //mWebSocketClient.reconnect();
                    //Log.w("Message", "Sent >>> " + jMessage.toString());
                }
                if (mApp.mWebSocketSendRequest.isOpen()) {
                    mApp.mWebSocketSendRequest.send(jMessage.toString());
                    Log.w("Message", "Sent Requests>>> " + jMessage.toString());
                    Intent intent = new Intent(context, WaitingActivity.class);
                    intent.putExtra("NotificationData", rider);
                    intent.putExtra("UserData", mUserBean);
                    context.startActivity(intent);
                    Log.w("Message", "Client Socket Data Sent >>> " + jMessage.toString());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //==================== Socket Connection for Send Request ==============//

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;

    }

    private void syncContact() {

        mProgressDialog.show();
        ArrayList<ContactBean> mlist = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);

        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    ContactBean bean = new ContactBean();
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    bean.setName(name);
                    bean.setMobile("+" + number.replaceAll("[^0-9]+", ""));
                    mlist.add(bean);
                }
            } finally {
                cursor.close();
            }
        }

        final ContactRequest request = new ContactRequest();
        request.setUser_id(mUserBean.getmUserId());
        request.setContact(mlist);
        ApiServiceModule.createService(RestApiInterface.class, context).syncContact(request).enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getStatus().equals("success")) {

                        MessageUtils.showSuccessMessage(context, "Contact Sync");
                    } else {
                        MessageUtils.showFailureMessage(context, "Contact Sync failed");
                    }
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
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
}

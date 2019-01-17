package com.app.rideWhiz.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.AutoCompleteLocationActivity;
import com.app.rideWhiz.activity.RideShareApp;
import com.app.rideWhiz.activity.WaitingActivity;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.RideSelect;
import com.app.rideWhiz.api.response.SendResponse;
import com.app.rideWhiz.listner.OnBackPressedListener;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements OnMapReadyCallback, OnBackPressedListener {

    private static final int REQUEST_LOCATION = 11;
    //RideShareApp application;
    ArrayList<Rider> mlist = new ArrayList<>();
    LatLng currentlthg;
    LatLng destinationLatLang;
    CustomProgressDialog mProgressDialog;
    User mUserBean;
    MaterialDialog mMaterialDialog;
    private GoogleMap mGoogleMap;
    private Marker curLocMarker;
    private Marker destinationLocationMarker;
    private int padding = 150;
    private LatLngBounds.Builder builder;
    private String mUserType = "";
    private EditText mLocationSearchAtv;
    private ImageView mClearLocationIv;
    private Polyline directionLine;
    private TextView mSearchCabTv;
    private LinearLayout linearLayout;
    private ArrayList<Marker> mlistMarker;
    private String duration;
    Context context;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        mLocationSearchAtv = (EditText) rootview.findViewById(R.id.location_search);
        //mLocationSearchAtv.setTypeface(mRobotoReguler);

        mLocationSearchAtv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent i = new Intent(getActivity(), AutoCompleteLocationActivity.class);
                    startActivityForResult(i, REQUEST_LOCATION);
                }
                return true;
            }
        });

        mClearLocationIv = (ImageView) rootview.findViewById(R.id.clear_location_iv);
        mClearLocationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationSearchAtv.setText("");
                if (directionLine != null) directionLine.remove();
                if (destinationLocationMarker != null) destinationLocationMarker.remove();
            }
        });

        mSearchCabTv = (TextView) rootview.findViewById(R.id.search_cab_iv);
        linearLayout = rootview.findViewById(R.id.linearLayout);
        //mSearchCabTv.setTypeface(mRobotoReguler);
        if(mUserType.equals("")){
            mUserType = mUserBean.getmRideType();
        }
        if (mUserType.equals("2")) {
            mSearchCabTv.setText("Find Rider");
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        if (PrefUtils.getString("isBlank").equals("true")) {
            CardView card_view_pin = (CardView) rootview.findViewById(R.id.card_view_pin);
            mPopupIv = (ImageView) rootview.findViewById(R.id.popup_iv);
            card_view_pin.setVisibility(View.GONE);
            mPopupIv.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }

        init(rootview);

        return rootview;
    }

    public void showDialog() {
        mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle(getResources().getString(R.string.app_name))

                .setMessage("No drivers are available")
                .setPositiveButton("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });


        mMaterialDialog.show();
    }

    boolean isSetMarker;


    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // YOU WILL GET DATA HERE...

            //Location location = new Location("");//provider name is unnecessary
            //location.setLatitude((Double) intent.getExtras().get("Latitude"));//your coords of course
            //location.setLongitude((Double) intent.getExtras().get("Longitude"));

            Log.d("Nikunj", RideShareApp.mLocation.toString());

            currentlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());

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
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        currentlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());

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

                    if (mLocationSearchAtv.getText().toString().isEmpty()) {
                        MessageUtils.showFailureMessage(getActivity(), "Please select destination location.");
                    } else {
                        if (selectedRide != null) {
                            if (!selectedRide.getnUserId().equals(PrefUtils.getUserInfo().getmUserId())) {
                                if (selectedRide.getU_ride_type().equals("2")) {
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

    private void createMarker(String mType) {

        if (!mlist.isEmpty()) {

            for (Marker m : mlistMarker) {
                m.remove();
            }

            mlistMarker.clear();

            BitmapDescriptor icon = null;
            /*if (mUserType.equals("1")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin);

            } else if (mUserType.equals("2")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin);
            }*/

            for (Rider driver : mlist) {

                LatLng currentDriverPos = new LatLng(Double.parseDouble(driver.getmLatitude()), Double.parseDouble(driver.getmLongitude()));
                builder.include(currentDriverPos);
                /*Marker m = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                        .position(currentDriverPos).anchor(0.5f, 0.5f)
                        .rotation(0f)
                        .flat(true)
                        .icon(icon));*/
                Marker m;
                if (mType.equals("1")) {
                    m = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                            //.position(currentDriverPos).anchor(0.5f, 0.5f)
                            .position(currentDriverPos).anchor(0.5f, 1f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                            // Specifies the anchor to be at a particular point in the marker image.
                            .rotation(0f)
                            .flat(true));

                    mlistMarker.add(m);
                } else {

                    getMarkerBitmapFromView(getActivity(), driver, mUserBean, 0, currentDriverPos);

                    //m = setcutommarker(currentDriverPos, driver, mUserBean, 0);
                    //getMarkerBitmapFromView(getActivity(), driver, mUserBean, 0, currentDriverPos);

                    //mlistMarker.add(m);
                }


            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cu);
        } else {
            for (Marker m : mlistMarker) {
                m.remove();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && data != null) {
            SearchPlace location = (SearchPlace) data.getSerializableExtra("location");
            mLocationSearchAtv.setText(location.getmArea() + " " + location.getmAddress());
            destinationLatLang = new LatLng(Double.parseDouble(location.getmLatitude()), Double.parseDouble(location.getmLongitude()));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(destinationLatLang).zoom(16).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (destinationLocationMarker != null) {
                destinationLocationMarker.remove();
            }
            destinationLocationMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker())
                    .position(destinationLatLang).title("destination"));
            requestRoute();
        }
    }

    private void requestRoute() {
        if (currentlthg != null && destinationLatLang != null) {
            mProgressDialog.show();
            MapDirectionAPI.getDirection(currentlthg, destinationLatLang, context).enqueue(updateRouteCallback);
        }
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

    private void selectRide(String mId, String mType, String latitude, String longitude, final String mRideType) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).getUser(mId, mType, latitude, longitude, mRideType).enqueue(new Callback<RideSelect>() {
            @Override
            public void onResponse(Call<RideSelect> call, Response<RideSelect> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMlistUser().size() == 0) {
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                        }
                        mlist.clear();
                        builder = new LatLngBounds.Builder();
                        builder.include(currentlthg);
                        createMarker(mRideType);
                        showDialog();
                    } else {
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                        }
                        mlist.clear();
                        mlist.addAll(response.body().getMlistUser());
                        builder = new LatLngBounds.Builder();
                        builder.include(currentlthg);
                        createMarker(mRideType);
                    }
                } else {
                    try {
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                        }
                        mlist.clear();
                        builder = new LatLngBounds.Builder();
                        builder.include(currentlthg);
                        createMarker(mRideType);
                    } catch (Exception e) {
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

    public void getRiderInfoDialog(final Rider rider) {
        final Dialog dialog = new Dialog(getActivity());

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.get_ride_info_dialog);
        CardView mllCustomDialogError = (CardView) dialog.findViewById(R.id.card_view_pin);

        mllCustomDialogError.setLayoutParams(new LinearLayout.LayoutParams(
                (int) (AppUtils.getDeviceWidth(getActivity()) / 1.2),
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView mDetails_tv = (TextView) dialog.findViewById(R.id.ride_type_details_tv);
        TextView mNameTv = (TextView) dialog.findViewById(R.id.name_tv);
        TextView mVahicalTv = (TextView) dialog.findViewById(R.id.vahical_tv);
        TextView mAddressTv = (TextView) dialog.findViewById(R.id.address_tv);
        LinearLayout mOther_info = (LinearLayout) dialog.findViewById(R.id.ride_other_info);
        TextView txt_maxPerson = dialog.findViewById(R.id.txt_maxPerson);
        TextView mGetRideTv = (TextView) dialog.findViewById(R.id.get_ride_tv);
        TextView mCancelTv = (TextView) dialog.findViewById(R.id.cancel_ride_tv);
        TextView cancel_driver_tv = (TextView) dialog.findViewById(R.id.cancel_driver_tv);
        ImageView userImage = dialog.findViewById(R.id.userImage);
        //cancel_driver_tv
        LinearLayout rider_layout = dialog.findViewById(R.id.rider_layout);
        LinearLayout driver_layout = dialog.findViewById(R.id.driver_layout);
        //yourTextView.setText(String.format("Value of a: %.2f", a));
        try {
            mNameTv.setText(rider.getmFirstName());
            mAddressTv.setText(rider.getmAddress());
            txt_maxPerson.setText("0" + rider.getMax_passengers());
            if (mUserType.equals("2") || !rider.getmType().equals("2")) {
                mDetails_tv.setText("Rider details");
                mGetRideTv.setText("Offer Ride");
                mVahicalTv.setText(rider.getmLastName());
                mOther_info.setVisibility(View.GONE);
                driver_layout.setVisibility(View.VISIBLE);
                rider_layout.setVisibility(View.GONE);
                try {
                    Glide.with(this).load(rider.getThumb_image())
                            .error(R.drawable.user_icon)
                            .into(userImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // mVahicalTv
            } else {
                driver_layout.setVisibility(View.GONE);
                rider_layout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*mGetRideTv.setTypeface(mRobotoReguler);
        mCancelTv.setTypeface(mRobotoReguler);*/

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
                SendRideRequest(rider.getnUserId(), mUserBean.getmUserId(), rider);
            }
        });

        dialog.show();
    }

    public void SendRideRequest(String userid, String fromuserid, final Rider rider) {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).sendRequest(userid, fromuserid, "" + currentlthg.latitude, "" + currentlthg.longitude, "" + destinationLatLang.latitude, "" + destinationLatLang.longitude, mUserType, "", "", "").enqueue(new Callback<SendResponse>() {
            @Override
            public void onResponse(Call<SendResponse> call, Response<SendResponse> response) {


                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getmStatus().equals("success")) {
                        Intent i = new Intent(getActivity(), WaitingActivity.class);
                        i.putExtra("rider", rider);
                        i.putExtra("rider_data", response.body().getMlist().get(0));
                        startActivity(i);
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

    private Marker setcutommarker(LatLng currentDriverPos, Rider driver, User user, int type) {
        String userImage;
        Marker marker = null;
        try {
            if (type == 0) {
                userImage = driver.getmProfileImage();
            } else {
                //mGoogleMap.clear();
                //userImage = user.getProfile_image();
                userImage = user.getThumb_image();
            }
            getMarkerBitmapFromView(getActivity(), driver, user, type, currentDriverPos);

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

    public void getMarkerBitmapFromView(Activity activity, final Rider driver, final User user, final int type, final LatLng currentDriverPos) {

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
            if(userimage == null){
                userimage = "";
            }
            Glide.with(activity).load(userimage).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (type != 0) {

                        if(curLocMarker == null) {
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(user))
                                    //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                    .position(currentDriverPos).anchor(0.5f, 1f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin))
                                    // Specifies the anchor to be at a particular point in the marker image.
                                    .rotation(0f)
                                    .flat(true));
                        } else {
                            curLocMarker.setPosition(currentDriverPos);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                    Bitmap icon = AppUtils.drawableToBitmap(resource);

                    Bitmap bmImg = AppUtils.getMarkerBitmapFromView(getActivity(), icon);

                    Marker m = null;
                    if (type == 0) {

                        m = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                                //.position(currentDriverPos).anchor(0.5f, 0.5f)
                                .position(currentDriverPos).anchor(0.5f, 1f)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                // Specifies the anchor to be at a particular point in the marker image.
                                .rotation(0f)
                                .flat(true));

                        mlistMarker.add(m);
                    } else {
                        try {
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
            }).error(R.drawable.ic_user_pin).placeholder(R.drawable.ic_user_pin).into(markerImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void doBack() {
        getActivity().finish();
    }


    private PopupWindow popupWindow;
    private ImageView mPopupIv;

    private void init(View view) {

        mPopupIv = (ImageView) view.findViewById(R.id.popup_iv);
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

    public void showPopup(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);


        final RadioButton mFriendRb = (RadioButton) popupView.findViewById(R.id.fridnds_rb);
        final RadioButton mAllRb = (RadioButton) popupView.findViewById(R.id.all_rb);

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
            popupWindow.dismiss();
        } catch (Exception e) {

        }
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            popupWindow.dismiss();
        } catch (Exception e) {

        }

        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationReceiver, new IntentFilter("update-location"));

    }

}

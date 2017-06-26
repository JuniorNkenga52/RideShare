package com.app.rideshare.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.AutoCompleteLocationActivity;
import com.app.rideshare.activity.HomeActivity;
import com.app.rideshare.activity.NotificationActivity;
import com.app.rideshare.activity.RideShareApp;
import com.app.rideshare.activity.StartRideActivity;
import com.app.rideshare.activity.WaitingActivity;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.api.response.SendResponse;
import com.app.rideshare.listner.OnBackPressedListener;
import com.app.rideshare.model.Directions;
import com.app.rideshare.model.Rider;
import com.app.rideshare.model.Route;
import com.app.rideshare.model.SearchPlace;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MapDirectionAPI;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.view.CustomProgressDialog;
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
import java.util.Calendar;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements OnMapReadyCallback ,OnBackPressedListener {

    private GoogleMap mGoogleMap;
    private Marker curLocMarker;
    private Marker destinationLocationMarker;
    RideShareApp application;

    public static HomeFragment newInstance(ArrayList<Rider> mlist) {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();
        bundle.putSerializable("list", mlist);
        fragment.setArguments(bundle);

        return fragment;
    }

    ArrayList<Rider> mlist;
    ArrayList<Marker> mlistMarker;

    private int padding = 150;
    private LatLngBounds.Builder builder;
    private String mUserType = "";

    private static final int REQUEST_LOCATION = 11;

    private EditText mLocationSearchAtv;
    private ImageView mClearLocationIv;

    Typeface mRobotoReguler;
    LatLng currentlthg;
    LatLng destinationLatLang;
    private Polyline directionLine;

    CustomProgressDialog mProgressDialog;
    private TextView mSearchCabTv;

    User mUserBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, null);

        mProgressDialog = new CustomProgressDialog(getActivity());
        PrefUtils.initPreference(getActivity());
        mUserBean = PrefUtils.getUserInfo();
        HomeActivity.setOnBackPressedListener(this);

        application = (RideShareApp) getApplicationContext();
        mUserType = application.getmUserType();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        mlist = (ArrayList<Rider>) getArguments().getSerializable("list");
        mlistMarker = new ArrayList<>();

        builder = new LatLngBounds.Builder();

        mRobotoReguler = TypefaceUtils.getTypefaceRobotoMediam(getActivity());

        mLocationSearchAtv = (EditText) rootview.findViewById(R.id.location_search);
        mLocationSearchAtv.setTypeface(mRobotoReguler);

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
        mSearchCabTv.setTypeface(mRobotoReguler);
        if(mUserType.equals("2"))
        {
            mSearchCabTv.setText("Find Rider");
        }

        mSearchCabTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  if (mUserType.equals("1"))
                {
                    if (PrefUtils.getBoolean("isFriends"))
                    {
                        selectRide(mUserBean.getmUserId(), "1", "" + currentlthg.latitude, "" + currentlthg.longitude,"1");
                    } else {
                        selectRide(mUserBean.getmUserId(), "2", "" + currentlthg.latitude, "" + currentlthg.longitude,"1");
                    }

                }else{
                    if (PrefUtils.getBoolean("isFriends"))
                    {
                        selectRide(mUserBean.getmUserId(), "1", "" + currentlthg.latitude, "" + currentlthg.longitude,"2");
                    } else {
                        selectRide(mUserBean.getmUserId(), "2", "" + currentlthg.latitude, "" + currentlthg.longitude,"2");
                    }
                }
            }
        });


        return rootview;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        SmartLocation.with(getActivity()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        currentlthg = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("Bearing", "" + location.getBearing());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentlthg).zoom(16).build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        if (curLocMarker == null) {
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker())
                                    .position(currentlthg));
                        } else {
                            curLocMarker.setPosition(currentlthg);
                        }
                        builder.include(currentlthg);
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

                    if(mLocationSearchAtv.getText().toString().isEmpty())
                    {
                        ToastUtils.showShort(getActivity(),"Please select destination location.");
                    }else{
                        getRiderInfoDialog(selectedRide);
                    }

                }
                return false;
            }
        });
    }

    private void createMarker() {

        if (!mlist.isEmpty()) {

            for (Marker m : mlistMarker) {
                m.remove();
            }

            mlistMarker.clear();

            BitmapDescriptor icon = null;
            if (mUserType.equals("1")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin);
            } else if (mUserType.equals("2")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin);
            }

            for (Rider driver : mlist) {

                LatLng currentDriverPos = new LatLng(Double.parseDouble(driver.getmLatitude()), Double.parseDouble(driver.getmLongitude()));
                builder.include(currentDriverPos);

                Marker m = mGoogleMap.addMarker(new MarkerOptions().snippet(new Gson().toJson(driver))
                        .position(currentDriverPos).anchor(0.5f, 0.5f)
                        .rotation(0f)
                        .flat(true)
                        .icon(icon));

                mlistMarker.add(m);
            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cu);
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
            MapDirectionAPI.getDirection(currentlthg, destinationLatLang).enqueue(updateRouteCallback);
        }
    }

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

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

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void selectRide(String mId, String mType, String latitude, String longitude,String mRideType) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).getUser(mId, mType, latitude, longitude,mRideType).enqueue(new Callback<RideSelect>() {
            @Override
            public void onResponse(Call<RideSelect> call, Response<RideSelect> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mlist.clear();
                    mlist.addAll(response.body().getMlistUser());
                    builder=new LatLngBounds.Builder();
                    builder.include(currentlthg);
                    createMarker();
                } else {

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

        TextView mNameTv = (TextView) dialog.findViewById(R.id.name_tv);
        TextView mVahicalTv = (TextView) dialog.findViewById(R.id.vahical_tv);
        TextView mAddressTv = (TextView) dialog.findViewById(R.id.address_tv);
        mNameTv.setTypeface(mRobotoReguler);
        mVahicalTv.setTypeface(mRobotoReguler);
        mAddressTv.setTypeface(mRobotoReguler);

        try {
            mNameTv.setText(rider.getmFirstName());
            mAddressTv.setText(rider.getmAddress());
        } catch (Exception e) {
        }

        TextView mGetRideTv = (TextView) dialog.findViewById(R.id.get_ride_tv);
        TextView mCancelTv = (TextView) dialog.findViewById(R.id.cancel_ride_tv);
        if(mUserType.equals("2"))
        {
            mGetRideTv.setText("Offer Ride");
        }
        mGetRideTv.setTypeface(mRobotoReguler);
        mCancelTv.setTypeface(mRobotoReguler);

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
                SendRideRequest(rider.getnUserId(),mUserBean.getmUserId(),rider);
            }
        });

        dialog.show();
    }

    public void SendRideRequest(String userid, String fromuserid, final Rider rider)
    {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class).sendRequest(userid, fromuserid,""+currentlthg.latitude,""+currentlthg.longitude,""+destinationLatLang.latitude,""+destinationLatLang.longitude,mUserType,"","").enqueue(new Callback<SendResponse>() {
            @Override
            public void onResponse(Call<SendResponse> call, Response<SendResponse> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    if(response.body().getmStatus().equals("success"))
                    {
                        Intent i=new Intent(getActivity(), WaitingActivity.class);
                        i.putExtra("rider",rider);
                        i.putExtra("rider_data",response.body().getMlist().get(0));
                        startActivity(i);
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

    @Override
    public void doBack()
    {
        getActivity().finish();
    }


}

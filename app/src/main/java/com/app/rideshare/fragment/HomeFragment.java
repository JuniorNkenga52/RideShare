package com.app.rideshare.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.rideshare.R;
import com.app.rideshare.activity.RideShareApp;
import com.app.rideshare.model.Rider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private Marker curLocMarker;
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
    private String mUserType="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, null);

        application = (RideShareApp) getApplicationContext();
        mUserType=application.getmUserType();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        mlist = (ArrayList<Rider>) getArguments().getSerializable("list");
        mlistMarker = new ArrayList<>();

        builder = new LatLngBounds.Builder();

        Log.d("size", "" + mlist.size());

        return rootview;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        SmartLocation.with(getActivity()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("Bearing", "" + location.getBearing());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                        //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        createMarker();
                        if (curLocMarker == null) {
                            curLocMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker())
                                    .position(loc).title("You are here!!"));
                        } else {
                            curLocMarker.setPosition(loc);
                        }
                        builder.include(loc);
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
    }

    private void createMarker() {

        if (!mlist.isEmpty()) {

            for (Marker m : mlistMarker) {
                m.remove();
            }

            mlistMarker.clear();

            BitmapDescriptor icon=null;
            if(mUserType.equals("1"))
            {
                icon=BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin);
            }else if(mUserType.equals("2")){
                icon=BitmapDescriptorFactory.fromResource(R.drawable.ic_user_pin);
            }

            for (Rider driver : mlist) {

                LatLng currentDriverPos = new LatLng(Double.parseDouble(driver.getmLatitude()), Double.parseDouble(driver.getmLongitude()));
                builder.include(currentDriverPos);

                Marker m = mGoogleMap.addMarker(new MarkerOptions()
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
}

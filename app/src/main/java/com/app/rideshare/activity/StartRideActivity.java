package com.app.rideshare.activity;

import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.response.AcceptRider;
import com.app.rideshare.model.Directions;
import com.app.rideshare.model.Route;
import com.app.rideshare.utils.MapDirectionAPI;
import com.app.rideshare.utils.TypefaceUtils;
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

import java.io.IOException;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class StartRideActivity extends AppCompatActivity implements OnMapReadyCallback
{
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

    Typeface mRobotoMedium;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_ride_layout);

        mNameTv=(TextView)findViewById(R.id.name_tv);
        mEmailTv=(TextView)findViewById(R.id.email_tv);

        mRobotoMedium= TypefaceUtils.getTypefaceRobotoMediam(this);
        mNameTv.setTypeface(mRobotoMedium);
        mEmailTv.setTypeface(mRobotoMedium);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        try
        {
            mRider=(AcceptRider)getIntent().getExtras().getSerializable("rideobj");
             pickuplocation=new LatLng(Double.parseDouble(mRider.getStart_lati()),Double.parseDouble(mRider.getStart_long()));
                droppfflocation=new LatLng(Double.parseDouble(mRider.getEnd_lati()),Double.parseDouble(mRider.getEnd_long()));
            requestRoute(pickuplocation,droppfflocation);

        }
        catch (Exception e){
            Log.d("Error",e.toString());
        }

    }
    private void requestRoute(LatLng picklng,LatLng droplng)
    {
        if (picklng != null && droplng != null) {
            MapDirectionAPI.getDirection(picklng, droplng).enqueue(updateRouteCallback);
        }
    }
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d("Error",e.toString());
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
                    List<Route> routes = directions.parse(json);
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

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        SmartLocation.with(this).location()
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
}

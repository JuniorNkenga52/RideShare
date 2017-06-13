package com.app.rideshare.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideTypeActivity extends AppCompatActivity {


    RadioButton mNeedRideRb;
    RadioButton mOfferRideRb;

    Typeface mRobotoMediam;

    TextView mNextTv;
    Location currentLocation;

    CustomProgressDialog mProgressDialog;
    User mUserBean;
    RideShareApp application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_type);

        application = (RideShareApp) getApplicationContext();

        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();

        mProgressDialog = new CustomProgressDialog(this);

        mRobotoMediam = TypefaceUtils.getTypefaceRobotoMediam(this);

        mNeedRideRb = (RadioButton) findViewById(R.id.need_ride_rb);
        mOfferRideRb = (RadioButton) findViewById(R.id.offer_ride_rb);

        mNeedRideRb.setTypeface(mRobotoMediam);
        mOfferRideRb.setTypeface(mRobotoMediam);

        mNextTv = (TextView) findViewById(R.id.next_tv);
        mNextTv.setTypeface(mRobotoMediam);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select Ride");

        mNextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNeedRideRb.isChecked()) {
                    application.setmUserType("1");
                    selectRide(mUserBean.getmUserId(), "1", "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
                } else if (mOfferRideRb.isChecked()) {
                    application.setmUserType("2");
                    selectRide(mUserBean.getmUserId(), "2", "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
                }
            }
        });

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

    }

    private void selectRide(String mId, String mType, String latitude, String longitude) {

        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).selectRide(mId, mType, latitude, longitude).enqueue(new Callback<RideSelect>() {
            @Override
            public void onResponse(Call<RideSelect> call, Response<RideSelect> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Intent i = new Intent(RideTypeActivity.this, HomeActivity.class);
                    i.putExtra("list", response.body().getMlistUser());
                    startActivity(i);
                    finish();


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

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            SmartLocation.with(RideTypeActivity.this).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            currentLocation = location;
                        }
                    });
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(RideTypeActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

}

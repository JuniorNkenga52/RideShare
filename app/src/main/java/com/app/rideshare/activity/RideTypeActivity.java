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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.request.ContactRequest;
import com.app.rideshare.api.response.ContactResponse;
import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
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

    LinearLayout mNeedRideLL;
    LinearLayout mOfferRideLL;

    int rideType = 0;

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


        mNeedRideLL = (LinearLayout) findViewById(R.id.need_ride_ll);
        mOfferRideLL = (LinearLayout) findViewById(R.id.offer_ride_ll);


        mNeedRideLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(currentLocation!=null)
                {
                    mNeedRideLL.setSelected(true);
                    mOfferRideLL.setSelected(false);
                    rideType = 1;
                    application.setmUserType(""+rideType);
                    selectRide(mUserBean.getmUserId(), "" + rideType, "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
                }else{
                    ToastUtils.showShort(RideTypeActivity.this,"Getting your location.");
                }

            }
        });
        mOfferRideLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(currentLocation!=null)
                {
                    mOfferRideLL.setSelected(true);
                    mNeedRideLL.setSelected(false);
                    rideType = 2;
                    application.setmUserType(""+rideType);
                    selectRide(mUserBean.getmUserId(), "" + rideType, "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
                }else{
                    ToastUtils.showShort(RideTypeActivity.this,"Getting your location.");
                }
            }
        });

        mNextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rideType == 0) {
                    ToastUtils.showShort(RideTypeActivity.this, "Please Select Ride type.");
                } else {
                    application.setmUserType(""+rideType);

                }
            }
        });

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS)
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

            if (mUserBean.getContact_sync().equals("0")) {
                syncContact();
            }


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


    private void syncContact() {

        mProgressDialog.show();

        final ContactRequest request = new ContactRequest();
        request.setUser_id(mUserBean.getmUserId());
        request.setContact(AppUtils.readContacts(RideTypeActivity.this));

        ApiServiceModule.createService(RestApiInterface.class).syncContact(request).enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getStatus().equals("success")) {

                        mUserBean.setContact_sync("1");
                        PrefUtils.addUserInfo(mUserBean);

                        ToastUtils.showShort(RideTypeActivity.this, "Contact Sync");
                    } else {
                        ToastUtils.showShort(RideTypeActivity.this, "Contact Sync failed");
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

}

package com.app.rideshare.activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.request.ContactRequest;
import com.app.rideshare.api.response.AcceptRider;
import com.app.rideshare.api.response.ContactResponse;
import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.api.response.StartRideResponse;
import com.app.rideshare.model.InProgressRide;
import com.app.rideshare.model.User;
import com.app.rideshare.service.LocationService;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideTypeActivity extends AppCompatActivity {


    RadioButton mNeedRideRb;
    RadioButton mOfferRideRb;

    //Typeface mRobotoMediam;
    Context context;
    Activity activity;
    TextView mNextTv;
    Location currentLocation;

    CustomProgressDialog mProgressDialog;
    User mUserBean;
    RideShareApp application;

    LinearLayout mNeedRideLL;
    LinearLayout mOfferRideLL;

    int rideType = 0;
    boolean GpsStatus = false;
    MaterialDialog mMaterialDialog;
    InProgressRide mRide;
    private static long back_pressed;

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            if (mUserBean.getContact_sync().equals("0")) {
                if (AppUtils.isInternetAvailable(activity)) {
                    syncContact();
                } else {
                    MessageUtils.showNoInternetAvailable(activity);
                }

            }
            Intent intent = new Intent(RideTypeActivity.this, LocationService.class);
            startService(intent);
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
            MessageUtils.showFailureMessage(RideTypeActivity.this, "Permission Denied\n" + deniedPermissions.toString());
        }
    };
    private String InprogressRide = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_type);

        PrefUtils.initPreference(this);
        context = this;
        activity = this;
        application = (RideShareApp) getApplicationContext();
        turnGPSOn();

        if (getIntent().hasExtra("inprogress")) {
            InprogressRide = getIntent().getExtras().getString("inprogress");
        }
        if (InprogressRide.equals("busy")) {
            mRide = (InProgressRide) getIntent().getExtras().getSerializable("rideprogress");
            mMaterialDialog = new MaterialDialog(this)
                    .setTitle("Warning")
                    .setMessage("You have currently 1 Ride active.Are you want to continue?")
                    .setPositiveButton("Yes i'm in", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();

                            AcceptRider ride = new AcceptRider();
                            ride.setToRider(mRide.getmToRider());
                            ride.setFromRider(mRide.getmFromRider());
                            ride.setEnd_lati(mRide.getmEndLat());
                            ride.setEnd_long(mRide.getmEndLang());
                            ride.setEnding_address(mRide.getmEndingAddress());
                            ride.setStarting_address(mRide.getmStartingAddress());
                            ride.setRide_id(mRide.getmRideId());
                            ride.setU_ride_type(mRide.getmRideType());
                            ride.setStart_lati(mRide.getmStartLat());
                            ride.setStart_long(mRide.getmStartLang());
                            ride.setRequest_status(mRide.getmRequestStatus());

                            if (mRide.getmToRider().getnUserId().equals(PrefUtils.getUserInfo().getmUserId())) {
                                application.setmUserType("" + mRide.getmToRider().getU_ride_type());
                            } else {
                                application.setmUserType("" + mRide.getmFromRider().getU_ride_type());
                            }

                            Intent i = new Intent(RideTypeActivity.this, StartRideActivity.class);
                            i.putExtra("rideobj", ride);
                            startActivity(i);

                        }
                    })
                    .setNegativeButton("no Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String Userid = getIntent().getStringExtra("rideUserID");
                            startRide(mRide.getmRideId(), "4", Userid);
                            mMaterialDialog.dismiss();

                        }
                    });

            mMaterialDialog.show();
        }


        mUserBean = PrefUtils.getUserInfo();

        mProgressDialog = new CustomProgressDialog(this);

        //mRobotoMediam = TypefaceUtils.getTypefaceRobotoMediam(this);

        mNeedRideRb = (RadioButton) findViewById(R.id.need_ride_rb);
        mOfferRideRb = (RadioButton) findViewById(R.id.offer_ride_rb);

       /* mNeedRideRb.setTypeface(mRobotoMediam);
        mOfferRideRb.setTypeface(mRobotoMediam);*/

        mNextTv = (TextView) findViewById(R.id.next_tv);
        //mNextTv.setTypeface(mRobotoMediam);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select Ride");

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }

        mNeedRideLL = (LinearLayout) findViewById(R.id.need_ride_ll);
        mOfferRideLL = (LinearLayout) findViewById(R.id.offer_ride_ll);


        mNeedRideLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GpsStatus) {
                    turnGPSOn();
                } else {
                    if (currentLocation != null) {
                        mNeedRideLL.setSelected(true);
                        mOfferRideLL.setSelected(false);
                        rideType = 1;
                        application.setmUserType("" + rideType);

                        if (AppUtils.isInternetAvailable(activity)) {
                            selectRide(mUserBean.getmUserId(), "" + rideType, "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
                        } else {
                            MessageUtils.showNoInternetAvailable(activity);
                        }

                    } else {
                        MessageUtils.showWarningMessage(RideTypeActivity.this, "Getting your location.");
                    }
                }

            }
        });
        mOfferRideLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GpsStatus) {
                    turnGPSOn();
                } else {
                    if (currentLocation != null) {
                        mOfferRideLL.setSelected(true);
                        mNeedRideLL.setSelected(false);
                        rideType = 2;
                        application.setmUserType("" + rideType);
                        if (AppUtils.isInternetAvailable(activity)) {
                            selectRide(mUserBean.getmUserId(), "" + rideType, "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
                        } else {
                            MessageUtils.showNoInternetAvailable(activity);
                        }
                    } else {
                        MessageUtils.showWarningMessage(RideTypeActivity.this, "Getting your location.");
                    }
                }
            }
        });

        mNextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rideType == 0) {
                    MessageUtils.showFailureMessage(RideTypeActivity.this, "Please Select Ride type.");
                } else {
                    application.setmUserType("" + rideType);
                }
            }
        });

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS)
                .check();


    }

    public void turnGPSOn() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GpsStatus) {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(RideTypeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(RideTypeActivity.this);
            }
            builder.setCancelable(false);
            builder.setTitle("Alert")
                    .setMessage("Please Enable GPS.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void selectRide(String mId, String mType, String latitude, String longitude) {

        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).selectRide(mId, mType, latitude, longitude).enqueue(new Callback<RideSelect>() {
            @Override
            public void onResponse(Call<RideSelect> call, Response<RideSelect> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Intent i = new Intent(RideTypeActivity.this, HomeNewActivity.class);
                    i.putExtra("list", response.body().getMlistUser());
                    startActivity(i);
                    //  finish();

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

                        MessageUtils.showSuccessMessage(RideTypeActivity.this, "Contact Sync");
                    } else {
                        MessageUtils.showFailureMessage(RideTypeActivity.this, "Contact Sync failed");
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

    private void startRide(String mId, final String mType, String userid) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).mStartRide(mId, mType, userid).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mType.equals("3")) {

                    } else if (mType.equals("4")) {


                        Intent intent = new Intent(RideTypeActivity.this, LocationService.class);
                        stopService(intent);
                        //finish();
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
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();

            RideShareApp.mHomeTabPos = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            FragmentManager fm = getFragmentManager(); // or 'getSupportFragmentManager();'
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStack();
            }
            MessageUtils.showFailureMessage(getBaseContext(), "Press once again to exit!");
            back_pressed = System.currentTimeMillis();
        }
        //super.onBackPressed();
    }
}

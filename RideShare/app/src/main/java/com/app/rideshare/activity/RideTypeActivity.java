package com.app.rideshare.activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.app.rideshare.service.LocationProvider;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideTypeActivity extends AppCompatActivity implements LocationProvider.LocationCallback {


    RadioButton mNeedRideRb;
    RadioButton mOfferRideRb;

    //Typeface mRobotoMediam;
    Context context;
    Activity activity;
    TextView mNextTv;
    //Location currentLocation;

    CustomProgressDialog mProgressDialog;
    User mUserBean;
    RideShareApp application;

    LinearLayout mNeedRideLL;
    LinearLayout mOfferRideLL;

    int rideType = 0;
    //boolean GpsStatus = false;
    MaterialDialog mMaterialDialog;
    InProgressRide mRide;
    private static long back_pressed;


    LocationProvider mLocationProvider;

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

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context,new Intent(context, LocationService.class));
            } else {
                context.startService(new Intent(context, LocationService.class));
            }*/
            //startService(new Intent(getBaseContext(), LocationService.class));
            /*Intent intent = new Intent(RideTypeActivity.this, LocationService.class);
            startService(intent);*/
            /*SmartLocation.with(RideTypeActivity.this).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            currentLocation = location;
                        }
                    });*/
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
        //turnGPSOn();

        mLocationProvider = new LocationProvider(this, this);

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
                            if (getIntent().hasExtra("Is_driver")) {
                                i.putExtra("Is_driver", getIntent().getExtras().getString("Is_driver"));
                            }
                            startActivity(i);

                        }
                    })
                    .setNegativeButton("no Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String Userid = getIntent().getStringExtra("rideUserID");

                            application.setmUserType(mUserBean.getmRideType());
                            if (getIntent().hasExtra("Is_driver")) {
                                if (getIntent().getExtras().getString("Is_driver").equals("1")) {
                                    startRide(mRide.getmRideId(), "1", "4", Userid);
                                } else {
                                    startRide(mRide.getmRideId(), "0", "4", Userid);
                                }
                            } else {
                                startRide(mRide.getmRideId(), "0", "4", Userid);
                            }

                            mMaterialDialog.dismiss();

                        }
                    });

            mMaterialDialog.show();
        }


        mUserBean = PrefUtils.getUserInfo();

        mProgressDialog = new CustomProgressDialog(this);

        mNeedRideRb = findViewById(R.id.need_ride_rb);
        mOfferRideRb = findViewById(R.id.offer_ride_rb);

        mNextTv = findViewById(R.id.next_tv);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        mNeedRideLL = findViewById(R.id.need_ride_ll);
        mOfferRideLL = findViewById(R.id.offer_ride_ll);

        mNeedRideLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RideShareApp.mLocation != null) {
                    mNeedRideLL.setSelected(true);
                    mOfferRideLL.setSelected(false);
                    rideType = 1;

                    mUserBean.setmRideType("1");
                    PrefUtils.addUserInfo(mUserBean);

                    application.setmUserType("" + rideType);

                    if (AppUtils.isInternetAvailable(activity)) {
                        selectRide(mUserBean.getmUserId(), "" + rideType, "" +
                                RideShareApp.mLocation.getLatitude(), "" +
                                RideShareApp.mLocation.getLongitude());
                    } else {
                        MessageUtils.showNoInternetAvailable(activity);
                    }

                } else {
                    MessageUtils.showWarningMessage(RideTypeActivity.this, "Getting your location.");
                }


            }
        });
        mOfferRideLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (RideShareApp.mLocation != null) {
                    mOfferRideLL.setSelected(true);
                    mNeedRideLL.setSelected(false);
                    rideType = 2;

                    mUserBean.setmRideType("2");
                    PrefUtils.addUserInfo(mUserBean);

                    application.setmUserType("" + rideType);
                    if (AppUtils.isInternetAvailable(activity)) {
                        selectRide(mUserBean.getmUserId(), "" + rideType, "" +
                                RideShareApp.mLocation.getLatitude(), "" +
                                RideShareApp.mLocation.getLongitude());
                    } else {
                        MessageUtils.showNoInternetAvailable(activity);
                    }
                } else {
                    MessageUtils.showWarningMessage(RideTypeActivity.this, "Getting your location.");
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
                .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                        "Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS)
                .check();


    }

    protected boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            int locationMode = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.LOCATION_MODE,
                    0
            );

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {

            String locationProviders = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            );


            return !TextUtils.isEmpty(locationProviders);
        }
    }


    private void selectRide(String mId, String mType, String latitude, String longitude) {

        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).selectRide(mId, mType, latitude, longitude).enqueue(new Callback<RideSelect>() {
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

        ApiServiceModule.createService(RestApiInterface.class, context).syncContact(request).enqueue(new Callback<ContactResponse>() {
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

    private void startRide(String mId, String check_driver, final String mType, String userid) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver, mType, userid, "" + mRide.getmEndLat(), "" + mRide.getmEndLang()).enqueue(new Callback<StartRideResponse>() {
            @Override
            public void onResponse(Call<StartRideResponse> call, Response<StartRideResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mType.equals("3")) {

                    } else if (mType.equals("4")) {
                        if (getIntent().getExtras().getString("Is_driver").equals("1")) {

                        } else {
                            MessageUtils.showSuccessMessage(RideTypeActivity.this, "Ride Finished");
                            Intent rateride = new Intent(RideTypeActivity.this, RideRateActivity.class);
                            rateride.putExtra("riderate", mRide.getmRideId());
                            rateride.putExtra("driverid", mRide.getmFromRider().getnUserId());
                            startActivity(rateride);
                        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {

            RideShareApp.mHomeTabPos = 0;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAndRemoveTask();
            finishAffinity();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(RideTypeActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("request_status"));

        LocalBroadcastManager.getInstance(RideTypeActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("start_ride"));

        mLocationProvider.connect(activity);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String staus = intent.getStringExtra("int_data");
                if (staus.equals("2")) {
                    try {
                        if (mUserBean.getmRideType().equals("1")) {
                            MessageUtils.showSuccessMessage(RideTypeActivity.this, "Ride Finished");
                            Intent rateride = new Intent(RideTypeActivity.this, RideRateActivity.class);
                            rateride.putExtra("riderate", mRide.getmRideId());
                            rateride.putExtra("driverid", mRide.getmFromRider().getnUserId());
                            startActivity(rateride);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public void handleNewLocation(Location location) {
        //currentLocation = location;
    }

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //currentLocation = RideShareApp.mLocation;
        }
    };


}
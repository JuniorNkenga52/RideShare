package com.app.rideWhiz.activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.rideWhiz.BuildConfig;
import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.request.ContactRequest;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.api.response.ContactResponse;
import com.app.rideWhiz.api.response.RideSelect;
import com.app.rideWhiz.api.response.StartRideResponse;
import com.app.rideWhiz.api.response.UpdateDestinationAddress;
import com.app.rideWhiz.fragment.ExploreFragment;
import com.app.rideWhiz.fragment.MessagesFragment;
import com.app.rideWhiz.fragment.NotificationFragment;
import com.app.rideWhiz.fragment.ProfileFragment;
import com.app.rideWhiz.model.ContactBean;
import com.app.rideWhiz.model.InProgressRide;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.service.LocationProvider;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RideTypeActivity extends AppCompatActivity implements LocationProvider.LocationCallback, BottomNavigationView.OnNavigationItemSelectedListener {


    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    private static long back_pressed;
    public BottomNavigationView bottomNavigationView;
    RadioButton mNeedRideRb;
    RadioButton mOfferRideRb;
    //Location currentLocation;
    //Typeface mRobotoMediam;
    Context context;
    Activity activity;
    TextView mNextTv;
    CustomProgressDialog mProgressDialog;
    User mUserBean;
    RideShareApp application;
    LinearLayout mNeedRideLL;
    LinearLayout mOfferRideLL;
    int rideType = 0;
    //boolean GpsStatus = false;
    MaterialDialog mMaterialDialog;
    InProgressRide mRide;
    LocationProvider mLocationProvider;
    FrameLayout frame_layout;
    LinearLayout layout_select_type;
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            if (mUserBean.getContact_sync() == null || mUserBean.getContact_sync().equals("0")) {
                if (AppUtils.isInternetAvailable(activity)) {
                    syncContact();
                } else {
                    MessageUtils.showNoInternetAvailable(activity);
                }

            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            MessageUtils.showFailureMessage(RideTypeActivity.this, "Permission Denied\n" + deniedPermissions.toString());
        }
    };
    private String InprogressRide = "";
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_type);

        PrefUtils.initPreference(this);
        context = this;
        activity = this;
        application = (RideShareApp) getApplicationContext();

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

                            final AcceptRider ride = new AcceptRider();
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
                                application.setmUserType("" + mRide.getmToRider().getmType());
                            } else {
                                application.setmUserType("" + mRide.getmFromRider().getU_ride_type());
                            }

                            if (application.mWebSocketSendRequest == null) {
                                application.connectRideRequest();
                            } else if (application.mWebSocketSendRequest.isClosed() || application.mWebSocketSendRequest.isClosing()) {
                                application.connectRideRequest();
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
                                    startRide(mRide.getmRideId(), "1", "4", Userid, "2");
                                } else {
                                    startRide(mRide.getmRideId(), "0", "4", Userid, "1");
                                }
                            } else {
                                startRide(mRide.getmRideId(), "0", "4", Userid, "1");
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

        frame_layout = findViewById(R.id.frame_layout);
        layout_select_type = findViewById(R.id.layout_select_type);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (PrefUtils.getString("isBlank").equals("true")) {
            if (RideShareApp.mRideTypeTabPos == 4) {
                RideShareApp.mRideTypeTabPos = 4;
            } else {
                RideShareApp.mRideTypeTabPos = 1;
            }
        }
        if (RideShareApp.mRideTypeTabPos == 1)
            bottomNavigationView.setSelectedItemId(R.id.action_item1);
        else if (RideShareApp.mRideTypeTabPos == 2)
            bottomNavigationView.setSelectedItemId(R.id.action_item2);
        else if (RideShareApp.mRideTypeTabPos == 3)
            bottomNavigationView.setSelectedItemId(R.id.action_item3);
        else if (RideShareApp.mRideTypeTabPos == 4)
            bottomNavigationView.setSelectedItemId(R.id.action_item4);

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

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                        "Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS)
                .check();

        updateDestinationAddress(mUserBean.getmUserId(), "");
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
                    RideShareApp.mHomeTabPos = 0;
                    Intent i = new Intent(RideTypeActivity.this, HomeNewActivity.class);
                    i.putExtra("list", response.body().getMlistUser());
                    startActivity(i);
                    //  finish();

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
        final ArrayList<ContactBean> mlist = new ArrayList<>();
        ContentResolver cr = getContentResolver();
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

    private void startRide(String mId, String check_driver, final String mType, String userid, String user_ride_type) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).mStartRide(mId, check_driver, mType, userid, "" + mRide.getmEndLat(), "" + mRide.getmEndLang(), user_ride_type).enqueue(new Callback<StartRideResponse>() {
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

            RideShareApp.mRideTypeTabPos = 0;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAndRemoveTask();
            finishAffinity();
            finish();
            System.exit(0);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                RideShareApp.mRideTypeTabPos = 0;
                startActivity(new Intent(context, RideTypeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(RideTypeActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("request_status"));

        LocalBroadcastManager.getInstance(RideTypeActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("start_ride"));

        mLocationProvider.connect(activity);
    }

    public void handleNewLocation(Location location) {
        //currentLocation = location;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.action_home:
                RideShareApp.mRideTypeTabPos = 0;
                startActivity(new Intent(context, RideTypeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //finish();
                break;
            case R.id.action_item1:
                RideShareApp.mRideTypeTabPos = 1;
                if (PrefUtils.getString("isBlank").equals("true")) {
                    selectedFragment = GroupSelectionFragment.newInstance();
                } else {
                    selectedFragment = ExploreFragment.newInstance();
                }
                break;
            case R.id.action_item2:
                RideShareApp.mRideTypeTabPos = 2;
                selectedFragment = MessagesFragment.newInstance();
                break;
            case R.id.action_item3:
                RideShareApp.mRideTypeTabPos = 3;
                selectedFragment = NotificationFragment.newInstance();
                break;
            case R.id.action_item4:
                RideShareApp.mRideTypeTabPos = 4;
                selectedFragment = ProfileFragment.newInstance();
                break;
        }
        layout_select_type.setVisibility(View.GONE);
        frame_layout.setVisibility(View.VISIBLE);
        if (RideShareApp.mRideTypeTabPos != 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return true;
    }

    private void updateDestinationAddress(String userid, final String destination_address) {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).updateDestinationAddress(userid, destination_address).enqueue(new Callback<UpdateDestinationAddress>() {
            @Override
            public void onResponse(Call<UpdateDestinationAddress> call, Response<UpdateDestinationAddress> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Successes :: >> ", response.body().toString());
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<UpdateDestinationAddress> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }
}
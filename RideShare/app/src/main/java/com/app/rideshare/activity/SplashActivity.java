package com.app.rideshare.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.rideshare.BuildConfig;
import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.notification.GCMRegistrationIntentService;
import com.app.rideshare.service.LocationService;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Activity activity;
    public static String token = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Location currentLocation;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        activity = this;
        context = this;
        TextView textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        textViewVersion.setText("Version " + BuildConfig.VERSION_NAME);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    token = intent.getStringExtra("token");
                    PrefUtils.putString("TokenID", token);
                    Log.d("token", token);

                    if (PrefUtils.getString("loginwith").equals("")) {

                        new TedPermission(context)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                                        "Please turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                                .check();

                    }
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {

                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }
        } else {
            startService(new Intent(this, GCMRegistrationIntentService.class));
        }

        PrefUtils.initPreference(this);

        RideShareApp.mHomeTabPos = 0;

        if (!PrefUtils.getBoolean("sortcut")) {
            addShortcut();
            PrefUtils.putBoolean("sortcut", true);
        }

        if (!PrefUtils.getString("loginwith").equals("")) {
            if (PrefUtils.getUserInfo() != null)
                getUserDetails(PrefUtils.getUserInfo().getmUserId());
        }
    }

    private void addShortcut() {
        Intent shortcutIntent = new Intent(getApplicationContext(),
                SplashActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "RideShare");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("Splash", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("Splash", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void getUserDetails(final String userId) {
        ApiServiceModule.createService(RestApiInterface.class, context).getUserDetails(userId).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isAll", true);
                        PrefUtils.putString("loginwith", "normal");

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            if (response.body().getMlist().get(0).getM_is_assigned_group().equals("1") ) {
                                //response.body().getMlist().get(0).getmRidestatus();
                                PrefUtils.putString("isBlank", "false");
                                Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                                if (response.body().getMlist().get(0).getmRidestatus().equals("busy")&&  response.body().getmProgressRide().size()>0) {
                                    i.putExtra("inprogress", "busy");
                                    i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                                    i.putExtra("rideUserID", response.body().getMlist().get(0).getmUserId());
                                    i.putExtra("Is_driver", response.body().getMlist().get(0).getmIs_driver());
                                }
                                startActivity(i);
                                finish();

                            } else {
                                PrefUtils.putString("isBlank", "true");
                                Intent i = new Intent(getBaseContext(), HomeNewActivity.class);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();

                                startService(new Intent(getBaseContext(), LocationService.class));

                                SmartLocation.with(getBaseContext()).location()
                                        .oneFix()
                                        .start(new OnLocationUpdatedListener() {
                                            @Override
                                            public void onLocationUpdated(Location location) {
                                                currentLocation = location;
                                            }
                                        });
                            }
                        }
                    } else {
                        MessageUtils.showFailureMessage(SplashActivity.this, response.body().getmMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if (PrefUtils.getBoolean("islogin")) {
                Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                i.putExtra("MyUserID", PrefUtils.getUserInfo().getmUserId());
                startActivity(i);
                finish();

            } else {
                Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };
}

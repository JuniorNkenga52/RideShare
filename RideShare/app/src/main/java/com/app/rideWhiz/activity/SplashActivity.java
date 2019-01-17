package com.app.rideWhiz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.rideWhiz.BuildConfig;
import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.SignupResponse;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Activity activity;
    public static String token = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //Location currentLocation;
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

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                PrefUtils.putString("TokenID", newToken);
                if (PrefUtils.getString("loginwith").equals("")) {
                    new TedPermission(context)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                                    "Please turn on permissions at [Setting] > [Permission]")
                            .setPermissions(Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                            .check();
                }
            }
        });

        /*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }
        } else {
            startService(new Intent(this, MyFirebaseMessagingService.class));
        }*/

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
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "RideWhiz");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            if (response.body().getMlist().get(0).getM_is_assigned_group().equals("1")) {
                                //response.body().getMlist().get(0).getmRidestatus();
                                PrefUtils.putString("isBlank", "false");
                                Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                                if (response.body().getMlist().get(0).getmRidestatus().equals("busy") && response.body().getmProgressRide().size() > 0) {
                                    i.putExtra("inprogress", "busy");
                                    i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                                    i.putExtra("rideUserID", response.body().getMlist().get(0).getmUserId());
                                    i.putExtra("Is_driver", response.body().getMlist().get(0).getmIs_driver());
                                }
                                startActivity(i);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            } else {
                                PrefUtils.putString("isBlank", "true");
                                Intent i = new Intent(getBaseContext(), HomeNewActivity.class);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();


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
                Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };
}

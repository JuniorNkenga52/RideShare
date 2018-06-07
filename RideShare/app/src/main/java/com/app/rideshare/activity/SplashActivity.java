package com.app.rideshare.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideshare.BuildConfig;
import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.notification.GCMRegistrationIntentService;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static String token_splash;
    Activity activity;
    public static String token = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        activity = this;
        TextView textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        textViewVersion.setText("Version " + BuildConfig.VERSION_NAME);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    token = intent.getStringExtra("token");
                    PrefUtils.putString("TokenID",token);
                    Log.d("token", token);

                    if (PrefUtils.getString("loginwith").equals("")) {

                        new TedPermission(context)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.READ_PHONE_STATE)
                                .check();

                    }
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                } else {
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
        /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    token_splash = intent.getStringExtra("token");

                    Log.d("token", token_splash);
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                } else {
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }*/

        PrefUtils.initPreference(this);

        RideShareApp.mHomeTabPos = 0;

        if (!PrefUtils.getBoolean("sortcut")) {
            addShortcut();
            PrefUtils.putBoolean("sortcut", true);
        }

        if (PrefUtils.getString("loginwith").equals("")) {
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (PrefUtils.getBoolean("islogin")) {



                        Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();

                    } else {
                        Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }
            }, 3000);*/
        } else {
            /*if (PrefUtils.getString("loginwith").equals("google")) {
                logingoogle(PrefUtils.getString("gId"), PrefUtils.getString("gemail"), PrefUtils.getString("gfname"), PrefUtils.getString("glast"));
            } else if (PrefUtils.getString("loginwith").equals("facebook")) {
                loginfacebookuser(PrefUtils.getString("gId"), PrefUtils.getString("gemail"), PrefUtils.getString("gfname"), PrefUtils.getString("glast"));
            } else if (PrefUtils.getString("loginwith").equals("normal")) {
                loginuser(PrefUtils.getString("gemail"), PrefUtils.getString("gId"),PrefUtils.getString("group_id"));
            }*/
            //PrefUtils.getUserInfo().getmUserId();
            getUserDetails(PrefUtils.getUserInfo().getmUserId());
                /*Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();*/

        }
    }

    private void addShortcut() {

        Intent shortcutIntent = new Intent(getApplicationContext(),
                SplashActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "RideShare");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        getApplicationContext().sendBroadcast(addIntent);
    }


    private void loginuser(final String mEmail, final String password, final String group_id) {
        ApiServiceModule.createService(RestApiInterface.class).login(mEmail, password, token_splash, group_id).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isFriends", true);

                        PrefUtils.putString("loginwith", "normal");
                        PrefUtils.putString("gemail", mEmail);
                        PrefUtils.putString("gId", password);
                        PrefUtils.putString("group_id", group_id);

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            i.putExtra("inprogress", response.body().getMlist().get(0).getmRidestatus());
                            if (!response.body().getMlist().get(0).getmRidestatus().equals("free")) {
                                i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                            }
                            startActivity(i);
                            finish();
                        }
                    } else {
                        MessageUtils.showFailureMessage(SplashActivity.this, response.body().getmMessage());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    private void loginfacebookuser(final String mFacebookId, final String mEmail, final String mFirstName, final String mLastName) {
        ApiServiceModule.createService(RestApiInterface.class).signfacebook(mFacebookId, mEmail, mFirstName, mLastName, token_splash, "4").enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("isFriends", true);
                        PrefUtils.putBoolean("islogin", true);

                        PrefUtils.putString("loginwith", "facebook");
                        PrefUtils.putString("gemail", mEmail);
                        PrefUtils.putString("gId", mFacebookId);
                        PrefUtils.putString("gfname", mFirstName);
                        PrefUtils.putString("glast", mLastName);

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            i.putExtra("inprogress", response.body().getMlist().get(0).getmRidestatus());
                            if (!response.body().getMlist().get(0).getmRidestatus().equals("free")) {
                                i.putExtra("rideprogress", response.body().getmProgressRide().get(0));

                            }
                            startActivity(i);
                            finish();
                        }

                    } else {
                        MessageUtils.showFailureMessage(SplashActivity.this, response.body().getmMessage());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }


    private void logingoogle(final String mGoogleId, final String mEmail, final String mFirstName, final String mLastName) {

        ApiServiceModule.createService(RestApiInterface.class).signGoogleplus(mGoogleId, mEmail, mFirstName, mLastName, token_splash, "4").enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isFriends", true);

                        PrefUtils.putString("loginwith", "google");
                        PrefUtils.putString("gemail", mEmail);
                        PrefUtils.putString("gId", mGoogleId);
                        PrefUtils.putString("gfname", mFirstName);
                        PrefUtils.putString("glast", mLastName);

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            i.putExtra("inprogress", response.body().getMlist().get(0).getmRidestatus());
                            if (!response.body().getMlist().get(0).getmRidestatus().equals("free")) {
                                i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                            }
                            startActivity(i);
                            finish();
                        }
                    } else {
                        MessageUtils.showFailureMessage(SplashActivity.this, response.body().getmMessage());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();



        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
    private void getUserDetails(final String userId) {
        ApiServiceModule.createService(RestApiInterface.class).getUserDetails(userId).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isFriends", true);

                        PrefUtils.putString("loginwith", "normal");

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {

                            /*Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            i.putExtra("inprogress", response.body().getMlist().get(0).getmRidestatus());
                            if (!response.body().getMlist().get(0).getmRidestatus().equals("free")) {
                                i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                                i.putExtra("rideUserID", response.body().getMlist().get(0).getmUserId());
                            }
                            startActivity(i);
                            finish();*/

                            //New One
                            if (response.body().getMlist().get(0).getM_is_assigned_group().equals("1")) {

                                /*Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                                i.putExtra("inprogress", response.body().getMlist().get(0).getmRidestatus());
                                if (!response.body().getMlist().get(0).getmRidestatus().equals("free")) {
                                    i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                                    i.putExtra("rideUserID", response.body().getMlist().get(0).getmUserId());
                                }
                                startActivity(i);
                                finish();*/

                                //MyGroupSelectionActivity
                                /*if(!PrefUtils.getString("MyGroup").equals("Selected")){
                                    Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                                    startActivity(i);
                                    finish();
                                }else {
                                    Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                                    i.putExtra("inprogress", response.body().getMlist().get(0).getmRidestatus());
                                    if (!response.body().getMlist().get(0).getmRidestatus().equals("free")) {
                                        i.putExtra("rideprogress", response.body().getmProgressRide().get(0));
                                        i.putExtra("rideUserID", response.body().getMlist().get(0).getmUserId());
                                    }
                                    startActivity(i);
                                    finish();
                                }*/
                                /*String userID=PrefUtils.getString("MyID");
                                if(userID.equals("")){
                                    Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                                    i.putExtra("MyUserID",PrefUtils.getUserInfo().getmUserId());
                                    startActivity(i);
                                    finish();
                                }*/
                                Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                                startActivity(i);
                                finish();


                            } else {
                                Intent i = new Intent(getBaseContext(), GroupSelectionActivity.class);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        }
                    } else {
                        MessageUtils.showFailureMessage(SplashActivity.this, response.body().getmMessage());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){

            Toast.makeText(getApplicationContext(), "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }else {

        }
        ActivityCompat.requestPermissions(this, new String[]{permission},PERMISSION_REQUEST_CODE);
    }
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if (PrefUtils.getBoolean("islogin")) {
                Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                i.putExtra("MyUserID",PrefUtils.getUserInfo().getmUserId());
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

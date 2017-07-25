package com.app.rideshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.notification.GCMRegistrationIntentService;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    token = intent.getStringExtra("token");
                    Log.d("token", token);
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

        PrefUtils.initPreference(this);

        if (!PrefUtils.getBoolean("sortcut")) {
            addShortcut();
            PrefUtils.putBoolean("sortcut", true);
        }

        if (PrefUtils.getString("loginwith").equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (PrefUtils.getBoolean("islogin")) {

                        if (PrefUtils.getUserInfo().getmMobileNo() == null || PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } else {
                        Intent i = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 3000);
        } else {
            if (PrefUtils.getString("loginwith").equals("google")) {
                logingoogle(PrefUtils.getString("gId"), PrefUtils.getString("gemail"), PrefUtils.getString("gfname"), PrefUtils.getString("glast"));
            } else if (PrefUtils.getString("loginwith").equals("facebook")) {
                loginfacebookuser(PrefUtils.getString("gId"), PrefUtils.getString("gemail"), PrefUtils.getString("gfname"), PrefUtils.getString("glast"));
            } else if (PrefUtils.getString("loginwith").equals("normal")) {
                loginuser(PrefUtils.getString("gemail"), PrefUtils.getString("gId"));
            }
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


    private void loginuser(final String mEmail, final String password) {
        ApiServiceModule.createService(RestApiInterface.class).login(mEmail, password, token).enqueue(new Callback<SignupResponse>() {
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
                            i.putExtra("inprogress",response.body().getMlist().get(0).getmRidestatus());
                            if(!response.body().getMlist().get(0).getmRidestatus().equals("free")){
                                i.putExtra("rideprogress",response.body().getmProgressRide().get(0));
                            }
                            startActivity(i);
                            finish();
                        }
                    } else {
                        ToastUtils.showShort(SplashActivity.this, response.body().getmMessage());
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
        ApiServiceModule.createService(RestApiInterface.class).signfacebook(mFacebookId, mEmail, mFirstName, mLastName, token).enqueue(new Callback<SignupResponse>() {
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
                            i.putExtra("inprogress",response.body().getMlist().get(0).getmRidestatus());
                            if(!response.body().getMlist().get(0).getmRidestatus().equals("free")){
                                i.putExtra("rideprogress",response.body().getmProgressRide().get(0));

                            }
                            startActivity(i);
                            finish();
                        }

                    } else {
                        ToastUtils.showShort(SplashActivity.this, response.body().getmMessage());
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

        ApiServiceModule.createService(RestApiInterface.class).signGoogleplus(mGoogleId, mEmail, mFirstName, mLastName, token).enqueue(new Callback<SignupResponse>() {
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
                            i.putExtra("inprogress",response.body().getMlist().get(0).getmRidestatus());
                            if(!response.body().getMlist().get(0).getmRidestatus().equals("free")){
                                i.putExtra("rideprogress",response.body().getmProgressRide().get(0));
                            }
                            startActivity(i);
                            finish();
                        }
                    } else {
                        ToastUtils.showShort(SplashActivity.this, response.body().getmMessage());
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
}

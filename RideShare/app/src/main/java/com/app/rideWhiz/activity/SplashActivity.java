package com.app.rideWhiz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.rideWhiz.BuildConfig;
import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.SignupResponse;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static String token = "";
    Activity activity;
    //Location currentLocation;
    Context context;
    ArrayList<String> user_id_List;
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
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        activity = this;
        context = this;
        TextView textViewVersion = findViewById(R.id.textViewVersion);
        textViewVersion.setText("Version " + BuildConfig.VERSION_NAME);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                PrefUtils.putString("TokenID", newToken);
                if (PrefUtils.getString("loginwith").equals("")) {
                    TedPermission.with(context)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                                    "Please turn on permissions at [Setting] > [Permission]")
                            .setPermissions(Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                            .check();
                }
            }

        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(SplashActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("newToken", e.getMessage());
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
                        user_id_List = new ArrayList<>();
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isAll", true);
                        PrefUtils.putString("loginwith", "normal");
                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            if (response.body().getMlist().get(0).getM_is_assigned_group().equals("1")) {
                                String status;
                                if (response.body().getmProgressRide().size() > 0) {
                                    status = "0";
                                    for (int j = 0; j < response.body().getmProgressRide().size(); j++) {
                                        user_id_List.add(response.body().getmProgressRide().get(j).getmFromRider().getnUserId());
                                    }
                                } else {
                                    status = "1";
                                    user_id_List.add(response.body().getMlist().get(0).getmUserId());
                                }
                                updateUserStatus(user_id_List, status, userId, response);

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

    private void removeDriver(String userid) {
        Log.w("Calling", "Calling now...");
        ApiServiceModule.createService(RestApiInterface.class, this).removeDriverFromList(userid).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject resp = null;
                try {
                    if (response.body() != null) {
                        resp = new JSONObject(response.body().toString());
                        if (resp.optString("status").equals("success")) {
                            Log.w("Success", "Completed API is Called");
                            Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("Failure", "Error in API");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

    private void updateUserStatus(ArrayList<String> userid, String status, final String userId, final Response<SignupResponse> responses) {
        Log.w("Calling", "Calling now...");
        ApiServiceModule.createService(RestApiInterface.class, this).updateUserStatus(userid, status).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject resp = null;
                try {
                    if (response.body() != null) {
                        resp = new JSONObject(response.body().toString());
                        if (resp.optString("status").equals("success")) {
                            Log.w("Success", "Completed API is Called");
                            PrefUtils.putString("isBlank", "false");
                            Intent i = new Intent(getBaseContext(), MyGroupSelectionActivity.class);
                            if (responses.body().getmProgressRide().size() > 0) {

                                i.putExtra("inprogress", "busy");
                                i.putExtra("rideprogress", responses.body().getmProgressRide().get(0));
                                i.putExtra("rideUserID", responses.body().getMlist().get(0).getmUserId());
                                i.putExtra("Is_driver", responses.body().getMlist().get(0).getmIs_driver());
                                startActivity(i);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                removeDriver(userId);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("Failure", "Error in API");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }
}

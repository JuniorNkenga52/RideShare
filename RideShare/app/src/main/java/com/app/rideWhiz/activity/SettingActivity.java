package com.app.rideWhiz.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.chat.CommonMethods;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    ConstraintLayout clSignOut;
    ConstraintLayout clear_chats;
    User mUserBean;
    private String toJabberId;
    CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mUserBean = PrefUtils.getUserInfo();
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clSignOut = (ConstraintLayout) findViewById(R.id.clSignOut);
        clear_chats = findViewById(R.id.clear_chats);
        mProgressDialog = new CustomProgressDialog(SettingActivity.this);

        clear_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(getResources().getString(R.string.clearChats))
                        .setMessage(getResources().getString(R.string.clearChats_msg))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    toJabberId = Constants.intentKey.jabberPrefix + mUserBean.getmUserId();
                                    toJabberId = toJabberId.toLowerCase();
                                    CommonMethods commonMethods = new CommonMethods(getApplicationContext());
                                    commonMethods.resetDatabase();
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        clSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(getResources().getString(R.string.logout))
                        .setMessage(getResources().getString(R.string.logout_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    dialog.dismiss();

                                    removeDriver( mUserBean.getmUserId());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(RideShareApp.mHomeTabPos!=0){
            RideShareApp.mHomeTabPos = 4;
            Intent i = new Intent(SettingActivity.this, HomeNewActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else {
            RideShareApp.mRideTypeTabPos = 4;

            Intent i = new Intent(SettingActivity.this, RideTypeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    private void removeDriver(String userid) {
        Log.w("Calling", "Calling now...");
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, this).removeDriverFromList(userid).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject resp = null;
                try {
                    if (response.body() != null) {
                        resp = new JSONObject(response.body().toString());
                        if (resp.optString("status").equals("success")) {
                            Log.w("Success", "Completed API is Called");
                            PrefUtils.putBoolean("islogin", false);
                            PrefUtils.putString("loginwith", "");
                            //PrefUtils.putString("TokenID","");
                            RideShareApp.mHomeTabPos = 0;
                            mProgressDialog.cancel();
                            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("Failure", "Error in API");
                    mProgressDialog.cancel();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }
}

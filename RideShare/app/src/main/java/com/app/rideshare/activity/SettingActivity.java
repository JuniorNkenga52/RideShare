package com.app.rideshare.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.app.rideshare.R;
import com.app.rideshare.chat.CommonMethods;
import com.app.rideshare.model.Rider;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.Constants;
import com.app.rideshare.utils.PrefUtils;

public class SettingActivity extends AppCompatActivity {

    ConstraintLayout clSignOut;
    ConstraintLayout clear_chats;
    ConstraintLayout privacy_policy;

    User mUserBean;
    private String toJabberId;

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
        privacy_policy = findViewById(R.id.PrivacyPolicy);

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(getResources().getString(R.string.privacy_policy))
                        .setMessage(getResources().getString(R.string.privacy_policy))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                                    startActivity(browserIntent);
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
                                    PrefUtils.putBoolean("islogin", false);
                                    PrefUtils.putString("loginwith", "");
                                    //PrefUtils.putString("TokenID","");
                                    RideShareApp.mHomeTabPos = 0;

                                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
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
}

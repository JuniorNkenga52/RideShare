package com.app.rideshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.app.rideshare.R;
import com.app.rideshare.utils.PrefUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        PrefUtils.initPreference(this);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {

                if (PrefUtils.getBoolean("islogin")) {

                    if (PrefUtils.getUserInfo().getmMobileNo()==null ||PrefUtils.getUserInfo().getmMobileNo().equals("") )
                    {
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
    }

}

package com.app.rideshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.rideshare.R;
import com.app.rideshare.fragment.AddressFragment;
import com.app.rideshare.fragment.EmailFragment;
import com.app.rideshare.fragment.MobileNumberFragment;
import com.app.rideshare.fragment.NameFragment;
import com.app.rideshare.fragment.OTPFragment;
import com.app.rideshare.fragment.ProfilePhotoFragment;
import com.app.rideshare.notification.GCMRegistrationIntentService;
import com.app.rideshare.utils.PrefUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class SignUpActivity extends AppCompatActivity {

    String token;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Context context;

    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = this;

        PrefUtils.initPreference(this);

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


        mViewPager = (ViewPager) findViewById(R.id.pagerSignUp);
        mViewPager.setAdapter(new SignUpPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
        finish();
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

    public class SignUpPagerAdapter extends FragmentPagerAdapter {

        public SignUpPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /** Show a Fragment based on the position of the current screen */
            if (position == 0) {
                return new MobileNumberFragment();
            } else if (position == 1) {
                return new OTPFragment();
            } else if (position == 2) {
                return new NameFragment();
            } else if (position == 3) {
                return new AddressFragment();
            } else if (position == 4) {
                return new EmailFragment()   ;
            } else if (position == 5) {
                return new ProfilePhotoFragment()   ;
            } else
                return new OTPFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 6;
        }
    }
}

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.fragment.AddressFragment;
import com.app.rideshare.fragment.EmailFragment;
import com.app.rideshare.fragment.MobileNumberFragment;
import com.app.rideshare.fragment.NameFragment;
import com.app.rideshare.fragment.OTPFragment;
import com.app.rideshare.fragment.ProfilePhotoFragment;
import com.app.rideshare.notification.GCMRegistrationIntentService;
import com.app.rideshare.service.LocationService;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.widget.CustomViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SignUpActivity extends AppCompatActivity {

    public static String token = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Context context;

    public static CustomViewPager mViewPager;

    public static String mUserId = "";
    public static String PhoneNumber = "";

    public static String FirstName = "";
    public static String LastName = "";
    public static String HomeAddress = "";
    public static String EmailId = "";
    public static String ProfilePhotoPath = "";

    private int PICK_CAMERA = 1;
    private int PICK_GALLERY = 2;



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
                    PrefUtils.putString("TokenID",token);
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
            /*Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);*/

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context,new Intent(context, GCMRegistrationIntentService.class));
            } else {
                startService(new Intent(context, GCMRegistrationIntentService.class));
            }*/
            startService(new Intent(context, GCMRegistrationIntentService.class));
        }


        mViewPager = (CustomViewPager) findViewById(R.id.pagerSignUp);
        mViewPager.setAdapter(new SignUpPagerAdapter(getSupportFragmentManager()));
        mViewPager.setPagingEnabled(false);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();*/
        if (mViewPager.getCurrentItem() == 0) {
            finish();
        } else {
            int pos = mViewPager.getCurrentItem() - 1;
            mViewPager.setCurrentItem(pos);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        FirstName = "";
        LastName = "";
        HomeAddress = "";
        EmailId = "";

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
                return new EmailFragment();
            } else if (position == 5) {
                return new ProfilePhotoFragment();
            } else
                return new OTPFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 6;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PICK_GALLERY == requestCode && resultCode == Activity.RESULT_OK) {
            ArrayList<String> photos = (ArrayList<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            ProfilePhotoFragment.setProfilePhoto(photos.get(0));

            //Picasso.with(SignUpActivity.this).load(photos.get(0)).into(imgProfilePhoto);
        } else if (PICK_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            //String imagePath = "file://" + convertImageUriToFile(imageUri, getActivity());
            //File imgFile = new  File(imagePath);
            //Picasso.with(getActivity()).load(imagePath).resize(300,300).centerCrop().into(imgProfilePhoto);
            //Picasso.with(getActivity()).load(imgFile.getAbsolutePath()).resize(300,300).centerCrop().into(imgProfilePhoto);

        }
    }


}

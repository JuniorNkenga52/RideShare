package com.app.rideWhiz.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.app.rideWhiz.R;
import com.app.rideWhiz.fragment.AddressFragment;
import com.app.rideWhiz.fragment.CarInfoFragment;
import com.app.rideWhiz.fragment.EmailFragment;
import com.app.rideWhiz.fragment.MobileNumberFragment;
import com.app.rideWhiz.fragment.NameFragment;
import com.app.rideWhiz.fragment.OTPFragment;
import com.app.rideWhiz.fragment.ProfilePhotoFragment;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.widget.CustomViewPager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import java.util.ArrayList;


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
    public static String CarModel = "";
    public static String CarType = "";
    public static String CarSeats = "";
    public static String ProfilePhotoPath = "";

    private int PICK_CAMERA = 1;
    private int PICK_GALLERY = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = this;

        PrefUtils.initPreference(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                PrefUtils.putString("TokenID", newToken);

            }
        });


       /* int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
            }
        } else {
            startService(new Intent(context, MyFirebaseMessagingService.class));
        }*/


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
            boolean mbfrag = false;
            if (position == 0) {
                return new MobileNumberFragment();
            } else if (position == 1) {
                if (PrefUtils.getString("FragVal").equals("true")) {
                    PrefUtils.putString("FragVal", "false");
                    return new MobileNumberFragment();
                } else {
                    return new OTPFragment();
                }
                //return new OTPFragment();
            } else if (position == 2) {
                return new NameFragment();
            } else if (position == 3) {
                return new AddressFragment();
            } else if (position == 4) {
                return new EmailFragment();
            } else if (position == 5) {
                return new CarInfoFragment();
            } else if (position == 6) {
                return new ProfilePhotoFragment();
            } else {
                PrefUtils.putString("FragVal", "true");
                mViewPager.setCurrentItem(0);
                return new MobileNumberFragment();
            }
            //return new AddressFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 7;
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

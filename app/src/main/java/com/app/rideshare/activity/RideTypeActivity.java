package com.app.rideshare.activity;

import android.graphics.Typeface;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.utils.TypefaceUtils;

public class RideTypeActivity extends AppCompatActivity {


    RadioButton mNeedRideRb;
    RadioButton mOfferRideRb;

    Typeface mRobotoMediam;

    TextView mNextTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_type);

        mRobotoMediam= TypefaceUtils.getTypefaceRobotoMediam(this);

        mNeedRideRb=(RadioButton)findViewById(R.id.need_ride_rb);
        mOfferRideRb=(RadioButton)findViewById(R.id.offer_ride_rb);

        mNeedRideRb.setTypeface(mRobotoMediam);
        mOfferRideRb.setTypeface(mRobotoMediam);

        mNextTv=(TextView)findViewById(R.id.next_tv);
        mNextTv.setTypeface(mRobotoMediam);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select Ride");

    }
}

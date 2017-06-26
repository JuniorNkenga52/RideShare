package com.app.rideshare.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.TypefaceUtils;

/**
 * Created by rlogical-dev-19 on 26-Jun-2017.
 */

public class ProfileActivity extends AppCompatActivity
{
    private ImageView mBackIv;
    private TextView mSaveTv;
    private TextView mUserNameTv;

    private Typeface mRobotoMedium;

    User mUserBean;


    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mMobileEt;
    private EditText mEmailEt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        PrefUtils.initPreference(this);
        mUserBean= PrefUtils.getUserInfo();

        mRobotoMedium= TypefaceUtils.getTypefaceRobotoMediam(this);
        mBackIv=(ImageView)findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSaveTv=(TextView)findViewById(R.id.save_tv);
        mSaveTv.setTypeface(mRobotoMedium);
        mSaveTv.setVisibility(View.INVISIBLE);

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mUserNameTv=(TextView)findViewById(R.id.username_tv);
        mUserNameTv.setTypeface(mRobotoMedium);
        mUserNameTv.setText(mUserBean.getmFirstName());

        mFirstNameEt=(EditText)findViewById(R.id.first_name_et);
        mLastNameEt=(EditText)findViewById(R.id.last_name_et);
        mMobileEt=(EditText)findViewById(R.id.mobile_et);
        mEmailEt=(EditText)findViewById(R.id.email_et);

        mFirstNameEt.setTypeface(mRobotoMedium);
        mLastNameEt.setTypeface(mRobotoMedium);
        mMobileEt.setTypeface(mRobotoMedium);
        mEmailEt.setTypeface(mRobotoMedium);


        mFirstNameEt.setText(mUserBean.getmFirstName());
        mLastNameEt.setText(mUserBean.getmLastName());
        mMobileEt.setText(mUserBean.getmMobileNo());
        mEmailEt.setText(mUserBean.getmEmail());
    }
}

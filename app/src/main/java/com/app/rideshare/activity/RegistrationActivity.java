package com.app.rideshare.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.utils.TypefaceUtils;


public class RegistrationActivity extends AppCompatActivity
{
    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mEmailEt;
    private EditText mMobileEt;
    private EditText mPasswordEt;
    private EditText mConfirmPasswordEt;

    private TextView mSignupTv;
    private Typeface mRobotoMedium;

    private TextView mAuthenticationTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign Up");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRobotoMedium= TypefaceUtils.getTypefaceRobotoMediam(this);

        mFirstNameEt=(EditText)findViewById(R.id.first_name_et);
        mLastNameEt=(EditText)findViewById(R.id.last_name_et);
        mEmailEt=(EditText)findViewById(R.id.email_et);
        mMobileEt=(EditText)findViewById(R.id.mobile_et);
        mPasswordEt=(EditText)findViewById(R.id.password_et);
        mConfirmPasswordEt=(EditText)findViewById(R.id.confirm_password_et);

        mSignupTv=(TextView)findViewById(R.id.signup_tv);

        mSignupTv.setTypeface(mRobotoMedium);
        mFirstNameEt.setTypeface(mRobotoMedium);
        mLastNameEt.setTypeface(mRobotoMedium);
        mEmailEt.setTypeface(mRobotoMedium);
        mMobileEt.setTypeface(mRobotoMedium);
        mPasswordEt.setTypeface(mRobotoMedium);
        mConfirmPasswordEt.setTypeface(mRobotoMedium);

        mAuthenticationTv=(TextView)findViewById(R.id.authentication_sent_tv);
        mAuthenticationTv.setTypeface(mRobotoMedium);

    }
}

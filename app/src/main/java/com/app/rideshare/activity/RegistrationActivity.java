package com.app.rideshare.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationActivity extends AppCompatActivity {
    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mEmailEt;
    private EditText mMobileEt;
    private EditText mPasswordEt;
    private EditText mConfirmPasswordEt;

    private TextView mSignupTv;
    private Typeface mRobotoMedium;

    private TextView mAuthenticationTv;

    CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign Up");

        PrefUtils.initPreference(this);

        mProgressDialog = new CustomProgressDialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRobotoMedium = TypefaceUtils.getTypefaceRobotoMediam(this);

        mFirstNameEt = (EditText) findViewById(R.id.first_name_et);
        mLastNameEt = (EditText) findViewById(R.id.last_name_et);
        mEmailEt = (EditText) findViewById(R.id.email_et);
        mMobileEt = (EditText) findViewById(R.id.mobile_et);
        mPasswordEt = (EditText) findViewById(R.id.password_et);
        mConfirmPasswordEt = (EditText) findViewById(R.id.confirm_password_et);

        mSignupTv = (TextView) findViewById(R.id.signup_tv);

        mSignupTv.setTypeface(mRobotoMedium);
        mFirstNameEt.setTypeface(mRobotoMedium);
        mLastNameEt.setTypeface(mRobotoMedium);
        mEmailEt.setTypeface(mRobotoMedium);
        mMobileEt.setTypeface(mRobotoMedium);
        mPasswordEt.setTypeface(mRobotoMedium);
        mConfirmPasswordEt.setTypeface(mRobotoMedium);

        mAuthenticationTv = (TextView) findViewById(R.id.authentication_sent_tv);
        mAuthenticationTv.setTypeface(mRobotoMedium);

        mSignupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirstNameEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter First Name.");
                } else if (mLastNameEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter Last Name.");
                } else if (mEmailEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter Email.");
                } else if (mMobileEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter Mobile Number.");
                } else if (mPasswordEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter Last Name.");
                } else if (mConfirmPasswordEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter Last Name.");
                } else if (!mConfirmPasswordEt.getText().toString().equals(mPasswordEt.getText().toString())) {
                    ToastUtils.showShort(RegistrationActivity.this, "Password and Confirm password must be Same.");
                } else if (!AppUtils.isEmail(mEmailEt.getText().toString())) {
                    ToastUtils.showShort(RegistrationActivity.this, "Please enter valid email.");
                } else {
                    registerUser(mFirstNameEt.getText().toString(), mLastNameEt.getText().toString(), mEmailEt.getText().toString(), mMobileEt.getText().toString(), mPasswordEt.getText().toString());
                }
            }
        });
    }

    private void registerUser(String mFirstName, String mLastName, String mEmail, String mMobile, String password) {

        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).signup(mFirstName, mLastName, mEmail, mMobile, password).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response)
            {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin",true);
                        Intent i = new Intent(RegistrationActivity.this, RideTypeActivity.class);
                        startActivity(i);
                    } else {
                        ToastUtils.showShort(RegistrationActivity.this, response.body().getmMessage());
                    }
                } else {

                }
                mProgressDialog.cancel();
            }
            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }
}

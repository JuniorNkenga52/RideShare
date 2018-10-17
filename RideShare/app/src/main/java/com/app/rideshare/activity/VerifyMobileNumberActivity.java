package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.dpizarro.pinview.library.PinView;
import com.dpizarro.pinview.library.PinViewSettings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerifyMobileNumberActivity extends AppCompatActivity {

    CustomProgressDialog mProgressDialog;
    private TextView mResendTv;
    private TextView mVerifyTv;
    User mBean;
    private String mOTP = "";

    private TextView mTitleTv;
    //private Typeface mRobotoMeduim;
    private TextView mChangeMobile;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_mobile_number);

        mProgressDialog = new CustomProgressDialog(this);

        context=this;
        PrefUtils.initPreference(this);
        mBean = PrefUtils.getUserInfo();

        PinView pinView = (PinView) findViewById(R.id.pinView);

        PinViewSettings pinViewSettings = new PinViewSettings.Builder()
                .withMaskPassword(false)
                .withDeleteOnClick(false)
                .withKeyboardMandatory(false)
                .withNumberPinBoxes(5)
                .withNativePinBox(false)
                .build();

        pinView.setSettings(pinViewSettings);

        mResendTv = (TextView) findViewById(R.id.resend_tv);
        mVerifyTv = (TextView) findViewById(R.id.verify_tv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mTitleTv.setText("Please input your 5 digit verification code. We sent the code to your mobile number " + mBean.getmMobileNo());

        //mRobotoMeduim = TypefaceUtils.getTypefaceRobotoMediam(this);
        /*mResendTv.setTypeface(mRobotoMeduim);
        mVerifyTv.setTypeface(mRobotoMeduim);
        mTitleTv.setTypeface(mRobotoMeduim);*/


        mChangeMobile = (TextView) findViewById(R.id.change_mobile_tv);
        //mChangeMobile.setTypeface(mRobotoMeduim);
        mChangeMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerifyMobileNumberActivity.this, MobileNumberActivity.class);
                startActivity(i);
                finish();
            }
        });


        mResendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP(mBean.getmMobileNo(), mBean.getmUserId());
            }
        });

        pinView.setOnCompleteListener(new PinView.OnCompleteListener() {
            @Override
            public void onComplete(boolean completed, final String pinResults) {
                if (completed) {
                    mOTP = pinResults;
                }
            }
        });

        mVerifyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOTP.length() == 5) {
                    VerifyOTP(mOTP, mBean.getmUserId());
                }

            }
        });

    }

    private void sendOTP(final String mobileNuber, String nUserId) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class,context).sendOTP(mobileNuber, nUserId).enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageUtils.showSuccessMessage(VerifyMobileNumberActivity.this, "OTP Sent");
                } else {
                    MessageUtils.showPleaseTryAgain(VerifyMobileNumberActivity.this);
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    private void VerifyOTP(String otp, String nUserId) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class,context).verifyOTP(nUserId, otp).enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getmStatus().equals("success")) {
                        mBean.setmIsVerify("1");
                        PrefUtils.addUserInfo(mBean);
                        /*if(PrefUtils.getBoolean("firstTime")){
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            startActivity(i);
                            finish();
                        }else {
                            Intent i = new Intent(getBaseContext(), MyGroupActivity.class);
                            startActivity(i);
                            finish();
                            PrefUtils.putBoolean("firstTime",true);
                        }*/
                        Intent i = new Intent(getBaseContext(), MyGroupActivity.class);
                        startActivity(i);
                        finish();
                        PrefUtils.putBoolean("firstTime", true);

                    } else {
                        MessageUtils.showPleaseTryAgain(VerifyMobileNumberActivity.this);
                    }
                } else {
                    MessageUtils.showPleaseTryAgain(VerifyMobileNumberActivity.this);
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }
}

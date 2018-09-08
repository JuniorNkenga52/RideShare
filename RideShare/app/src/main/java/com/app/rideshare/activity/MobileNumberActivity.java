package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MobileNumberActivity extends AppCompatActivity {
    private TextView mTitleTv;
    private EditText mMobileEt;
    private TextView mInfoTv;

    private TextView mSendOTPTv;

    //private Typeface mRobotoRegular;
    CustomProgressDialog mProgressDialog;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_number_activity);
        mProgressDialog = new CustomProgressDialog(this);

        context=this;
        PrefUtils.initPreference(this);

        //mRobotoRegular = TypefaceUtils.getOpenSansRegular(this);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mMobileEt = (EditText) findViewById(R.id.mobile_et);
        mInfoTv = (TextView) findViewById(R.id.title_number_tv);


        mSendOTPTv = (TextView) findViewById(R.id.send_code_tv);
        //mSendOTPTv.setTypeface(mRobotoRegular);

        mSendOTPTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMobileEt.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(MobileNumberActivity.this, "Please enter mobile number.");
                } else {
                    sendOTP(mMobileEt.getText().toString(), PrefUtils.getUserInfo().getmUserId());
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
                    User bean = PrefUtils.getUserInfo();
                    bean.setmMobileNo(mobileNuber);
                    PrefUtils.addUserInfo(bean);

                    Intent i = new Intent(MobileNumberActivity.this, VerifyMobileNumberActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    MessageUtils.showPleaseTryAgain(MobileNumberActivity.this);
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

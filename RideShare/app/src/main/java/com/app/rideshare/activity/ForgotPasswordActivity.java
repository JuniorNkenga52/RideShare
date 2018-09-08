package com.app.rideshare.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    Context context;
    private EditText forgot_email_et;
    private TextView btn_forgot_password;
    private CustomProgressDialog mProgressDialog;
    private TextView txt_forgot_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        context = this;
        forgot_email_et = findViewById(R.id.forgot_email_et);
        txt_forgot_back = findViewById(R.id.txt_forgot_back);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);

        mProgressDialog = new CustomProgressDialog(this);
        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = forgot_email_et.getText().toString();
                if (email.isEmpty()) {
                    MessageUtils.showFailureMessage(context, "Please enter Email.");
                } else if (!AppUtils.isEmail(email) || email.isEmpty()) {
                    MessageUtils.showFailureMessage(context, "Please enter valid email.");
                } else {
                    forgotpassword(email);
                }

            }
        });
        txt_forgot_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void forgotpassword(String email) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class,context).forgotpassword(email).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        finish();
                    }

                    MessageUtils.showSuccessMessage(ForgotPasswordActivity.this, response.body().getmMessage());
                }
                mProgressDialog.cancel();

            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                mProgressDialog.cancel();
            }
        });
    }
}

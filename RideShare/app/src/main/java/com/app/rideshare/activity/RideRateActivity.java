package com.app.rideshare.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.RateRideResponce;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideRateActivity extends AppCompatActivity implements TextWatcher {

    Context context;
    RatingBar ride_rate;
    User mUserbean;
    CustomProgressDialog mProgressDialog;
    private TextView txt_rate_ride, txt_rate_ride_msg, txt_rate_type, txt_rate_word;
    private TextView btn_submit;
    //btn_cancle
    private EditText edt_comment;
    private String ride_id="", driver_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_rate);
        context = this;
        PrefUtils.initPreference(this);
        mProgressDialog = new CustomProgressDialog(this);

        mUserbean = PrefUtils.getUserInfo();

        if(getIntent().hasExtra("riderate")){
            ride_id = getIntent().getStringExtra("riderate");
            driver_id = getIntent().getStringExtra("driverid");
        }


        ride_rate = (RatingBar) findViewById(R.id.ride_rate);
        txt_rate_ride = (TextView) findViewById(R.id.txt_rate_ride);
        txt_rate_ride_msg = (TextView) findViewById(R.id.txt_rate_ride_msg);
        txt_rate_type = (TextView) findViewById(R.id.txt_rate_type);
        txt_rate_word = (TextView) findViewById(R.id.txt_rate_word);
        btn_submit = (TextView) findViewById(R.id.btn_submit);
        //btn_cancle = (Button) findViewById(R.id.btn_cancle);
        edt_comment = (EditText) findViewById(R.id.edt_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ride_rate_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rate My Ride");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
                if(tv.getText().equals(toolbar.getTitle())){
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
        ride_rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                txt_rate_type.setVisibility(View.VISIBLE);
                txt_rate_type.setText(String.valueOf(rating));

            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rates = txt_rate_type.getText().toString();
                String review = edt_comment.getText().toString();
                if (rates.equals("")) {
                    ToastUtils.showShort(context, "Please Rate Your Feed Back.");
                }else if (review.isEmpty()) {
                    ToastUtils.showShort(context, "Please Write Comment.");
                } else {
                    rateride(driver_id, mUserbean.getmUserId(), ride_id, rates, review);
                }

            }
        });

        /*btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        edt_comment.addTextChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int slenght = s.length();
        String input = edt_comment.getText().toString().trim().replaceAll("\n", "");
        String[] wordCount = input.split("\\s");
        txt_rate_word.setText(String.valueOf(wordCount.length));
        /*txt_rate_word.setText("Character count is: " + String.valueOf(slenght));
        txt_rate_word.setTextColor(Color.GREEN);*/
    }

    private void rateride(String driver_id, String rider_id, String ride_id, String ride_rate, String ride_review) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).rateride(driver_id, rider_id, ride_id, ride_rate, ride_review).enqueue(new Callback<RateRideResponce>() {
            @Override
            public void onResponse(Call<RateRideResponce> call, Response<RateRideResponce> response) {
                if (response.body() != null || response.body().getResult().size() > 0) {
                    if (response.body().getResult().size() != 0) {
                        ToastUtils.showShort(context, response.body().getMessage());
                    }
                }
                finish();
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<RateRideResponce> call, Throwable t) {
                ToastUtils.showShort(context, "Problem occurs while submitting");
                mProgressDialog.cancel();
            }
        });
    }
}

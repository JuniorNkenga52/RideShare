package com.app.rideshare.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.rideshare.R;

public class RideRateActivity extends AppCompatActivity implements TextWatcher {

    Context context;
    RatingBar ride_rate;
    TextView txt_rate_ride, txt_rate_ride_msg, txt_rate_type, txt_rate_word;
    Button btn_submit, btn_cancle;
    EditText edt_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_rate);
        context = this;
        ride_rate = (RatingBar) findViewById(R.id.ride_rate);
        txt_rate_ride = (TextView) findViewById(R.id.txt_rate_ride);
        txt_rate_ride_msg = (TextView) findViewById(R.id.txt_rate_ride_msg);
        txt_rate_type = (TextView) findViewById(R.id.txt_rate_type);
        txt_rate_word = (TextView) findViewById(R.id.txt_rate_word);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
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

            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
}

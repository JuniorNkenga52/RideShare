package com.app.rideshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.AcceptRider;
import com.app.rideshare.api.response.CancelRequest;
import com.app.rideshare.model.RideResponse;
import com.app.rideshare.model.Rider;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WaitingActivity extends AppCompatActivity {

    private TextView mCancelTv;
    private TextView mWaitTv;
    private Typeface mRobotoMedium;

    DonutProgress mCircleProgress;
    private OTPTimer timer;
    PulsatorLayout pulsator;

    Rider currentRider;

    private TextView mNameTv;
    private TextView mEmailTv;

    RideResponse mRideResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        mRobotoMedium = TypefaceUtils.getTypefaceRobotoMediam(this);

        currentRider = (Rider) getIntent().getExtras().getSerializable("rider");
        mRideResponse = (RideResponse) getIntent().getExtras().getSerializable("rider_data");

        mCancelTv = (TextView) findViewById(R.id.btn_cancel_ride);
        mCancelTv.setTypeface(mRobotoMedium);

        mWaitTv = (TextView) findViewById(R.id.wait_tv);
        mWaitTv.setTypeface(mRobotoMedium);

        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();


        mCircleProgress = (DonutProgress) findViewById(R.id.donut_progress);
        mCircleProgress.setMax(30);
        mCircleProgress.setSuffixText("");
        mCircleProgress.setStartingDegree(270);
        mCircleProgress.setShowText(false);
        mCircleProgress.setUnfinishedStrokeWidth(7);
        mCircleProgress.setFinishedStrokeWidth(7);
        mCircleProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        mCircleProgress.setUnfinishedStrokeColor(getResources().getColor(R.color.gray));

        mNameTv = (TextView) findViewById(R.id.name_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mNameTv.setTypeface(mRobotoMedium);
        mEmailTv.setTypeface(mRobotoMedium);

        mNameTv.setText(currentRider.getmFirstName());
        mEmailTv.setText(currentRider.getmEmail());

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cancelRequst(mRideResponse.getRide_id());

            }
        });

        startTimer();
    }

    @Override
    public void onBackPressed() {

    }

    public class OTPTimer extends CountDownTimer {

        public OTPTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / 1000);
            mCircleProgress.setText("" + progress);
            mCircleProgress.setProgress(progress);
        }

        @Override
        public void onFinish() {
            pulsator.stop();
            finish();
        }
    }

    protected void startTimer() {
        timer = new OTPTimer(30000, 1000);
        timer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(WaitingActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("request_status"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("int_data");

            try {
                JSONObject jobj = new JSONObject(info);

                if (jobj.getString("request_status").equals("1")) {
                    ToastUtils.showShort(WaitingActivity.this, "Request Accepted");
                    timer.cancel();
                    pulsator.stop();

                    JSONArray jmesg = jobj.getJSONArray("msg");
                    JSONObject jRider = jmesg.getJSONObject(0);

                    AcceptRider rider = new AcceptRider();
                    rider.setRequest_status(jRider.getString("request_status"));
                    rider.setRide_id(jRider.getString("ride_id"));
                    rider.setEnding_address(jRider.getString("ending_address"));
                    rider.setStart_long(jRider.getString("start_long"));
                    rider.setU_ride_type(jRider.getString("u_ride_type"));
                    rider.setFrom_id(jRider.getString("from_id"));
                    rider.setTo_id(jRider.getString("to_id"));
                    rider.setStarting_address(jRider.getString("starting_address"));
                    rider.setCreated_datetime(jRider.getString("created_datetime"));
                    rider.setStart_lati(jRider.getString("start_lati"));
                    rider.setEnd_long(jRider.getString("end_long"));
                    rider.setUpdated_datetime(jRider.getString("updated_datetime"));
                    rider.setEnd_lati(jRider.getString("end_lati"));

                    Intent i = new Intent(WaitingActivity.this, StartRideActivity.class);
                    i.putExtra("rideobj",rider);
                    startActivity(i);
                    finish();

                } else if (jobj.getString("request_status").equals("2")) {
                    ToastUtils.showShort(WaitingActivity.this, "Request Rejected");
                    finish();
                }

            } catch (Exception e) {

            }

        }
    };

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(WaitingActivity.this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public void cancelRequst(String mRideId) {
        ApiServiceModule.createService(RestApiInterface.class).cancelRequest(mRideId).enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        timer.cancel();
                        pulsator.stop();
                        finish();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }

}

package com.app.rideshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WaitingActivity extends AppCompatActivity {

    private String TAG = WaitingActivity.class.getName();
    private TextView mCancelTv;
    private TextView mWaitTv;
    //private Typeface mRobotoMedium;

    DonutProgress mCircleProgress;
    private OTPTimer timer;
    PulsatorLayout pulsator;

    Rider currentRider;

    private TextView mNameTv;
    private TextView mEmailTv;

    RideResponse mRideResponse;
    CircularImageView mProfilePic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        //mRobotoMedium = TypefaceUtils.getTypefaceRobotoMediam(this);

        currentRider = (Rider) getIntent().getExtras().getSerializable("rider");
        mRideResponse = (RideResponse) getIntent().getExtras().getSerializable("rider_data");

        mCancelTv = (TextView) findViewById(R.id.btn_cancel_ride);
        //mCancelTv.setTypeface(mRobotoMedium);

        mWaitTv = (TextView) findViewById(R.id.wait_tv);
        //mWaitTv.setTypeface(mRobotoMedium);

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
        mProfilePic = (CircularImageView) findViewById(R.id.user_profile);

        /*mNameTv.setTypeface(mRobotoMedium);
        mEmailTv.setTypeface(mRobotoMedium);*/

        mNameTv.setText(currentRider.getmFirstName());
        mEmailTv.setText(currentRider.getmEmail());
        try {
            if (!currentRider.getmProfileImage().equals("")) {
                //Picasso.with(this).load(currentRider.getmProfileImage()).into(mProfilePic);

                Glide.with(this).load(currentRider.getmProfileImage())
                        .error(R.drawable.icon_test)
                        .placeholder(R.drawable.icon_test)
                        .into(mProfilePic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                Log.e(TAG, "onReceive: jobj >> " + jobj);
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
                    rider.setStarting_address(jRider.getString("starting_address"));
                    rider.setCreated_datetime(jRider.getString("created_datetime"));
                    rider.setStart_lati(jRider.getString("start_lati"));
                    rider.setEnd_long(jRider.getString("end_long"));
                    rider.setUpdated_datetime(jRider.getString("updated_datetime"));
                    rider.setEnd_lati(jRider.getString("end_lati"));


                    JSONObject jFromRider = jRider.getJSONObject("from_id");
                    Rider fromRider = new Rider();
                    fromRider.setnUserId(jFromRider.getString("u_id"));
                    fromRider.setmFirstName(jFromRider.getString("u_firstname"));
                    fromRider.setmLastName(jFromRider.getString("u_lastname"));
                    fromRider.setmEmail(jFromRider.getString("u_email"));
                    fromRider.setmProfileImage(jFromRider.getString("profile_image"));
                    fromRider.setmLatitude(jFromRider.getString("u_lat"));
                    fromRider.setmLongitude(jFromRider.getString("u_long"));
                    rider.setFromRider(fromRider);


                    JSONObject jToRider = jRider.getJSONObject("to_id");
                    Rider toRider = new Rider();
                    toRider.setnUserId(jToRider.getString("u_id"));
                    toRider.setmFirstName(jToRider.getString("u_firstname"));
                    toRider.setmLastName(jToRider.getString("u_lastname"));
                    toRider.setmEmail(jToRider.getString("u_email"));
                    toRider.setmProfileImage(jToRider.getString("profile_image"));
                    toRider.setmLatitude(jToRider.getString("u_lat"));
                    toRider.setmLongitude(jToRider.getString("u_long"));
                    rider.setToRider(toRider);


                    Intent i = new Intent(WaitingActivity.this, StartRideActivity.class);
                    i.putExtra("rideobj", rider);
                    startActivity(i);
                    finish();

                } else if (jobj.getString("request_status").equals("2")) {
                    ToastUtils.showShort(WaitingActivity.this, "Request Rejected");
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
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

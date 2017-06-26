package com.app.rideshare.activity;

import android.accessibilityservice.GestureDescription;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.AcceptRequest;
import com.app.rideshare.api.response.SendResponse;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.JsonNull;

import org.json.JSONObject;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationActivity extends AppCompatActivity
{

    CustomProgressDialog mProgressDialog;

    private Button mAcceptBtn;
    private Button mRejectBtn;

    private String mRideId;
    private String mFirstName;

    private TextView mTitleTv;

    private Typeface mRobotoMedium;


    DonutProgress mCircleProgress;
    private OTPTimer timer;
    PulsatorLayout pulsator;


    private String mStartingAddress;
    private String mEndingAddress;

    private TextView mStartingAddressTv;
    private TextView mEndingAddressTv;

    MediaPlayer BG;
    Vibrator vibration;
    JSONObject jobjRide;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mProgressDialog = new CustomProgressDialog(this);

        try{
            jobjRide= new JSONObject(getIntent().getExtras().getString("data"));
            Log.d("erroe",""+jobjRide);

            mRideId=jobjRide.getString("ride_id");
            mFirstName=jobjRide.getString("u_firstname");
            mStartingAddress=jobjRide.getString("starting_address");
            mEndingAddress=jobjRide.getString("ending_address");
        }catch (Exception e){
            Log.d("erroe",e.toString());
        }

        mStartingAddressTv=(TextView)findViewById(R.id.starting_address_tv);
        mEndingAddressTv=(TextView)findViewById(R.id.ending_address_tv);


        mAcceptBtn=(Button)findViewById(R.id.btnAccept);
        mRejectBtn=(Button)findViewById(R.id.btnReject);
        mTitleTv=(TextView)findViewById(R.id.title_tv);
        mRobotoMedium= TypefaceUtils.getTypefaceRobotoMediam(this);

        mStartingAddressTv.setTypeface(mRobotoMedium);
        mEndingAddressTv.setTypeface(mRobotoMedium);

        mStartingAddressTv.setText(mStartingAddress);
        mEndingAddressTv.setText(mEndingAddress);

        mTitleTv.setTypeface(mRobotoMedium);
        mAcceptBtn.setTypeface(mRobotoMedium);
        mRejectBtn.setTypeface(mRobotoMedium);

        mTitleTv.setText("Ride Request from "+mFirstName);

        mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BG.isPlaying()){
                    BG.stop();
                    vibration.cancel();
                }
                acceptOrRejectRequest(mRideId,"1");

            }
        });
        mRejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BG.isPlaying()){
                    BG.stop();
                    vibration.cancel();
                }
                acceptOrRejectRequest(mRideId,"0");
            }
        });



        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.setColor(getResources().getColor(R.color.colorPrimary));
        pulsator.start();


        mCircleProgress=(DonutProgress)findViewById(R.id.donut_progress);
        mCircleProgress.setMax(30);
        mCircleProgress.setSuffixText("");
        mCircleProgress.setStartingDegree(270);
        mCircleProgress.setShowText(false);
        mCircleProgress.setUnfinishedStrokeWidth(7);
        mCircleProgress.setFinishedStrokeWidth(7);
        mCircleProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        mCircleProgress.setUnfinishedStrokeColor(getResources().getColor(R.color.gray));

        startTimer();
        playSound();

    }


    public void acceptOrRejectRequest(String mRideId,String acceptOrreject)
    {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).acceptRequest(mRideId, acceptOrreject).enqueue(new Callback<AcceptRequest>()
        {
            @Override
            public void onResponse(Call<AcceptRequest> call, Response<AcceptRequest> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    if(response.body().getStatus().equals("success"))
                    {
                        if(response.body().getMsg().get(0).getRequest_status().equals("1"))
                        {
                            Intent i=new Intent(NotificationActivity.this,StartRideActivity.class);
                            i.putExtra("rideobj",response.body().getMsg().get(0));
                            startActivity(i);
                            finish();
                        }
                    }
                } else {

                }
                mProgressDialog.cancel();
                finish();
            }

            @Override
            public void onFailure(Call<AcceptRequest> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
                finish();
            }
        });
    }
    public class OTPTimer extends CountDownTimer {

        public OTPTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / 1000);
            mCircleProgress.setText(""+progress);
            mCircleProgress.setProgress(progress);
        }

        @Override
        public void onFinish() {
            pulsator.stop();
            if(BG.isPlaying()){
                BG.stop();
                vibration.cancel();
            }
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
        LocalBroadcastManager.getInstance(NotificationActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("request_notification"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("int_data");

            try{
                JSONObject jobj=new JSONObject(info);

                if(jobj.getString("status").equals("success"))
                {
                    ToastUtils.showShort(NotificationActivity.this,"Ride Cancel");
                }

            }catch (Exception e){

            }

        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null)
            timer.cancel();

        if(BG.isPlaying()){
            BG.stop();
            vibration.cancel();
        }
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(NotificationActivity.this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }


    private void playSound(){
        vibration = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 0, 100, 700};
        vibration.vibrate(pattern, 0);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            BG = MediaPlayer.create(getBaseContext(), notification);
        } catch (Exception e) {
            BG = MediaPlayer.create(getBaseContext(), R.raw.morningsunshine);
            e.printStackTrace();
        }

        BG.setLooping(true);
        BG.setVolume(100, 100);
        BG.start();
    }
}

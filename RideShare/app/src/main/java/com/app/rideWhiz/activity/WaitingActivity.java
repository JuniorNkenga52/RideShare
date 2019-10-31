package com.app.rideWhiz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.api.response.CancelRequest;
import com.app.rideWhiz.listner.SocketConnection;
import com.app.rideWhiz.model.RideResponse;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.notificationservice.ManageNotifications;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WaitingActivity extends AppCompatActivity {

    private String TAG = WaitingActivity.class.getName();
    private TextView mCancelTv;
    private TextView mWaitTv;

    DonutProgress mCircleProgress;
    private OTPTimer timer;
    PulsatorLayout pulsator;

    Rider currentRider;
    User mUserBean;

    private TextView mNameTv;
    private TextView mEmailTv;

    CircularImageView mProfilePic;
    Context context;
    RideResponse mRideResponse;
    Activity activity;
    RideShareApp mApp;
    private CustomProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        context = this;
        activity = this;

        mApp = (RideShareApp) getApplicationContext();
        mProgressDialog = new CustomProgressDialog(activity);
        currentRider = (Rider) getIntent().getExtras().getSerializable("NotificationData");
        mUserBean = (User) getIntent().getExtras().getSerializable("UserData");
        mRideResponse = (RideResponse) getIntent().getExtras().getSerializable("rider_data");

        mCancelTv = (TextView) findViewById(R.id.btn_cancel_ride);
        mWaitTv = (TextView) findViewById(R.id.wait_tv);
        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

        mCircleProgress = (DonutProgress) findViewById(R.id.donut_progress);
        mCircleProgress.setMax(60);
        mCircleProgress.setSuffixText("");
        mCircleProgress.setStartingDegree(270);
        mCircleProgress.setShowText(false);
        mCircleProgress.setUnfinishedStrokeWidth(12);
        mCircleProgress.setFinishedStrokeWidth(12);
        mCircleProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        mCircleProgress.setUnfinishedStrokeColor(getResources().getColor(R.color.gray));

        mNameTv = (TextView) findViewById(R.id.name_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mProfilePic = (CircularImageView) findViewById(R.id.user_profile);

        if (currentRider != null) {
            mNameTv.setText(currentRider.getmFirstName());
            mEmailTv.setText(currentRider.getmEmail());
            try {
                if (!currentRider.getThumb_image().equals("")) {

                    Glide.with(this)
                            .load(currentRider.getThumb_image())
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.user_icon)
                            .dontTransform()
                            .into(mProfilePic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send Cancel Ride Socket Notification to Driver
                Log.d("Ride ID :::: >>>> ", currentRider.getRideID());
                CancelRideRequest(currentRider.getRideID());

            }
        });

        if (mApp.mWebSocketSendRequest == null) {
            mApp.connectRideRequest();
        }

        mApp.setSocketConnection(new SocketConnection() {
            @Override
            public void onMessageReceived(String response) {
                Log.d("Received", "");
                try {
                    JSONObject jFromRider = new JSONObject(response);
                    if (mUserBean.getmUserId().equals(jFromRider.getString("username"))) {
                        // Get the Not Current Route Ride Notification from Driver.
                        if (jFromRider.optString("sender_user").equals("1009")) {
                            //MessageUtils.showFailureMessage(context,jFromRider.getString("chat_message"));
                            if (timer != null)
                                timer.cancel();
                            //pulsator.stop();
                            mApp.mWebSocketSendRequest.close();
                            mApp.mWebSocketSendRequest = null;
                            finish();

                        } else {
                            // Get the Accept Ride Notification from Driver.
                            String chatMessage = jFromRider.getString("chat_message");
                            if (chatMessage.equals("Ride Rejected")) {
                                ManageNotifications.sendNotification(activity, null, "Ride Rejected", "1004");
                                finish();
                            } else {
                                ManageNotifications.sendNotification(activity, null, "Ride is Accepted", "1003");
                                setRequestData(activity, new JSONObject(jFromRider.getString("chat_message")));
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnected() {
                if (jMessage != null) {
                    mApp.mWebSocketSendRequest.send(jMessage.toString());
                }
            }
        });
        startTimer();
    }

    @Override
    public void onBackPressed() {

    }


    public class OTPTimer extends CountDownTimer {

        private OTPTimer(long millisInFuture, long countDownInterval) {
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

            if (timer != null)
                timer.cancel();
            pulsator.stop();
            CancelRideRequest(currentRider.getRideID());
        }
    }

    protected void startTimer() {
        timer = new OTPTimer(60000, 1000);
        timer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setRequestData(Activity activity, JSONObject jRider) {
        try {

            AcceptRider rider = new AcceptRider();
            rider.setRequest_status(jRider.getString("request_status"));
            rider.setRide_id(jRider.getString("ride_id"));
            rider.setEnding_address(jRider.getString("ending_address"));
            rider.setStart_long(jRider.getString("start_long"));
            rider.setU_ride_type(jRider.getString("u_ride_type"));
            rider.setStarting_address(jRider.getString("starting_address"));
            rider.setStart_lati(jRider.getString("start_lati"));
            rider.setEnd_long(jRider.getString("end_long"));
            rider.setEnd_lati(jRider.getString("end_lati"));


            Rider fromRider = new Rider();
            fromRider.setnUserId(mUserBean.getmUserId());
            fromRider.setmFirstName(mUserBean.getmFirstName());
            fromRider.setmLastName(mUserBean.getmLastName());
            fromRider.setmEmail(mUserBean.getmEmail());
            fromRider.setmProfileImage(mUserBean.getProfile_image());
            fromRider.setThumb_image(mUserBean.getThumb_image());
            fromRider.setmLatitude(mUserBean.getmLatitude());
            fromRider.setmLongitude(mUserBean.getmLongitude());
            rider.setFromRider(fromRider);

            Rider toRider = new Rider();
            toRider.setnUserId(currentRider.getnUserId());
            toRider.setmFirstName(currentRider.getmFirstName());
            toRider.setmLastName(currentRider.getmLastName());
            toRider.setmEmail(currentRider.getmEmail());
            toRider.setmProfileImage(currentRider.getmProfileImage());
            toRider.setThumb_image(currentRider.getThumb_image());
            toRider.setmLatitude(currentRider.getmLatitude());
            toRider.setmLongitude(currentRider.getmLongitude());
            toRider.setIs_driver("0");
            rider.setToRider(toRider);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (timer != null)
                        timer.cancel();
                    pulsator.stop();
                }
            });

            Intent i = new Intent(activity, StartRideActivity.class);
            i.putExtra("rideobj", rider);
            activity.startActivity(i);
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    JSONObject jMessage = new JSONObject();

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        pulsator.stop();
        super.onDestroy();

    }

    // Call API for Cancel Rider's Ride
    private void CancelRideRequest(String ride_id) {
        if (!isFinishing()) {
            mProgressDialog.show();
        }
        ApiServiceModule.createService(RestApiInterface.class, activity).cancelRequest(ride_id).enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (timer != null)
                    timer.cancel();
                pulsator.stop();
                sendCancelSocketRideRequest(currentRider.getnUserId());
            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                if (timer != null)
                    timer.cancel();
                pulsator.stop();
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                sendCancelSocketRideRequest(currentRider.getnUserId());
            }
        });
    }

    private void sendCancelSocketRideRequest(String userid) {

        try {
            Gson gson = new Gson();
            jMessage.put("chat_message", gson.toJson(mUserBean));
            jMessage.put("chat_user", userid);
            jMessage.put("sender_user", "1005");
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");

            if (mApp.mWebSocketSendRequest == null) {
                mApp.connectRideRequest();
                mApp.setSocketConnection(new SocketConnection() {
                    @Override
                    public void onMessageReceived(String response) {

                    }

                    @Override
                    public void onConnected() {
                        mApp.mWebSocketSendRequest.send(jMessage.toString());
                    }
                });
            } else {
                if (mApp.mWebSocketSendRequest.isOpen()) {
                    mApp.mWebSocketSendRequest.send(jMessage.toString());
                }
            }
            mApp.mWebSocketSendRequest.close();
            mApp.mWebSocketSendRequest = null;
            finish();
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

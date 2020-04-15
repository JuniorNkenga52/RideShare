package com.app.rideWhiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.RequestListAdapter;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.AcceptRequest;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.chat.CommonMethods;
import com.app.rideWhiz.listner.OnFinishViewListener;
import com.app.rideWhiz.listner.SocketConnection;
import com.app.rideWhiz.model.Directions;
import com.app.rideWhiz.model.Route;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MapDirectionAPI;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.utils.ToSort;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewActivity extends AppCompatActivity {

    List<Route> routes;
    List<Route> new_routes;
    ImageView image_next;
    CommonMethods commonMethods;
    float driver_duration = 0.0f;
    float driver_distance = 0.0f;
    ArrayList<User> mListReqTemp = new ArrayList<>();
    boolean isDirectionApiCalling = false;
    int reqPos = 0;
    private RecyclerView list_requests;
    private RecyclerView.LayoutManager mLayoutManager;
    private RequestListAdapter mAdapter;
    private ArrayList<User> requestList;
    private ArrayList<User> requestList2;
    private User fromUserInfo;
    private User toUserInfo;
    private User newFromUser;
    private Activity activity;
    private RideShareApp mApp;
    //Timers Info
    private DonutProgress mCircleProgress;
    private OTPTimer timer;
    private PulsatorLayout pulsator;
    public static MediaPlayer BG;
    public static Vibrator vibration;
    private CustomProgressDialog mProgressDialog;

    LatLng currentlthg;
    LatLng pickuplocation;
    LatLng droppfflocation;

    // Additional Time for driver to complete new Request Ride
    private okhttp3.Callback additionalTimeCallback = new okhttp3.Callback() {

        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            mProgressDialog.dismiss();
        }

        @Override
        public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                mProgressDialog.dismiss();
                final String json = response.body().string();
                Log.w(">>>>>>>", "json :: >>>>>>>>>>>>>>>>>>" + json);
                activity.runOnUiThread(() -> {
                    try {
                        Directions directions = new Directions(activity);
                        List<Route> routes = directions.parse(json);
                        if (routes.size() > 0) {
                            float duration;
                            if (routes.get(0).getLegs().get(0).getDuration().getText().split("\\s").length > 2) {
                                float hour = Float.parseFloat(routes.get(0).getLegs().get(0).getDuration().getText().split("\\s")[0]) * 60;
                                float mins = Float.parseFloat(routes.get(0).getLegs().get(0).getDuration().getText().split("\\s")[2]);
                                duration = hour + mins;
                            } else {
                                duration = Float.parseFloat(routes.get(0).getLegs().get(0).getDuration().getText().split("\\s")[0]);
                            }
                            String strdis = routes.get(0).getLegs().get(0).getDistance().getText();
                            float distance;
                            if (strdis.split("\\s")[1].equals("m")) {
                                distance = Float.parseFloat(strdis.split("\\s")[0]) / 1000;
                            } else {
                                distance = Float.parseFloat(strdis.split("\\s")[0]);
                            }
                            if (newFromUser != null) {
                                float dur = 0.0f;
                                float dis = 0.0f;

                                float lastdur = 0.0f;
                                float lastdis = 0.0f;

                                for (int i = 0; i < requestList.size(); i++) {
                                    if (dur < Float.parseFloat(requestList.get(i).getRide_time())) {
                                        dur += (Float.parseFloat(requestList.get(i).getRide_time()) + driver_duration);
                                        dis += (Float.parseFloat(requestList.get(i).getRide_distance()) + driver_distance);
                                        lastdur = Float.parseFloat(requestList.get(i).getRide_time());
                                        lastdis = Float.parseFloat(requestList.get(i).getRide_distance());
                                    } else {
                                        dur = Float.parseFloat(requestList.get(i).getAdd_ride_time());
                                        dis = Float.parseFloat(requestList.get(i).getAdd_ride_distance());
                                    }
                                }

                                float finaldur = duration > lastdur ? duration + dur : 0.0f;
                                float finaldes = distance > lastdis ? distance + dis : 0.0f;

                                /*float finaldur = duration > lastdur ? duration + (duration - dur) : 0.0f;
                                float finaldes = distance > lastdis ? distance + (distance - dis) : 0.0f;*/

                                newFromUser.setAdd_ride_time(String.valueOf(finaldur));
                                newFromUser.setRide_time(String.valueOf(duration));

                                newFromUser.setAdd_ride_distance(String.valueOf(finaldes));
                                //newFromUser.setRide_distance(String.valueOf(distance));
                                newFromUser.setDriver_pickup_distance(String.valueOf(driver_distance));
                                mAdapter.updateData(newFromUser, 0);
                                //requestList.add(newFromUser);

                                new Handler().postDelayed(() -> {
                                    reqPos++;
                                    Log.w("Meet XYZ", "" + mListReqTemp.size() + "--- " + reqPos);
                                    if (mListReqTemp.size() > reqPos) {

                                        newFromUser = mListReqTemp.get(reqPos);

                                        newFromUser.setAdd_ride_time(newFromUser.getAdd_ride_time());
                                        newFromUser.setRide_time(newFromUser.getRide_time());
                                        newFromUser.setAdd_ride_distance(newFromUser.getAdd_ride_distance());
                                        //newFromUser.setRide_distance(newFromUser.getRide_distance());

                                        //requestList.add(newFromUser);

                                        /*MapDirectionAPI.getDirection(newFromUser.getReq_source(), newFromUser.getReq_des(),
                                                activity).enqueue(getNewRoutePolygonsCallback);*/

                                        MapDirectionAPI.getDirection(newFromUser.getReq_source(), newFromUser.getReq_des(),
                                                activity).enqueue(getNewRoutePolygonsCallback);
                                    } else {
                                        isDirectionApiCalling = false;
                                        mListReqTemp.clear();
                                        reqPos = 0;
                                    }

                                }, 1500);

                            } else {
                                fromUserInfo.setAdd_ride_time(String.valueOf(duration + driver_duration));
                                fromUserInfo.setRide_time("" + duration);

                                fromUserInfo.setAdd_ride_distance(String.valueOf(distance + driver_distance));
                                //fromUserInfo.setRide_distance("" + distance);
                                fromUserInfo.setDriver_pickup_distance(String.valueOf(driver_distance));

                                requestList.set(0, fromUserInfo);

                                setAdapter();

                                new Handler().postDelayed(() -> {
                                    reqPos++;
                                    Log.w("Meet XYZ", "" + mListReqTemp.size() + "--- " + reqPos);
                                    if (mListReqTemp.size() > reqPos) {

                                        newFromUser = mListReqTemp.get(reqPos);

                                        newFromUser.setAdd_ride_time(fromUserInfo.getAdd_ride_time());
                                        newFromUser.setRide_time(fromUserInfo.getRide_time());
                                        newFromUser.setAdd_ride_distance(fromUserInfo.getAdd_ride_distance());
                                        //newFromUser.setRide_distance(fromUserInfo.getRide_distance());

                                        //requestList.add(newFromUser);
                                        MapDirectionAPI.getDirection(mListReqTemp.get(reqPos).getReq_source(), mListReqTemp.get(reqPos).getReq_des(),
                                                activity).enqueue(getNewRoutePolygonsCallback);
                                    } else {
                                        isDirectionApiCalling = false;
                                        mListReqTemp.clear();
                                        reqPos = 0;
                                    }

                                }, 1500);
                            }
                            //requestList.add()
                        } else {
                            Toast.makeText(activity, json, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    };
    private okhttp3.Callback driverRouteTimeCallback = new okhttp3.Callback() {

        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            mProgressDialog.dismiss();
        }

        @Override
        public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                mProgressDialog.dismiss();
                final String json = response.body().string();
                Log.w(">>>>>>>", "json :: >>>>>>>>>>>>>>>>>>" + json);
                activity.runOnUiThread(() -> {
                    try {
                        Directions directions = new Directions(activity);
                        List<Route> routes = directions.parse(json);
                        //if (directionLine != null) directionLine.remove();
                        if (routes.size() > 0) {
                            driver_duration = Float.parseFloat(routes.get(0).getLegs().get(0).getDuration().getText().split("\\s")[0]);
                            String distance = routes.get(0).getLegs().get(0).getDistance().getText();
                            if (distance.split("\\s")[1].equals("m")) {
                                driver_distance = Float.parseFloat(distance.split("\\s")[0]) / 1000;
                            } else {
                                driver_distance = Float.parseFloat(distance.split("\\s")[0]);
                            }

                            calculateAdditionalTime_Distance();
                        } else {
                            Toast.makeText(activity, json, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    };
    // Get the Current Lat & Long Polygons from MAP  Response
    private okhttp3.Callback getRoutePolygonsCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d("Error", e.toString());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                Log.d("Response for Json", ">>>>>>>>>>>>> \n\n" + json);
                getRoutes(json);

                LatLng source = new LatLng(Double.parseDouble(fromUserInfo.getStart_lat()),
                        Double.parseDouble(fromUserInfo.getStart_long()));

                LatLng dest = new LatLng(Double.parseDouble(fromUserInfo.getEnd_lat()),
                        Double.parseDouble(fromUserInfo.getEnd_long()));


                MapDirectionAPI.getDirection(source, dest,
                        activity).enqueue(getNewRoutePolygonsCallback);
            }
        }
    };
    // Get the New Lat & Long Polygons from MAP  Response
    private okhttp3.Callback getNewRoutePolygonsCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d("Error", e.toString());
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                Log.d("Response for Json", ">>>>>>>>>>>>> \n\n" + json);
                checkRideRoute(json);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        activity = this;
        init();
    }

    public void init() {
        mApp = (RideShareApp) getApplicationContext();
        commonMethods = new CommonMethods(getApplicationContext());
        mProgressDialog = new CustomProgressDialog(activity);
        currentlthg = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
        if (mApp.mWebSocketSendRequest == null) {
            mApp.connectRideRequest();
        }

        if (getIntent().hasExtra("fromUSerData")) {
            fromUserInfo = (User) getIntent().getExtras().getSerializable("fromUSerData");
        }
        toUserInfo = PrefUtils.getUserInfo();

        image_next = findViewById(R.id.image_next);


        pulsator = findViewById(R.id.pulsator);
        pulsator.setColor(getResources().getColor(R.color.colorPrimary));
        pulsator.start();

        mCircleProgress = findViewById(R.id.donut_progress);
        mCircleProgress.setMax(60);
        mCircleProgress.setSuffixText("");
        mCircleProgress.setStartingDegree(270);
        mCircleProgress.setShowText(false);
        mCircleProgress.setUnfinishedStrokeWidth(12);
        mCircleProgress.setFinishedStrokeWidth(12);
        mCircleProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        mCircleProgress.setUnfinishedStrokeColor(getResources().getColor(R.color.gray));

        startTimer();
        if (BG != null) {
            BG = null;
        }
        AppUtils.playSound(getApplicationContext());

        requestList = new ArrayList<>();
        requestList2 = new ArrayList<>();

        if (fromUserInfo != null) {
            commonMethods.deleteRideTable("UserRides");
            commonMethods.deleteRidersTable("Riders");
            commonMethods.deletePolyTable("Ploy");
            requestList.add(fromUserInfo);
        } else {
            requestList = commonMethods.fetchRides("UserRides", true);
            image_next.setVisibility(View.VISIBLE);
        }
        image_next.setOnClickListener(v -> finish());

        // Get the Cancel Ride Request from Rider to Driver. 1005
        // Get the Rider's Requests List Data 1002

        getRideRequestListListener();
        currentRoute();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();
        list_requests = findViewById(R.id.list_requests);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list_requests.setLayoutManager(mLayoutManager);
    }

    private void getRideRequestListListener() {
        mApp.setSocketConnection(new SocketConnection() {
            @Override
            public void onMessageReceived(String response) {
                JSONObject jFromRider;
                try {
                    jFromRider = new JSONObject(response);
                    Gson gson = new Gson();
                    Type type = new TypeToken<User>() {
                    }.getType();
                    final User newFromUser_ = gson.fromJson(jFromRider.getString("chat_message"), type);
                    if (toUserInfo.getmUserId().equals(jFromRider.getString("username"))) {
                        final JSONObject finalJFromRider = jFromRider;
                        runOnUiThread(() -> {
                            if (list_requests != null) {
                                if (finalJFromRider.optString("sender_user").equals("1005")) {
                                    // Delete the Current Request
                                    try {
                                        mAdapter.updateData(newFromUser_, 1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                    LatLng pickuplat = new LatLng(Double.parseDouble(newFromUser_.getStart_lat()), Double.parseDouble(newFromUser_.getStart_long()));
                                    LatLng dropplat = new LatLng(Double.parseDouble(newFromUser_.getEnd_lat()), Double.parseDouble(newFromUser_.getEnd_long()));
                                    newFromUser_.setReq_source(pickuplat);
                                    newFromUser_.setReq_des(dropplat);
                                    mListReqTemp.add(newFromUser_);

                                    if (!isDirectionApiCalling) {
                                        isDirectionApiCalling = true;

                                        newFromUser = newFromUser_;

                                        MapDirectionAPI.getDirection(newFromUser.getReq_source(), newFromUser.getReq_des(),
                                                activity).enqueue(getNewRoutePolygonsCallback);
                                    }
                                }
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnected() {

            }
        });
    }

    protected void startTimer() {
        timer = new OTPTimer(600000, 1000);
        timer.start();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list_requests = null;
        if (timer != null)
            timer.cancel();

        if (BG != null && BG.isPlaying()) {
            BG.stop();
            BG.release();
            BG = null;
            vibration.cancel();
        }
    }

    // Call API for Accept or Reject Ride
    public void acceptOrRejectRequest(final User selUser, final String acceptOrreject) {
        if (!activity.isFinishing()) {
            mProgressDialog.show();
        }
        ApiServiceModule.createService(RestApiInterface.class, activity).acceptRequest(selUser.getRequest_share_id(), acceptOrreject).enqueue(new Callback<AcceptRequest>() {
            @Override
            public void onResponse(Call<AcceptRequest> call, Response<AcceptRequest> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        if (response.body().getMsg().get(0).getRequest_status().equals("1")) {
                            // for sending from Socket
                            AcceptRider acceptRider2 = new AcceptRider();
                            acceptRider2.setRequest_status("1");
                            acceptRider2.setRide_id(response.body().getMsg().get(0).getRide_id());
                            acceptRider2.setU_ride_type("2");
                            acceptRider2.setStart_long(selUser.getStart_long());
                            acceptRider2.setStart_lati(selUser.getStart_lat());
                            acceptRider2.setEnd_long(selUser.getEnd_long());
                            acceptRider2.setEnd_lati(selUser.getEnd_lat());
                            acceptRider2.setStarting_address(AppUtils.getAddress(activity,
                                    Double.parseDouble(selUser.getStart_lat()), Double.parseDouble(selUser.getStart_long())));
                            acceptRider2.setEnding_address(AppUtils.getAddress(activity,
                                    Double.parseDouble(selUser.getEnd_lat()), Double.parseDouble(selUser.getEnd_long())));

                            // for adding data in DB.
                            AcceptRider acceptRider3 = response.body().getMsg().get(0);
                            acceptRider3.getFromRider().setnUserId(selUser.getmUserId());
                            acceptRider3.getFromRider().setmEmail(selUser.getmEmail());
                            acceptRider3.setRequest_status("1");
                            acceptRider3.setRide_id(response.body().getMsg().get(0).getRide_id());
                            acceptRider3.setU_ride_type("2");
                            acceptRider3.setStart_long(selUser.getStart_long());
                            acceptRider3.setStart_lati(selUser.getStart_lat());
                            acceptRider3.setEnd_long(selUser.getEnd_long());
                            acceptRider3.setEnd_lati(selUser.getEnd_lat());
                            acceptRider3.setStarting_address(AppUtils.getAddress(activity,
                                    Double.parseDouble(selUser.getStart_lat()), Double.parseDouble(selUser.getStart_long())));
                            acceptRider3.setEnding_address(AppUtils.getAddress(activity,
                                    Double.parseDouble(selUser.getEnd_lat()), Double.parseDouble(selUser.getEnd_long())));

                            Gson gson = new Gson();
                            sendSocketRideRequest(acceptRider3, gson.toJson(acceptRider2), "1003", selUser);
                        } else {

                            if (acceptOrreject.equals("0")) {
                                try {
                                    if (mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                    sendSocketRideRequest(null, "Ride Rejected", "1004", selUser);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<AcceptRequest> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                //mProgressDialog.cancel();
                finish();
            }
        });
    }

    // Send Socket Accept or Reject Socket Notification from Driver to Rider
    private void sendSocketRideRequest(AcceptRider acceptRider, String ChatMessage, String requestType, User selectedUser) {
        final JSONObject jMessage = new JSONObject();
        try {
            jMessage.put("chat_message", ChatMessage);
            jMessage.put("chat_user", selectedUser.getmUserId());
            jMessage.put("sender_user", requestType);
            jMessage.put("message_type", "chat-box-html");
            jMessage.put("message_new", " ");
            sendAcceptRejectReq(jMessage, acceptRider, requestType, selectedUser);
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendAcceptRejectReq(final JSONObject jMessage, AcceptRider acceptRider, String requestType, User selectedUser) {
        if (mApp.mWebSocketSendRequest != null) {
            if (mApp.mWebSocketSendRequest.isClosing() || (mApp.mWebSocketSendRequest.isClosed())) {
                Log.w("Message", "Closed >>> ");
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
                Log.w("Message", "Sent >>> " + jMessage.toString());
            } else if (mApp.mWebSocketSendRequest.isOpen()) {
                mApp.mWebSocketSendRequest.send(jMessage.toString());

            } else {
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
            }

            if (requestType.equals("1003")) {
                if (timer != null)
                    timer.cancel();

                if (BG != null && BG.isPlaying()) {
                    BG.stop();
                    BG.release();
                    BG = null;
                    vibration.cancel();
                }
                if (!commonMethods.isTableExists("UserRides")) {
                    commonMethods.createTableRides("UserRides");
                    Gson gson = new Gson();
                    selectedUser.setIs_new_request(false);
                    commonMethods.insertIntoTableRides("UserRides", selectedUser.getmUserId(), gson.toJson(selectedUser));
                } else {
                    Gson gson = new Gson();
                    selectedUser.setIs_new_request(false);
                    commonMethods.updateTableRides("UserRides", selectedUser.getmUserId(), gson.toJson(selectedUser));
                }
                if (!commonMethods.isTableExists("Riders")) {
                    commonMethods.createTableRiders("Riders");
                }
                Gson gson = new Gson();
                acceptRider.getToRider().setIs_driver("1");
                acceptRider.getFromRider().setIs_new_request(true);
                acceptRider.setDriver_end_lati(fromUserInfo.getDriver_end_lati());
                acceptRider.setDriver_end_long(fromUserInfo.getDriver_end_long());
                commonMethods.insertIntoTableRiders("Riders", selectedUser.getmUserId(), gson.toJson(acceptRider));
                pickuplocation = new LatLng(Double.parseDouble(acceptRider.getStart_lati()), Double.parseDouble(acceptRider.getStart_long()));
                droppfflocation = new LatLng(Double.parseDouble(acceptRider.getEnd_lati()), Double.parseDouble(acceptRider.getEnd_long()));
                requestList2 = commonMethods.fetchRides("UserRides", false);
                Intent i = new Intent(activity, StartRideActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("rideobj", acceptRider);
                startActivity(i);
                activity.finish();
                openMapDirection();
            } else {
                if (commonMethods.isTableExists("UserRides")) {
                    commonMethods.deleteRideRecord("UserRides", selectedUser.getmUserId());
                }
                mAdapter.updateData(selectedUser, 1);

            }

        }
    }

    // Get the Current Route Request Data
    private void currentRoute() {

        LatLng picklng, droplng;
        String strpicklng, strdroplng;
        if (fromUserInfo == null) {
            User user = commonMethods.fetchUserRide("UserRides");
            strpicklng = user.getStart_lat() + "'" + user.getStart_long();
            strdroplng = user.getEnd_lat() + "'" + user.getEnd_long();
            fromUserInfo = commonMethods.fetchRides("UserRides", true).get(0);
            /*picklng = new LatLng(Double.parseDouble(user.getStart_lat()), Double.parseDouble(user.getStart_long()));
            droplng = new LatLng(Double.parseDouble(user.getEnd_lat()), Double.parseDouble(user.getEnd_long()));*/
            picklng = new LatLng(Double.parseDouble(user.getDriver_start_lati()), Double.parseDouble(user.getDriver_start_long()));
            droplng = new LatLng(Double.parseDouble(user.getDriver_end_lati()), Double.parseDouble(user.getDriver_end_long()));
        } else {
            strpicklng = fromUserInfo.getStart_lat() + "'" + fromUserInfo.getStart_long();
            strdroplng = fromUserInfo.getEnd_lat() + "'" + fromUserInfo.getEnd_long();

            /*picklng = new LatLng(Double.parseDouble(fromUserInfo.getStart_lat()), Double.parseDouble(fromUserInfo.getStart_long()));
            droplng = new LatLng(Double.parseDouble(fromUserInfo.getEnd_lat()), Double.parseDouble(fromUserInfo.getEnd_long()));*/

            picklng = new LatLng(Double.parseDouble(fromUserInfo.getDriver_start_lati()), Double.parseDouble(fromUserInfo.getDriver_start_long()));
            droplng = new LatLng(Double.parseDouble(fromUserInfo.getDriver_end_lati()), Double.parseDouble(fromUserInfo.getDriver_end_long()));

            LatLng pickuplat = new LatLng(Double.parseDouble(fromUserInfo.getStart_lat()), Double.parseDouble(fromUserInfo.getStart_long()));
            LatLng dropplat = new LatLng(Double.parseDouble(fromUserInfo.getEnd_lat()), Double.parseDouble(fromUserInfo.getEnd_long()));

            fromUserInfo.setReq_source(pickuplat);
            fromUserInfo.setReq_des(dropplat);
            mListReqTemp.add(fromUserInfo);

            isDirectionApiCalling = true;
        }
        PrefUtils.putString("picklng", strpicklng);
        PrefUtils.putString("droplng", strdroplng);

        MapDirectionAPI.getDirection(picklng, droplng, activity).enqueue(getRoutePolygonsCallback);
    }

    // Get the  Routes From the MAP
    private void getRoutes(final String json) {

        runOnUiThread(() -> {
            try {
                Directions directions = new Directions(activity);
                routes = directions.parse(json);
                calculateDriver_Distance();

                //calculateTime_Distance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Check the New Route is inBetween the Current Route
    private void checkRideRoute(final String json) {

        runOnUiThread(() -> {
            try {
                Directions directions = new Directions(activity);
                new_routes = directions.parse(json);
                if (!checkRoute(new_routes.get(0).getOverviewPolyLine())) {

                    // Add the new Request
                    if (timer != null)
                        timer.cancel();
                    startTimer();
                    calculateDriver_Distance();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Check the Route is inBetween the Current Route Radius
    private boolean checkRoute(List<LatLng> newRoute) {

        boolean exceededTolerance = false;
        List<LatLng> gpsPoints = routes.get(0).getOverviewPolyLine();
        int size = gpsPoints.size() < newRoute.size() ? gpsPoints.size() : newRoute.size();

       /* String destination1 = toUserInfo.getEnd_lat() + "'" + toUserInfo.getEnd_long();
        String destination2 = newFromUser.getEnd_lat() + "'" + newFromUser.getEnd_long();
        String finalDestination = gpsPoints.size() > newRoute.size() ? destination1 : destination2;*/


        PrefUtils.putString("finalDestination", AppUtils.getAddress(activity, Double.parseDouble(fromUserInfo.getDriver_end_lati()),
                Double.parseDouble(fromUserInfo.getDriver_end_long())));
        for (int i = 0; i < size; i++) {
            if (AppUtils.getDistance(gpsPoints.get(i), newRoute.get(i)) > 1000) {
                exceededTolerance = true;
                break;
            }
        }
        if (exceededTolerance) {
            sendCancelSocketRideRequest(newFromUser == null ? fromUserInfo.getmUserId() : newFromUser.getmUserId());
            Toast.makeText(activity, "Route Not In the Current Ride !!", Toast.LENGTH_LONG).show();
            Log.w("", "User deviated from path");
        } else {
            Toast.makeText(activity, "Route Connected !!", Toast.LENGTH_LONG).show();
            Log.w("", "User is in between path");
        }

        return exceededTolerance;
    }

    // Calculate Driver Reach Time for Rider's Source Location
    private void calculateDriver_Distance() {
        LatLng startLat;
        LatLng endLat;
        if (newFromUser != null) {
            startLat = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
            endLat = new LatLng(Double.parseDouble(newFromUser.getStart_lat()), Double.parseDouble(newFromUser.getStart_long()));
            mProgressDialog.show();


            MapDirectionAPI.getDirection(startLat, endLat, activity).enqueue(driverRouteTimeCallback);
        } else if (fromUserInfo != null) {
            startLat = new LatLng(RideShareApp.mLocation.getLatitude(), RideShareApp.mLocation.getLongitude());
            endLat = new LatLng(Double.parseDouble(fromUserInfo.getStart_lat()), Double.parseDouble(fromUserInfo.getStart_long()));
            mProgressDialog.show();


            MapDirectionAPI.getDirection(startLat, endLat, activity).enqueue(driverRouteTimeCallback);
        } else {
            setAdapter();
        }
    }

    // Calculate Ride Additional Time & Distance for Finishing the Ride
    private void calculateAdditionalTime_Distance() {
        LatLng startLat;
        LatLng endLat;
        if (newFromUser != null) {
            startLat = new LatLng(Double.parseDouble(newFromUser.getStart_lat()), Double.parseDouble(newFromUser.getStart_long()));
            //startLat = new LatLng(Double.parseDouble(fromUserInfo.getEnd_lat()), Double.parseDouble(fromUserInfo.getEnd_long()));
            endLat = new LatLng(Double.parseDouble(newFromUser.getEnd_lat()), Double.parseDouble(newFromUser.getEnd_long()));
        } else {
            startLat = new LatLng(Double.parseDouble(fromUserInfo.getStart_lat()), Double.parseDouble(fromUserInfo.getStart_long()));
            endLat = new LatLng(Double.parseDouble(fromUserInfo.getEnd_lat()), Double.parseDouble(fromUserInfo.getEnd_long()));
        }
        MapDirectionAPI.getDirection(startLat, endLat, activity).enqueue(additionalTimeCallback);
    }

    private void setAdapter() {
        mAdapter = new RequestListAdapter(activity, requestList, mApp);
        list_requests.setAdapter(mAdapter);

        mAdapter.OnFinished(new OnFinishViewListener() {
            @Override
            public void onAccepted(int pos) {
                if (requestList.size() > 0) {
                    User selUser = requestList.get(pos);
                    String strpicklng = selUser.getStart_lat() + "'" + selUser.getStart_long();
                    String strdroplng = selUser.getEnd_lat() + "'" + selUser.getEnd_long();
                    PrefUtils.putString("picklng", strpicklng);
                    PrefUtils.putString("droplng", strdroplng);
                    acceptOrRejectRequest(selUser, "1");
                }
            }

            @Override
            public void onRejected(int pos) {
                // Send Socket Accept or Reject Socket Notification from Driver to Rider
                User selUser = requestList.get(pos);
                acceptOrRejectRequest(selUser, "0");
            }

            @Override
            public void onFinished() {
                list_requests = null;
                if (timer != null)
                    timer.cancel();

                if (BG != null && BG.isPlaying()) {
                    BG.stop();
                    BG.release();
                    BG = null;
                    vibration.cancel();
                }
                finish();
            }


        });
    }

    private void sendCancelSocketRideRequest(String userid) {

        try {
            final JSONObject jMessage = new JSONObject();
            jMessage.put("chat_message", "Ride in not in Current Route");
            jMessage.put("chat_user", userid);
            jMessage.put("sender_user", "1009");
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
                        if (requestList.size() == 1 && userid.equals(requestList.get(0).getmUserId())) {
                            if (timer != null)
                                timer.cancel();
                            pulsator.stop();
                            if (BG != null && BG.isPlaying()) {
                                BG.stop();
                                BG.release();
                                BG = null;
                                vibration.cancel();
                            }
                            finish();
                        }
                    }
                });
            } else {
                if (mApp.mWebSocketSendRequest.isOpen()) {
                    mApp.mWebSocketSendRequest.send(jMessage.toString());
                    if (requestList.size() == 1 && userid.equals(requestList.get(0).getmUserId())) {
                        if (timer != null)
                            timer.cancel();
                        pulsator.stop();
                        if (BG != null && BG.isPlaying()) {
                            BG.stop();
                            BG.release();
                            BG = null;
                            vibration.cancel();
                        }
                        finish();
                    }
                }
            }
            Log.w("Message", "Sent Requests>>> " + jMessage.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            if (BG != null && BG.isPlaying()) {
                BG.stop();
                BG.release();
                BG = null;
                vibration.cancel();
            }
            finish();
        }
    }

    private void openMapDirection() {
        if (AppUtils.isInternetAvailable(activity)) {
            try {
                String srcAdd;
                String desAdd;
                StringBuilder wayPoints = new StringBuilder();
                if (requestList2.size() > 0) {
                    //srcAdd = "&origin=" + markerPoints.get(0).latitude + "," + markerPoints.get(0).longitude;
                    srcAdd = "&origin=" + currentlthg.latitude + "," + currentlthg.longitude;

                    wayPoints.insert(0, "&waypoints=");

                    ArrayList<ToSort> startPointList = getShortestStartPointDistances();
                    ArrayList<ToSort> endPointList = getShortestEndPointDistances();

                    for (int i = 0; i < requestList2.size(); i++) {
                        int id = Integer.parseInt(startPointList.get(i).getId());
                        LatLng startPoint = new LatLng(Double.parseDouble(requestList2.get(id).getStart_lat()), Double.parseDouble(requestList2.get(id).getStart_long()));
                        wayPoints.append(startPoint.latitude).append(",").append(startPoint.longitude).append("|");
                    }

                    for (int i = 0; i < requestList2.size(); i++) {
                        int id = Integer.parseInt(endPointList.get(i).getId());
                        LatLng endPoint = new LatLng(Double.parseDouble(requestList2.get(id).getEnd_lat()), Double.parseDouble(requestList2.get(id).getEnd_long()));
                        wayPoints.append(endPoint.latitude).append(",").append(endPoint.longitude);
                        if (i != requestList2.size() - 1) {
                            wayPoints.append("|");
                        }
                    }
                    int id_dest = Integer.parseInt(endPointList.get(endPointList.size() - 1).getId());
                    /* LatLng finalDestination = new LatLng(Double.parseDouble(requestList2.get(id_dest).getEnd_lat()), Double.parseDouble(requestList2.get(id_dest).getEnd_long()));*/
                    desAdd = "&destination=" + fromUserInfo.getDriver_end_lati() + "," + fromUserInfo.getDriver_end_long();
                } else {
                    srcAdd = "&origin=" + pickuplocation.latitude + "," + pickuplocation.longitude;
                    desAdd = "&destination=" + fromUserInfo.getDriver_end_lati() + "," + fromUserInfo.getDriver_end_long();
                }


                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate" + srcAdd + desAdd + wayPoints));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&travelmode=driving" + srcAdd + desAdd + wayPoints));
                intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }


        } else {
            MessageUtils.showNoInternetAvailable(activity);
        }
    }

    private ArrayList<ToSort> getShortestStartPointDistances() {
        ArrayList<ToSort> distances = new ArrayList<>();
        for (int i = 0; i < requestList2.size(); i++) {
            distances.add(new ToSort(Double.parseDouble(requestList2.get(i).getDriver_pickup_distance()), "" + i));
        }
        Collections.sort(distances);

        return distances;
    }

    private ArrayList<ToSort> getShortestEndPointDistances() {
        ArrayList<ToSort> distances2 = new ArrayList<>();
        User rider2 = requestList2.get(getShortestStartPointDistances().size() - 1);
        for (int i = 0; i < requestList2.size(); i++) {
            distances2.add(new ToSort(AppUtils.distance(Double.valueOf(rider2.getStart_lat()), Double.valueOf(rider2.getStart_long()), Double.valueOf(requestList2.get(i).getEnd_lat()), Double.valueOf(requestList2.get(i).getEnd_long())), "" + i));
            //distances.add(new ToSort(Double.parseDouble(requestList2.get(i).getRide_distance()), "" + i));
        }
        Collections.sort(distances2);

        return distances2;
    }

}
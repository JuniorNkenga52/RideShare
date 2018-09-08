package com.app.rideshare.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.HomeNewActivity;
import com.app.rideshare.activity.MyGroupSelectionActivity;
import com.app.rideshare.activity.SignUpActivity;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.app.rideshare.widget.PinEntryEditText;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPFragment extends Fragment {

    static Context context;

    private ImageView imgBack;
    private TextView txtNext;
    static TextView txtPhoneNumberInfo;

    private PinEntryEditText txtPin;
    private TextView txtResendOTP;

    //CustomProgressDialog mProgressDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SmsVerifyCatcher smsVerifyCatcher;
    //String token;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_otp, container, false);

        context = getActivity();

        PrefUtils.initPreference(getActivity());

        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(0);
            }
        });

        txtPhoneNumberInfo = (TextView) rootView.findViewById(R.id.txtPhoneNumberInfo);
        txtPhoneNumberInfo.setText(getActivity().getResources().getString(R.string.txt_enter_the_code) + " " + SignUpActivity.PhoneNumber);

        txtPin = (PinEntryEditText) rootView.findViewById(R.id.txtPin);

        txtResendOTP = (TextView) rootView.findViewById(R.id.txtResendOTP);

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtils.isInternetAvailable(getActivity())) {
                    sendOTP("+" + AppUtils.getCountryTelephoneCode(context) + SignUpActivity.PhoneNumber, SignUpActivity.mUserId);
                    //sendOTP("+1" +SignUpActivity.PhoneNumber, SignUpActivity.mUserId);
                    //sendOTP("+91" +SignUpActivity.PhoneNumber, SignUpActivity.mUserId);
                } else {
                    MessageUtils.showNoInternetAvailable(getActivity());
                }
            }
        });

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = txtPin.getText().toString();

                if (otp.length() == 0) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter verification code.");
                } else if (otp.length() != 5) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter valid verification code.");
                } else {
                    //sendOTP(otp, MobileNumberFragment.mUserId);
                    if (AppUtils.isInternetAvailable(getActivity())) {
                        new AsyncOTP(otp).execute();
                    } else {
                        MessageUtils.showNoInternetAvailable(getActivity());
                    }
                }
                //SignUpActivity.mViewPager.setCurrentItem(2);

            }
        });

        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {

            @Override
            public void onSmsCatch(String message) {
                if (message.contains("RideWhiz")) {

                    final String code = AppUtils.parseOTPFromSms(message);

                    txtPin.setText(code);

                    if (AppUtils.isInternetAvailable(getActivity()))
                        new AsyncOTP(code).execute();
                }
            }
        });

        smsVerifyCatcher.onStart();

       /* mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    token = intent.getStringExtra("token");
                    Log.d("token", token);
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                } else {
                }
            }
        };*/
        return rootView;
    }

    /* @Override
    protected void onResume() {
        super.onResume();

        FirstName = "";
        LastName = "";
        HomeAddress = "";
        EmailId = "";

        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Called",">>>>>>>");
    }

    @Override
    public void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    public static void updateTest() {
        String number = "(" + SignUpActivity.PhoneNumber.substring(0, 3) + ") " + SignUpActivity.PhoneNumber.substring(3, 6) + "-" + SignUpActivity.PhoneNumber.substring(6, 10);
        txtPhoneNumberInfo.setText(context.getResources().getString(R.string.txt_enter_the_code) + " " + number);
    }

    public class AsyncOTP extends AsyncTask<Object, Integer, Object> {

        String OTP;
        CustomProgressDialog mProgressDialog;

        public AsyncOTP(String OTP) {

            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.show();

            this.OTP = OTP;
            /*if(token.equals("")){
                PrefUtils.getString("tokenID");
            }*/
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                //return RideShareApi.verifyOTP(OTP, SignUpActivity.mUserId, SignUpActivity.token);
                return RideShareApi.verifyOTP(OTP, SignUpActivity.mUserId, PrefUtils.getString("TokenID"),getContext());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    if (jsonObject.getString("result").equalsIgnoreCase("Your mobile number successfully verified")) {
                        SignUpActivity.mViewPager.setCurrentItem(2);

                    } else {
                        PrefUtils.putString("loginwith", "normal");
                        JSONArray jArrayResult = new JSONArray(jsonObject.getString("result"));

                        JSONObject jObjResult = jArrayResult.getJSONObject(0);

                        User beanUser = new User();

                        beanUser.setmUserId(jObjResult.getString("u_id"));
                        beanUser.setmGroup_id(jObjResult.getString("group_id"));
                        beanUser.setmFirstName(jObjResult.getString("u_firstname"));
                        beanUser.setmLastName(jObjResult.getString("u_lastname"));
                        beanUser.setmEmail(jObjResult.getString("u_email"));
                        beanUser.setmDescription(jObjResult.getString("description"));
                        beanUser.setmAddress(jObjResult.getString("address"));
                        beanUser.setProfile_image(jObjResult.getString("profile_image"));
                        beanUser.setmMobileNo(jObjResult.getString("u_mo_number"));
                        beanUser.setmLatitude(jObjResult.getString("u_lat"));
                        beanUser.setmLongitude(jObjResult.getString("u_long"));
                        beanUser.setMu_type(jObjResult.getString("u_type"));
                        beanUser.setMtoken(jObjResult.getString("token"));
                        beanUser.setmMobileNumber(jObjResult.getString("mobile_verify_number"));
                        beanUser.setmIsVerify(jObjResult.getString("verify_mobile"));
                        beanUser.setmRideType(jObjResult.getString("u_ride_type"));
                        beanUser.setmStatus(jObjResult.getString("u_status"));
                        beanUser.setmRidestatus(jObjResult.getString("ride_status"));
                        beanUser.setContact_sync(jObjResult.getString("contact_sync"));
                        beanUser.setmIs_rider(jObjResult.getString("is_rider"));
                        beanUser.setmUpdatedDate(jObjResult.getString("update_date"));
                        beanUser.setmCreatedDate(jObjResult.getString("create_date"));

                        beanUser.setMvehicle_model(jObjResult.optString("vehicle_model"));
                        beanUser.setMvehicle_type(jObjResult.optString("vehicle_type"));
                        beanUser.setmMax_passengers(jObjResult.optString("max_passengers"));
                        beanUser.setM_is_assigned_group(jObjResult.optString("is_assigned_group"));

                        PrefUtils.addUserInfo(beanUser);

                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putString("loginwith", "normal");

                        smsVerifyCatcher.onStop();


                        if (beanUser.getM_is_assigned_group().equals("1")) {

                            PrefUtils.putString("isBlank", "false");
                            Intent i = new Intent(context, MyGroupSelectionActivity.class);
                            startActivity(i);
                            getActivity().finish();
                            /*Intent i = new Intent(getActivity(), RideTypeActivity.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();*/
                        } else {
                            /*Intent i = new Intent(getActivity(), GroupSelectionFragment.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();*/
                            PrefUtils.putString("isBlank", "true");
                            Intent i = new Intent(getActivity(), HomeNewActivity.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();
                        }


                        /*if (PrefUtils.getBoolean("firstTime")) {
                            Intent i = new Intent(getActivity(), RideTypeActivity.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();
                        } else {
                            Intent i = new Intent(getActivity(), GroupSelectionFragment.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();

                        }*/
                        /*Intent i = new Intent(getActivity(), GroupSelectionFragment.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();
                        PrefUtils.putBoolean("firstTime", true);*/

                    }
                } else {
                    MessageUtils.showPleaseTryAgain(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.showPleaseTryAgain(context);
            }
        }
    }

    private void sendOTP(final String mobileNuber, String nUserId) {
        final CustomProgressDialog mProgressDialog = new CustomProgressDialog(getActivity());
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class,context).sendOTP(mobileNuber, nUserId).enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageUtils.showSuccessMessage(getActivity(), "OTP Sent");
                } else {
                    MessageUtils.showPleaseTryAgain(getActivity());
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
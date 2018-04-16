package com.app.rideshare.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.HomeNewActivity;
import com.app.rideshare.activity.RideTypeActivity;
import com.app.rideshare.activity.SignUpActivity;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.app.rideshare.widget.PinEntryEditText;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_otp, container,
                false);

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
        txtPhoneNumberInfo.setText(getActivity().getResources().getString(R.string.txt_enter_the_code) +  " "+SignUpActivity.PhoneNumber);

        txtPin = (PinEntryEditText) rootView.findViewById(R.id.txtPin);

        txtPin.setText("");

        txtResendOTP = (TextView) rootView.findViewById(R.id.txtResendOTP);

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP(SignUpActivity.PhoneNumber, SignUpActivity.mUserId);
            }
        });

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = txtPin.getText().toString();

                if(otp.length() == 0){
                    ToastUtils.showShort(getActivity(), "Please enter verification code.");
                } else if(otp.length() != 5){
                    ToastUtils.showShort(getActivity(), "Please enter valid verification code.");
                } else {
                    //sendOTP(otp, MobileNumberFragment.mUserId);
                    new AsyncOTP(otp).execute();
                }

                //SignUpActivity.mViewPager.setCurrentItem(2);

            }
        });

        return rootView;
    }

    public static void updateTest(){

        String number = "(" + SignUpActivity.PhoneNumber.substring(0,3) + ") " + SignUpActivity.PhoneNumber.substring(3,6) + "-" + SignUpActivity.PhoneNumber.substring(6,10);

        txtPhoneNumberInfo.setText(context.getResources().getString(R.string.txt_enter_the_code) +  " "+number);
    }

    public class AsyncOTP extends AsyncTask<Object, Integer, Object> {

        String OTP;
        CustomProgressDialog mProgressDialog;

        public AsyncOTP(String OTP) {

            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.show();

            this.OTP = OTP;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.verifyOTP(OTP, SignUpActivity.mUserId, SignUpActivity.token);
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

                if(jsonObject.getString("status").equalsIgnoreCase("success")) {
                    if(jsonObject.getString("result").equalsIgnoreCase("Your mobile number successfully verified"))
                        SignUpActivity.mViewPager.setCurrentItem(2);
                    else {

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

                        PrefUtils.addUserInfo(beanUser);

                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putString("loginwith", "normal");

                        Intent i = new Intent(getActivity(), RideTypeActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();
                    }
                } else {
                    ToastUtils.showShort(getContext(), "Please try againg..");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort(getContext(), "Please try againg..");
            }
        }
    }

    private void sendOTP(final String mobileNuber, String nUserId) {
        final CustomProgressDialog mProgressDialog = new CustomProgressDialog(getActivity());
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).sendOTP(mobileNuber, nUserId).enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ToastUtils.showShort(getActivity(), "OTP Sent");
                } else {
                    ToastUtils.showShort(getActivity(), "Please try againg..");
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
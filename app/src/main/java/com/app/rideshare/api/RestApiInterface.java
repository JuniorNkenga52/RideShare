package com.app.rideshare.api;

import com.app.rideshare.api.request.ContactRequest;
import com.app.rideshare.api.response.ContactResponse;
import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.api.response.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestApiInterface {

    @FormUrlEncoded
    @POST("auth/register")
    Call<SignupResponse> signup(@Field("u_firstname") String mFirstName, @Field("u_lastname") String mLastName, @Field("u_email") String mEmail, @Field("u_mo_number") String mMobileNuber, @Field("u_password") String mPassword,@Field("deviceToken") String deviceTocken);


    @FormUrlEncoded
    @POST("auth/login")
    Call<SignupResponse> login(@Field("u_email") String mEmail, @Field("u_password") String mPassword);

    @FormUrlEncoded
    @POST("auth/select_ride")
    Call<RideSelect> selectRide(@Field("u_id") String mId, @Field("u_ride_type") String mType, @Field("u_lat") String mLatitude, @Field("u_long") String mLongitude);


    @FormUrlEncoded
    @POST("auth/facebook_register")
    Call<SignupResponse> signfacebook(@Field("facebook_id") String mFacebookId, @Field("u_email") String mEmail, @Field("u_firstname") String mFirstName, @Field("u_lastname") String mLastName);


    @FormUrlEncoded
    @POST("auth/google_register")
    Call<SignupResponse> signGoogleplus(@Field("google_id") String mGoogleId, @Field("u_email") String mEmail, @Field("u_firstname") String mFirstName, @Field("u_lastname") String mLastName);


    @FormUrlEncoded
    @POST("user/sendTextMessage")
    Call<SendOTPResponse> sendOTP(@Field("mobile_number") String mMobileNuber, @Field("user_id") String mUserId);


    @FormUrlEncoded
    @POST("user/verifyMobile")
    Call<SendOTPResponse> verifyOTP(@Field("user_id") String mUserId, @Field("token_number") String otp);


    @POST("user/addUserContacts")
    Call<ContactResponse> syncContact(@Body ContactRequest user);
}

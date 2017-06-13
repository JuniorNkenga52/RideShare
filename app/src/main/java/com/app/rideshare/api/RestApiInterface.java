package com.app.rideshare.api;

import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.api.response.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApiInterface {

    @FormUrlEncoded
    @POST("auth/register")
    Call<SignupResponse> signup(@Field("u_firstname") String mFirstName,@Field("u_lastname") String mLastName,@Field("u_email") String mEmail,@Field("u_mo_number") String mMobileNuber,@Field("u_password") String mPassword);


    @FormUrlEncoded
    @POST("auth/login")
    Call<SignupResponse> login(@Field("u_email") String mEmail,@Field("u_password") String mPassword);

    @FormUrlEncoded
    @POST("auth/select_ride")
    Call<RideSelect> selectRide(@Field("u_id") String mId, @Field("u_ride_type") String mType, @Field("u_lat") String mLatitude, @Field("u_long") String mLongitude);


}

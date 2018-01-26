package com.app.rideshare.api;

import com.app.rideshare.api.request.ContactRequest;
import com.app.rideshare.api.response.AcceptRequest;
import com.app.rideshare.api.response.CancelRequest;
import com.app.rideshare.api.response.ContactResponse;
import com.app.rideshare.api.response.GroupListResponce;
import com.app.rideshare.api.response.GroupResponce;
import com.app.rideshare.api.response.HistoryResponse;
import com.app.rideshare.api.response.MyGroupsResponce;
import com.app.rideshare.api.response.RateRideResponce;
import com.app.rideshare.api.response.RideSelect;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.api.response.SendResponse;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.api.response.StartRideResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RestApiInterface {


    MediaType MULTIPART = MediaType.parse("multipart/form-data");

    @FormUrlEncoded
    @POST("auth/register")
    Call<SignupResponse> signup(@Field("u_firstname") String mFirstName, @Field("u_lastname") String mLastName, @Field("u_email") String mEmail, @Field("u_mo_number") String mMobileNuber, @Field("u_password") String mPassword, @Field("deviceToken") String deviceTocken);

    @FormUrlEncoded
    @POST("auth/forgotpassword")
    Call<SignupResponse> forgotpassword(@Field("u_email") String mU_email);


    @FormUrlEncoded
    @POST("auth/login")
    Call<SignupResponse> login(@Field("u_email") String mEmail, @Field("u_password") String mPassword, @Field("deviceToken") String deviceTocken, @Field("group_id") String mGroup_id);

    @FormUrlEncoded
    @POST("group/create")
    Call<GroupResponce> creategroup(@Field("group_name") String group_name, @Field("email") String email);


    @POST("group")
    Call<GroupListResponce> getgrouplist();

    @FormUrlEncoded
    @POST("user/mygroups")
    Call<GroupListResponce> mygroups(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user/groupusers")
    Call<MyGroupsResponce> groupusers(@Field("group_id") String group_id, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user/updateAdminFunction")
    Call<MyGroupsResponce> updateAdminFunction(@Field("user_id") String user_id, @Field("driver_request") String d_req, @Field("user_disable") String user_disable, @Field("group_id") String group_id, @Field("is_admin") String is_admin);

    @FormUrlEncoded
    @POST("auth/select_ride")
    Call<RideSelect> selectRide(@Field("u_id") String mId, @Field("u_ride_type") String mType, @Field("u_lat") String mLatitude, @Field("u_long") String mLongitude);


    @FormUrlEncoded
    @POST("user/getUserContacts")
    Call<RideSelect> getUser(@Field("user_id") String mId, @Field("friend_list_type") String mType, @Field("u_lat") String mLatitude, @Field("u_long") String mLongitude, @Field("u_ride_type") String mRideType);

    @FormUrlEncoded
    @POST("auth/facebook_register")
    Call<SignupResponse> signfacebook(@Field("facebook_id") String mFacebookId, @Field("u_email") String mEmail, @Field("u_firstname") String mFirstName, @Field("u_lastname") String mLastName, @Field("deviceToken") String deviceTocken, @Field("group_id") String group_id);


    @FormUrlEncoded
    @POST("auth/google_register")
    Call<SignupResponse> signGoogleplus(@Field("google_id") String mGoogleId, @Field("u_email") String mEmail, @Field("u_firstname") String mFirstName, @Field("u_lastname") String mLastName, @Field("deviceToken") String deviceTocken, @Field("group_id") String group_id);


    @FormUrlEncoded
    @POST("user/sendTextMessage")
    Call<SendOTPResponse> sendOTP(@Field("mobile_number") String mMobileNuber, @Field("user_id") String mUserId);


    @FormUrlEncoded
    @POST("user/verifyMobile")
    Call<SendOTPResponse> verifyOTP(@Field("user_id") String mUserId, @Field("token_number") String otp);


    @POST("user/addUserContacts")
    Call<ContactResponse> syncContact(@Body ContactRequest user);

    @FormUrlEncoded
    @POST("user/sendRequest")
    Call<SendResponse> sendRequest(@Field("user_id") String mUserId, @Field("from_id") String mFromUserId,
                                   @Field("start_latitude") String startlatitude, @Field("start_longitude") String startLongitude,
                                   @Field("end_latitude") String endlatitude, @Field("end_longitude") String endlongitude,
                                   @Field("u_ride_type") String mtype, @Field("start_address") String mStartAddress, @Field("end_address") String mEndAddress, @Field("group_id") String mGroup_id);

    @FormUrlEncoded
    @POST("user/acceptDeclineRequest")
    Call<AcceptRequest> acceptRequest(@Field("ride_id") String mRideId, @Field("request_status") String mRequestStatus);


    @FormUrlEncoded
    @POST("user/cancelRide")
    Call<CancelRequest> cancelRequest(@Field("ride_id") String mRideId);

    @FormUrlEncoded
    @POST("user/getRideHistory")
    Call<HistoryResponse> getHistory(@Field("user_id") String mUserId);


    @Multipart
    @POST("user/updateProfile")
    Call<SignupResponse> updateProfile(@Part("user_id") RequestBody mUserId, @Part("u_firstname") RequestBody Firstname, @Part("u_lastname") RequestBody lastName
            , @Part("u_mobile") RequestBody mobilenumber, @Part MultipartBody.Part file, @Part("u_email") RequestBody mEmail, @Part("vehicle_model") RequestBody mVh_Model, @Part("vehicle_type") RequestBody mVh_Type, @Part("max_passengers") RequestBody mMax_Passengers, @Part("requested_as_driver") RequestBody mReq_driver, @Part("group_id") RequestBody mgroup_id);

    @FormUrlEncoded
    @POST("user/startOrEndRide")
    Call<StartRideResponse> mStartRide(@Field("ride_id") String mRideId, @Field("ride_status") String mRideStatus, @Field("user_id") String mUserId);

    @FormUrlEncoded
    @POST("user/rideRate")
    Call<RateRideResponce> rateride(@Field("driver_id") String mDriver_id,@Field("rider_id") String mRider_id, @Field("ride_id") String mRideId, @Field("ride_rate") String mRideRate, @Field("ride_review") String mRideReview);

}

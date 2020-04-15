package com.app.rideWhiz.api;

import com.app.rideWhiz.api.request.ContactRequest;
import com.app.rideWhiz.api.response.AcceptRequest;
import com.app.rideWhiz.api.response.CancelRequest;
import com.app.rideWhiz.api.response.ContactResponse;
import com.app.rideWhiz.api.response.GroupListResponce;
import com.app.rideWhiz.api.response.GroupResponce;
import com.app.rideWhiz.api.response.HistoryResponse;
import com.app.rideWhiz.api.response.ManageCarResponce;
import com.app.rideWhiz.api.response.MyGroupsResponce;
import com.app.rideWhiz.api.response.RateRideResponce;
import com.app.rideWhiz.api.response.RideSelect;
import com.app.rideWhiz.api.response.SendOTPResponse;
import com.app.rideWhiz.api.response.SendResponse;
import com.app.rideWhiz.api.response.SignupResponse;
import com.app.rideWhiz.api.response.StartRideResponse;
import com.app.rideWhiz.api.response.UpdateDestinationAddress;
import com.app.rideWhiz.model.DriverReviewResponse;
import com.app.rideWhiz.model.DriverReviews;
import com.app.rideWhiz.model.User;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

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
    @POST("user/getUserDetails")
    Call<SignupResponse> getUserDetails(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user/updateUserStatus")
    Call<JsonObject> updateUserStatus(@Field("user_id[]") ArrayList<String> user_id_list, @Field("status") String status);

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
    @POST("user/updateDestinationAddress")
    Call<UpdateDestinationAddress> updateDestinationAddress(@Field("user_id") String user_id, @Field("destination_address") String destination_address);

    @FormUrlEncoded
    @POST("user/getUserContacts")
    Call<RideSelect> getUser(@Field("user_id") String mId, @Field("friend_list_type") String mType, @Field("u_lat") String mLatitude, @Field("u_long") String mLongitude, @Field("u_ride_type") String mRideType, @Field("group_id") String mgroup_id);

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
                                   @Field("u_ride_type") String mtype, @Field("start_address") String mStartAddress,
                                   @Field("end_address") String mEndAddress, @Field("group_id") String mGroup_id, @Field("ride_id") String mride_id, @Field("no_of_seats") String no_of_seats);

    @FormUrlEncoded
    @POST("user/acceptDeclineRequest")
    Call<AcceptRequest> acceptRequest(@Field("ride_id") String mRideId, @Field("request_status") String mRequestStatus);


    @FormUrlEncoded
    @POST("user/declineRequestNotification")
    Call<AcceptRequest> declineRequestNotification(@Field("user_id") String user_id, @Field("ride_id") String mRideId, @Field("request_status") String mRequestStatus, @Field("is_driver") String is_driver);


    @FormUrlEncoded
    @POST("user/cancelRide")
    Call<CancelRequest> cancelRequest(@Field("ride_id") String mRideId);

    @FormUrlEncoded
    @POST("user/getRideHistory")
    Call<HistoryResponse> getHistory(@Field("user_id") String mUserId);

    @Multipart
    @POST("user/updateProfile")
    Call<SignupResponse> updateProfile(
            @Part("user_id") RequestBody mUserId,
            @Part("u_firstname") RequestBody Firstname,
            @Part("u_lastname") RequestBody lastName,
            @Part("u_mobile") RequestBody mobilenumber,
            @Part MultipartBody.Part file,
            @Part("u_email") RequestBody mEmail,
            @Part("vehicle_model") RequestBody mVh_Model,
            @Part("vehicle_type") RequestBody mVh_Type,
            @Part("max_passengers") RequestBody mMax_Passengers,
            @Part("requested_as_driver") RequestBody mReq_driver,
            @Part("group_id") RequestBody mgroup_id);

    @FormUrlEncoded
    @POST("user/startOrEndRide")
    Call<StartRideResponse> mStartRide(@Field("ride_id") String mRideId, @Field("check_driver") String check_driver, @Field("ride_status") String mRideStatus, @Field("user_id") String mUserId, @Field("end_lati") String endlat, @Field("end_long") String endlong,@Field("u_ride_type") String u_ride_type);

    @FormUrlEncoded
    @POST("user/rideRate")
    Call<RateRideResponce> rateride(@Field("driver_id") String mDriver_id, @Field("rider_id") String mRider_id, @Field("ride_id") String mRideId, @Field("ride_rate") String mRideRate, @Field("ride_review") String mRideReview);

    @FormUrlEncoded
    @POST("user/manageCar")
    Call<ManageCarResponce> manageCar(@Field("user_id") String m_user_id, @Field("car_make") String m_car_make, @Field("car_month") String m_car_month, @Field("car_year") String m_car_year, @Field("license_plate") String m_license_plate, @Field("brand") String m_brand, @Field("car_type") String m_car_type, @Field("seating_capacity") String m_seating_capacity, @Field("car_model") String m_car_model);
    /*
    ec2-13-58-7-10.us-east-2.compute.amazonaws.com/rideshare/api/user/manageCar
    * */

    @FormUrlEncoded
    @POST("user/updateUserLocation")
    Call<User> updateUserLocation(@Field("user_id") String mUserId, @Field("u_lat") String mLatitude, @Field("u_long") String mLongitude);
    //http://php.rlogical.com/rideshare/api/user/updateUserLocation

    // Update User Details
    @FormUrlEncoded
    @POST("user/updateDriverDetails")
    Call<User> updateDriverDetails(@Field("user_id") String mUserId, @Field("driverDetail[no_of_seats]") String no_of_seats);

    // Get Ride ID
    @FormUrlEncoded
    @POST("user/getRideId")
    Call<JsonObject> getRideId(@Field("to_id") String rider_id, @Field("from_id") String user_id);

    // removeDriverFromList for Ghost Riders
    @FormUrlEncoded
    @POST("user/removeDriverFromList")
    Call<JsonObject> removeDriverFromList(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user/updateFriendListType")
    Call<JSONObject> updateFriendListType(@Field("user_id") String mId, @Field("friend_list_type") String mType);

    @FormUrlEncoded
    @POST("user/getDriverRates")
    Call<DriverReviews> getDriverRates(@Field("user_id") String user_id);
}

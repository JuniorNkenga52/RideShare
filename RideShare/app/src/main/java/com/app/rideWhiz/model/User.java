package com.app.rideWhiz.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class User implements Serializable {
    @SerializedName("u_id")
    private String mUserId;
    @SerializedName("u_firstname")
    private String mFirstName;
    @SerializedName("u_lastname")
    private String mLastName;
    @SerializedName("u_email")
    private String mEmail;

    @SerializedName("description")
    private String mDescription;
    @SerializedName("address")
    private String mAddress;

    @SerializedName("u_mo_number")
    private String mMobileNo;
    @SerializedName("u_lat")
    private String mLatitude;
    @SerializedName("u_long")
    private String mLongitude;
    @SerializedName("tokan")
    private String mTocken;
    @SerializedName("u_ride_type")
    private String mRideType;
    @SerializedName("u_status")
    private String mStatus;
    @SerializedName("update_date")
    private String mUpdatedDate;
    @SerializedName("create_date")
    private String mCreatedDate;
    @SerializedName("mobile_verify_number")
    private String mMobileNumber;
    @SerializedName("verify_mobile")
    private String mIsVerify;
    @SerializedName("contact_sync")
    private String contact_sync;

    @SerializedName("ride_status")
    private String mRidestatus;

    private String profile_image;
    private String thumb_image;

    @SerializedName("car_info")
    private CarInfo car_info;

    @SerializedName("group_id")
    private String mGroup_id;


    @SerializedName("is_rider")
    private String mIs_rider;

    @SerializedName("is_driver")
    private String mIs_driver;

    @SerializedName("requested_as_driver")
    private String mrequested_as_driver;

    @SerializedName("is_admin")
    private String mis_admin;

    @SerializedName("vehicle_model")
    private String mvehicle_model;

    @SerializedName("vehicle_type")
    private String mvehicle_type;

    @SerializedName("max_passengers")
    private String mMax_passengers;

    @SerializedName("u_fb_id")
    private String mu_fb_id;

    @SerializedName("u_google_id")
    private String mu_google_id;

    @SerializedName("u_password")
    private String mu_password;

    @SerializedName("u_type")
    private String mu_type;

    @SerializedName("token")
    private String mtoken;

    @SerializedName("is_assigned_group")
    private String m_is_assigned_group;

    private String jabberId;

    private String start_lat;
    private String start_long;

    private String end_lat;
    private String end_long;

    private String start_address;
    private String end_address;

    private String riderID;
    private String request_share_id;

    private String ride_time;
    private String ride_distance;

    private String add_ride_time;
    private String add_ride_distance;

    private boolean is_new_request;

    private LatLng req_source;
    private LatLng req_des;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getMu_fb_id() {
        return mu_fb_id;
    }

    public void setMu_fb_id(String mu_fb_id) {
        this.mu_fb_id = mu_fb_id;
    }

    public String getMu_google_id() {
        return mu_google_id;
    }

    public void setMu_google_id(String mu_google_id) {
        this.mu_google_id = mu_google_id;
    }

    public String getMu_password() {
        return mu_password;
    }

    public void setMu_password(String mu_password) {
        this.mu_password = mu_password;
    }

    public String getMu_type() {
        return mu_type;
    }

    public void setMu_type(String mu_type) {
        this.mu_type = mu_type;
    }

    public String getMtoken() {
        return mtoken;
    }

    public void setMtoken(String mtoken) {
        this.mtoken = mtoken;
    }

    public String getmRidestatus() {
        return mRidestatus;
    }

    public void setmRidestatus(String mRidestatus) {
        this.mRidestatus = mRidestatus;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getContact_sync() {
        return contact_sync;
    }

    public void setContact_sync(String contact_sync) {
        this.contact_sync = contact_sync;
    }

    public String getmMobileNumber() {
        return mMobileNumber;
    }

    public void setmMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public String getmIsVerify() {
        return mIsVerify;
    }

    public void setmIsVerify(String mIsVerify) {
        this.mIsVerify = mIsVerify;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmMobileNo() {
        return mMobileNo;
    }

    public void setmMobileNo(String mMobileNo) {
        this.mMobileNo = mMobileNo;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmTocken() {
        return mTocken;
    }

    public void setmTocken(String mTocken) {
        this.mTocken = mTocken;
    }

    public String getmRideType() {
        return mRideType;
    }

    public void setmRideType(String mRideType) {
        this.mRideType = mRideType;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmUpdatedDate() {
        return mUpdatedDate;
    }

    public void setmUpdatedDate(String mUpdatedDate) {
        this.mUpdatedDate = mUpdatedDate;
    }

    public String getmCreatedDate() {
        return mCreatedDate;
    }

    public void setmCreatedDate(String mCreatedDate) {
        this.mCreatedDate = mCreatedDate;
    }

    public String getmGroup_id() {
        return mGroup_id;
    }

    public void setmGroup_id(String mGroup_id) {
        this.mGroup_id = mGroup_id;
    }

    public String getmIs_rider() {
        return mIs_rider;
    }

    public void setmIs_rider(String mIs_rider) {
        this.mIs_rider = mIs_rider;
    }

    public String getmIs_driver() {
        return mIs_driver;
    }

    public void setmIs_driver(String mIs_driver) {
        this.mIs_driver = mIs_driver;
    }

    public String getMrequested_as_driver() {
        return mrequested_as_driver;
    }

    public void setMrequested_as_driver(String mrequested_as_driver) {
        this.mrequested_as_driver = mrequested_as_driver;
    }

    public String getMis_admin() {
        return mis_admin;
    }

    public void setMis_admin(String mis_admin) {
        this.mis_admin = mis_admin;
    }

    public String getMvehicle_model() {
        return mvehicle_model;
    }

    public void setMvehicle_model(String mvehicle_model) {
        this.mvehicle_model = mvehicle_model;
    }

    public String getMvehicle_type() {
        return mvehicle_type;
    }

    public void setMvehicle_type(String mvehicle_type) {
        this.mvehicle_type = mvehicle_type;
    }

    public String getmMax_passengers() {
        return mMax_passengers;
    }

    public void setmMax_passengers(String mMax_passengers) {
        this.mMax_passengers = mMax_passengers;
    }

    public String getM_is_assigned_group() {
        return m_is_assigned_group;
    }

    public void setM_is_assigned_group(String m_is_assigned_group) {
        this.m_is_assigned_group = m_is_assigned_group;
    }

    public String getStart_lat() {
        return start_lat;
    }

    public void setStart_lat(String start_lat) {
        this.start_lat = start_lat;
    }

    public String getStart_long() {
        return start_long;
    }

    public void setStart_long(String start_long) {
        this.start_long = start_long;
    }

    public String getEnd_lat() {
        return end_lat;
    }

    public void setEnd_lat(String end_lat) {
        this.end_lat = end_lat;
    }

    public String getEnd_long() {
        return end_long;
    }

    public void setEnd_long(String end_long) {
        this.end_long = end_long;
    }

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    public String getRiderID() {
        return riderID;
    }

    public void setRiderID(String riderID) {
        this.riderID = riderID;
    }

    public String getRequest_share_id() {
        return request_share_id;
    }

    public void setRequest_share_id(String request_share_id) {
        this.request_share_id = request_share_id;
    }

    public String getRide_time() {
        return ride_time;
    }

    public void setRide_time(String ride_time) {
        this.ride_time = ride_time;
    }

    public String getRide_distance() {
        return ride_distance;
    }

    public void setRide_distance(String ride_distance) {
        this.ride_distance = ride_distance;
    }

    public String getAdd_ride_time() {
        return add_ride_time;
    }

    public void setAdd_ride_time(String add_ride_time) {
        this.add_ride_time = add_ride_time;
    }

    public String getAdd_ride_distance() {
        return add_ride_distance;
    }

    public void setAdd_ride_distance(String add_ride_distance) {
        this.add_ride_distance = add_ride_distance;
    }

    public boolean getIs_new_request() {
        return is_new_request;
    }

    public void setIs_new_request(boolean is_new_request) {
        this.is_new_request = is_new_request;
    }

    public LatLng getReq_source() {
        return req_source;
    }

    public void setReq_source(LatLng req_source) {
        this.req_source = req_source;
    }

    public LatLng getReq_des() {
        return req_des;
    }

    public void setReq_des(LatLng req_des) {
        this.req_des = req_des;
    }

    public CarInfo getCar_info() {
        return car_info;
    }

    public void setCar_info(CarInfo car_info) {
        this.car_info = car_info;
    }
}


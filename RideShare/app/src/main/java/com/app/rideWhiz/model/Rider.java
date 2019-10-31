package com.app.rideWhiz.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Rider implements Serializable {


    @SerializedName("u_id")
    private String nUserId;

    @SerializedName("u_firstname")
    private String mFirstName;

    @SerializedName("u_lastname")
    private String mLastName;

    @SerializedName("u_email")
    private String mEmail;

    @SerializedName("u_mo_number")
    private String mMobileNumber;

    @SerializedName("u_lat")
    private String mLatitude;

    @SerializedName("u_long")
    private String mLongitude;

    @SerializedName("u_type")
    private String mType;

    @SerializedName("token")
    private String mToken;

    @SerializedName("distance")
    private String mDistance;

    @SerializedName("address_from_google")
    private String mAddress;

    @SerializedName("profile_image")
    private String mProfileImage;

    private String thumb_image;

    @SerializedName("u_ride_type")
    private String u_ride_type;


    @SerializedName("group_id")
    private String mGroup_id;

    @SerializedName("is_driver")
    private String is_driver;

    @SerializedName("max_passengers")
    private String max_passengers;

    @SerializedName("no_of_seats")
    private String no_of_seats;

    @SerializedName("average_rate")
    private String average_rate;

    @SerializedName("destination_address")
    private String destination_address;

    private String rideID;

    private int rideCount;

    private boolean is_new_request;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getU_ride_type() {
        return u_ride_type;
    }

    public void setU_ride_type(String u_ride_type) {
        this.u_ride_type = u_ride_type;
    }

    public String getmProfileImage() {
        return mProfileImage;
    }

    public void setmProfileImage(String mProfileImage) {
        this.mProfileImage = mProfileImage;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getnUserId() {
        return nUserId;
    }

    public void setnUserId(String nUserId) {
        this.nUserId = nUserId;
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

    public String getmMobileNumber() {
        return mMobileNumber;
    }

    public void setmMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
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

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmGroup_id() {
        return mGroup_id;
    }

    public void setmGroup_id(String mGroup_id) {
        this.mGroup_id = mGroup_id;
    }

    public String getIs_driver() {
        return is_driver;
    }

    public void setIs_driver(String is_driver) {
        this.is_driver = is_driver;
    }

    public String getMax_passengers() {
        return max_passengers;
    }

    public void setMax_passengers(String max_passengers) {
        this.max_passengers = max_passengers;
    }

    public String getNo_of_seats() {
        return no_of_seats;
    }

    public void setNo_of_seats(String no_of_seats) {
        this.no_of_seats = no_of_seats;
    }

    public String getRideID() {
        return rideID;
    }

    public void setRideID(String rideID) {
        this.rideID = rideID;
    }

    public boolean getIs_new_request() {
        return is_new_request;
    }

    public void setIs_new_request(boolean is_new_request) {
        this.is_new_request = is_new_request;
    }

    public String getAverage_rate() {
        return average_rate;
    }

    public void setAverage_rate(String average_rate) {
        this.average_rate = average_rate;
    }

    public String getDestination_address() {
        return destination_address;
    }

    public void setDestination_address(String destination_address) {
        this.destination_address = destination_address;
    }

    public int getRideCount() {
        return rideCount;
    }

    public void setRideCount(int rideCount) {
        this.rideCount = rideCount;
    }
}

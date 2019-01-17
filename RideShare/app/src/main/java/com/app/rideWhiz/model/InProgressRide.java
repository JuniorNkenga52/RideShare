package com.app.rideWhiz.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InProgressRide implements Serializable{

    @SerializedName("ride_id")
    private String mRideId;

    @SerializedName("u_ride_type")
    private String mRideType;

    @SerializedName("starting_address")
    private String mStartingAddress;

    @SerializedName("ending_address")
    private String mEndingAddress;

    @SerializedName("start_lati")
    private String mStartLat;

    @SerializedName("start_long")
    private String mStartLang;

    @SerializedName("end_lati")
    private String mEndLat;

    @SerializedName("end_long")
    private String mEndLang;

    @SerializedName("request_status")
    private String mRequestStatus;

    @SerializedName("from_id")
    private Rider mFromRider;

    @SerializedName("to_id")
    private Rider mToRider;



    public String getmRideId() {
        return mRideId;
    }

    public void setmRideId(String mRideId) {
        this.mRideId = mRideId;
    }

    public String getmRideType() {
        return mRideType;
    }

    public void setmRideType(String mRideType) {
        this.mRideType = mRideType;
    }

    public String getmStartingAddress() {
        return mStartingAddress;
    }

    public void setmStartingAddress(String mStartingAddress) {
        this.mStartingAddress = mStartingAddress;
    }

    public String getmEndingAddress() {
        return mEndingAddress;
    }

    public void setmEndingAddress(String mEndingAddress) {
        this.mEndingAddress = mEndingAddress;
    }

    public String getmStartLat() {
        return mStartLat;
    }

    public void setmStartLat(String mStartLat) {
        this.mStartLat = mStartLat;
    }

    public String getmStartLang() {
        return mStartLang;
    }

    public void setmStartLang(String mStartLang) {
        this.mStartLang = mStartLang;
    }

    public String getmEndLat() {
        return mEndLat;
    }

    public void setmEndLat(String mEndLat) {
        this.mEndLat = mEndLat;
    }

    public String getmEndLang() {
        return mEndLang;
    }

    public void setmEndLang(String mEndLang) {
        this.mEndLang = mEndLang;
    }

    public String getmRequestStatus() {
        return mRequestStatus;
    }

    public void setmRequestStatus(String mRequestStatus) {
        this.mRequestStatus = mRequestStatus;
    }

    public Rider getmFromRider() {
        return mFromRider;
    }

    public void setmFromRider(Rider mFromRider) {
        this.mFromRider = mFromRider;
    }

    public Rider getmToRider() {
        return mToRider;
    }

    public void setmToRider(Rider mToRider) {
        this.mToRider = mToRider;
    }
}

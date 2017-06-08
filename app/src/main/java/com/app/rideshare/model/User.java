package com.app.rideshare.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class User implements Serializable
{
    @SerializedName("u_id")
    private String mUserId;
    @SerializedName("u_firstname")
    private String mFirstName;
    @SerializedName("u_lastname")
    private String mLastName;
    @SerializedName("u_email")
    private String mEmail;
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
}

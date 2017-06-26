package com.app.rideshare.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Rider  implements Serializable{


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
}

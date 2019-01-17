package com.app.rideWhiz.model;


import java.io.Serializable;

public class SearchPlace implements Serializable
{
    private String mArea;
    private String mAddress;
    private String mLatitude;
    private String mLongitude;
    private String mLocationId;

    public String getmArea() {
        return mArea;
    }

    public void setmArea(String mArea) {
        this.mArea = mArea;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
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

    public String getmLocationId() {
        return mLocationId;
    }

    public void setmLocationId(String mLocationId) {
        this.mLocationId = mLocationId;
    }
}

package com.app.rideWhiz.api.response;

import com.app.rideWhiz.model.RideResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SendResponse
{
    @SerializedName("status")
    private String mStatus;

    @SerializedName("type")
    private String mType;

    @SerializedName("msg")
    ArrayList<RideResponse> mlist;

    @SerializedName("message")
    private String message;

    public ArrayList<RideResponse> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<RideResponse> mlist) {
        this.mlist = mlist;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

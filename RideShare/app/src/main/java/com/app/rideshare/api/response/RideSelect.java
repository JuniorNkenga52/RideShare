package com.app.rideshare.api.response;

import com.app.rideshare.model.Rider;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RideSelect
{
    @SerializedName("status")
    private String mStatus;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("result")
    private ArrayList<Rider> mlistUser;

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public ArrayList<Rider> getMlistUser() {
        return mlistUser;
    }

    public void setMlistUser(ArrayList<Rider> mlistUser) {
        this.mlistUser = mlistUser;
    }
}

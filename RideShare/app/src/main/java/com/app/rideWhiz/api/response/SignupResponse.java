package com.app.rideWhiz.api.response;


import com.app.rideWhiz.model.InProgressRide;
import com.app.rideWhiz.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SignupResponse
{
    @SerializedName("status")
    private String mStatus;

    @SerializedName("result")
    ArrayList<User> mlist;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("ride_history")
    ArrayList<InProgressRide> mProgressRide;


    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public ArrayList<User> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<User> mlist) {
        this.mlist = mlist;
    }

    public ArrayList<InProgressRide> getmProgressRide() {
        return mProgressRide;
    }

    public void setmProgressRide(ArrayList<InProgressRide> mProgressRide) {
        this.mProgressRide = mProgressRide;
    }
}

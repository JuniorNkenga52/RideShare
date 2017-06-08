package com.app.rideshare.api.response;


import com.app.rideshare.model.User;
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
}

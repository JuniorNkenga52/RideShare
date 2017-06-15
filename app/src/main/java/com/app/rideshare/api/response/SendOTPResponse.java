package com.app.rideshare.api.response;

import com.google.gson.annotations.SerializedName;

public class SendOTPResponse
{
    @SerializedName("status")
    private String mStatus;

    @SerializedName("result")
    private String mResult;


    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmResult() {
        return mResult;
    }

    public void setmResult(String mResult) {
        this.mResult = mResult;
    }
}

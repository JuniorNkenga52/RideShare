package com.app.rideWhiz.api.response;

import com.app.rideWhiz.model.RateRide;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-48 on 12/1/2017.
 */

public class RateRideResponce {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("result")
    private ArrayList<RateRide> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<RateRide> getResult() {
        return result;
    }

    public void setResult(ArrayList<RateRide> result) {
        this.result = result;
    }


}

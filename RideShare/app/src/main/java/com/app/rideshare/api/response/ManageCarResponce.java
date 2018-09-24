package com.app.rideshare.api.response;

import com.app.rideshare.model.ManageCar;
import com.app.rideshare.model.RateRide;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-48 on 12/1/2017.
 */

public class ManageCarResponce {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("result")
    private ManageCar result;

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

    public ManageCar getResult() {
        return result;
    }

    public void setResult(ManageCar result) {
        this.result = result;
    }
}

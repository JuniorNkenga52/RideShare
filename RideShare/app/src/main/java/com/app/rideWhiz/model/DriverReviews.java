package com.app.rideWhiz.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DriverReviews implements Serializable {
    @SerializedName("result")
    private ArrayList<DriverReviewResponse> result;
    @SerializedName("avg_rate")
    private String avg_rate;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;

    public ArrayList<DriverReviewResponse> getResult() {
        return result;
    }

    public void setResult(ArrayList<DriverReviewResponse> result) {
        this.result = result;
    }

    public String getAvg_rate() {
        return avg_rate;
    }

    public void setAvg_rate(String avg_rate) {
        this.avg_rate = avg_rate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [result = " + result + ", avg_rate = " + avg_rate + ", message = " + message + ", status = " + status + "]";
    }
}
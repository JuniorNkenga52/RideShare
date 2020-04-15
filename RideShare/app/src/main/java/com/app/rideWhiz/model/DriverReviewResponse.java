package com.app.rideWhiz.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DriverReviewResponse implements Serializable {
    @SerializedName("ride_id")
    private String ride_id;
    @SerializedName("driver_id")
    private String driver_id;
    @SerializedName("rate")
    private String rate;
    @SerializedName("review")
    private String review;
    @SerializedName("id")
    private String id;
    @SerializedName("created_date")
    private String created_date;
    @SerializedName("updated_date")
    private String updated_date;
    @SerializedName("rider_id")
    private String rider_id;

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    @Override
    public String toString() {
        return "ClassPojo [ride_id = " + ride_id + ", driver_id = " + driver_id + ", rate = " + rate + ", review = " + review + ", id = " + id + ", created_date = " + created_date + ", updated_date = " + updated_date + ", rider_id = " + rider_id + "]";
    }
}
package com.app.rideWhiz.api.response;

import com.app.rideWhiz.model.Rider;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class AcceptRider  implements Serializable{
    private String ride_id;
    private String u_ride_type;
    private String starting_address;
    private String ending_address;
    private String start_lati;
    private String start_long;
    private String end_lati;
    private String end_long;
    private String request_status;
    private String created_datetime;
    private String updated_datetime;

    @SerializedName("to_id")
    private Rider toRider;

    @SerializedName("from_id")
    private Rider fromRider;


    public AcceptRider()
    {
        this.request_status = "";
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getU_ride_type() {
        return u_ride_type;
    }

    public void setU_ride_type(String u_ride_type) {
        this.u_ride_type = u_ride_type;
    }

    public String getStarting_address() {
        return starting_address;
    }

    public void setStarting_address(String starting_address) {
        this.starting_address = starting_address;
    }

    public String getEnding_address() {
        return ending_address;
    }

    public void setEnding_address(String ending_address) {
        this.ending_address = ending_address;
    }

    public String getStart_lati() {
        return start_lati;
    }

    public void setStart_lati(String start_lati) {
        this.start_lati = start_lati;
    }

    public String getStart_long() {
        return start_long;
    }

    public void setStart_long(String start_long) {
        this.start_long = start_long;
    }

    public String getEnd_lati() {
        return end_lati;
    }

    public void setEnd_lati(String end_lati) {
        this.end_lati = end_lati;
    }

    public String getEnd_long() {
        return end_long;
    }

    public void setEnd_long(String end_long) {
        this.end_long = end_long;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getUpdated_datetime() {
        return updated_datetime;
    }

    public void setUpdated_datetime(String updated_datetime) {
        this.updated_datetime = updated_datetime;
    }

    public Rider getToRider() {
        return toRider;
    }

    public void setToRider(Rider toRider) {
        this.toRider = toRider;
    }

    public Rider getFromRider() {
        return fromRider;
    }

    public void setFromRider(Rider fromRider) {
        this.fromRider = fromRider;
    }
}

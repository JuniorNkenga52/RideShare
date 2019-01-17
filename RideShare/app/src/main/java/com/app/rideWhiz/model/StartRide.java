package com.app.rideWhiz.model;

/**
 * Created by rlogical-dev-19 on 29-Jun-2017.
 */

public class StartRide
{
    private String ride_id;
    private String to_id;
    private String from_id;
    private String u_ride_type;
    private String starting_address;
    private String ending_address;
    private String start_lati;
    private String start_long;
    private String end_lati;
    private String end_long;
    private String request_status;
    private String ride_status;

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
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

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }
}

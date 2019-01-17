package com.app.rideWhiz.model;

public class HistoryBean
{
    private String ride_id;
    private String u_ride_type;
    private String request_status;
    private String fromname;
    private String toname;
    private String time;
    private String starting_address;
    private String ending_address;
    private String from_id;
    private String to_id;


    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getToname() {
        return toname;
    }

    public void setToname(String toname) {
        this.toname = toname;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
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

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}

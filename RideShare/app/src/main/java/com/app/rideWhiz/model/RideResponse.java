package com.app.rideWhiz.model;


import java.io.Serializable;

public class RideResponse implements Serializable
{
    private String u_id;
    private String u_firstname;
    private String u_lastname;
    private String u_email;
    private String profile_image;
    private String u_fb_id;
    private String u_google_id;
    private String u_mo_number;
    private String u_password;
    private String u_lat;
    private String u_long;
    private String u_type;
    private String token;
    private String mobile_verify_number;
    private String verify_mobile;
    private String u_ride_type;
    private String u_status;
    private String starting_address;
    private String ending_address;
    private String distance;
    private String ride_id;

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_firstname() {
        return u_firstname;
    }

    public void setU_firstname(String u_firstname) {
        this.u_firstname = u_firstname;
    }

    public String getU_lastname() {
        return u_lastname;
    }

    public void setU_lastname(String u_lastname) {
        this.u_lastname = u_lastname;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getU_fb_id() {
        return u_fb_id;
    }

    public void setU_fb_id(String u_fb_id) {
        this.u_fb_id = u_fb_id;
    }

    public String getU_google_id() {
        return u_google_id;
    }

    public void setU_google_id(String u_google_id) {
        this.u_google_id = u_google_id;
    }

    public String getU_mo_number() {
        return u_mo_number;
    }

    public void setU_mo_number(String u_mo_number) {
        this.u_mo_number = u_mo_number;
    }

    public String getU_password() {
        return u_password;
    }

    public void setU_password(String u_password) {
        this.u_password = u_password;
    }

    public String getU_lat() {
        return u_lat;
    }

    public void setU_lat(String u_lat) {
        this.u_lat = u_lat;
    }

    public String getU_long() {
        return u_long;
    }

    public void setU_long(String u_long) {
        this.u_long = u_long;
    }

    public String getU_type() {
        return u_type;
    }

    public void setU_type(String u_type) {
        this.u_type = u_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile_verify_number() {
        return mobile_verify_number;
    }

    public void setMobile_verify_number(String mobile_verify_number) {
        this.mobile_verify_number = mobile_verify_number;
    }

    public String getVerify_mobile() {
        return verify_mobile;
    }

    public void setVerify_mobile(String verify_mobile) {
        this.verify_mobile = verify_mobile;
    }

    public String getU_ride_type() {
        return u_ride_type;
    }

    public void setU_ride_type(String u_ride_type) {
        this.u_ride_type = u_ride_type;
    }

    public String getU_status() {
        return u_status;
    }

    public void setU_status(String u_status) {
        this.u_status = u_status;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }
}

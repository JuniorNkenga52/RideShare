package com.app.rideWhiz.model;

import java.util.ArrayList;

public class MatchedUser {

    private String user_id;
    private String matched_user_fb_id;
    private String matched_user_id;
    private String user_request_status;
    private String matched_request_status;
    private String user_profile_status;
    private String matched_profile_status;
    private String name;
    private String fname;
    private String lname;
    private String profile_image;

    private String gender;

    private ArrayList<String> images;

    private String FullJid;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public MatchedUser() {
        FullJid = "";
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMatched_user_id() {
        return matched_user_id;
    }

    public void setMatched_user_id(String matched_user_id) {
        this.matched_user_id = matched_user_id;
    }

    public String getUser_request_status() {
        return user_request_status;
    }

    public void setUser_request_status(String user_request_status) {
        this.user_request_status = user_request_status;
    }

    public String getMatched_request_status() {
        return matched_request_status;
    }

    public void setMatched_request_status(String matched_request_status) {
        this.matched_request_status = matched_request_status;
    }

    public String getUser_profile_status() {
        return user_profile_status;
    }

    public void setUser_profile_status(String user_profile_status) {
        this.user_profile_status = user_profile_status;
    }

    public String getMatched_profile_status() {
        return matched_profile_status;
    }

    public void setMatched_profile_status(String matched_profile_status) {
        this.matched_profile_status = matched_profile_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getFullJid() {
        return FullJid;
    }

    public void setFullJid(String fullJid) {
        FullJid = fullJid;
    }

    public String getMatched_user_fb_id() {
        return matched_user_fb_id;
    }

    public void setMatched_user_fb_id(String matched_user_fb_id) {
        this.matched_user_fb_id = matched_user_fb_id;
    }
}
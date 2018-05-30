package com.app.rideshare.model;

/**
 * Created by rlogical-dev-48 on 11/10/2017.
 */

public class NotificationList {

    private String u_id;
    private String u_firstname;

    private String u_lastname;
    private String u_email;
    private String profile_image;
    private String category_name;
    private String category_id;

    private String group_id;
    private String group_name;

    private String status;
    private String is_admin_accept;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getIs_admin_accept() {
        return is_admin_accept;
    }

    public void setIs_admin_accept(String is_admin_accept) {
        this.is_admin_accept = is_admin_accept;
    }
}

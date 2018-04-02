package com.app.rideshare.model;

import java.io.Serializable;

/**
 * Created by rlogical-dev-48 on 11/10/2017.
 */

public class GroupList implements Serializable {

    private String id;
    private String group_name;

    private String group_description;
    private String category_name;
    private String category_image;
    private String is_joined;
    private String is_admin;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getGroup_description() {
        return group_description;
    }

    public void setGroup_description(String group_description) {
        this.group_description = group_description;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getIs_joined() {
        return is_joined;
    }

    public void setIs_joined(String is_joined) {
        this.is_joined = is_joined;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }


}

package com.app.rideWhiz.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyGroup implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("group_name")
    private String group_name;
    @SerializedName("group_description")
    private String group_description;
    @SerializedName("group_image")
    private String group_image;
    @SerializedName("category_name")
    private String category_name;
    @SerializedName("category_image")
    private String category_image;

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

    public String getGroup_description() {
        return group_description;
    }

    public void setGroup_description(String group_description) {
        this.group_description = group_description;
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
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
}
		
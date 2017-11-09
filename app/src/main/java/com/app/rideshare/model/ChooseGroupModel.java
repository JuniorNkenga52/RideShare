package com.app.rideshare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rlogical-dev-48 on 11/7/2017.
 */

public class ChooseGroupModel {

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //@SerializedName("id")
    private int id;

    //@SerializedName("user_id")
    private int user_id;
    //@SerializedName("group_name")
    private String group_name;
   // @SerializedName("status")
    private String status;
   // @SerializedName("created_date")
    private String created_date;
   // @SerializedName("updated_date")
    private String updated_date;


    public ChooseGroupModel(int gropid, String groupname) {
        this.user_id = gropid;
        this.group_name = groupname;
    }



}

package com.app.rideshare.api.response;

import com.app.rideshare.model.GroupusersModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-48 on 11/15/2017.
 */

public class MyGroupsResponce {
    @SerializedName("status")
    private String status;

    @SerializedName("result")
    private ArrayList<GroupusersModel> result;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GroupusersModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupusersModel> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

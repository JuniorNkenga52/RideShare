package com.app.rideshare.api.response;

import com.app.rideshare.model.ChooseGroupModel;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-48 on 11/10/2017.
 */

public class GroupListResponce {
    private String status;
    //private ChooseGroupModel result;
    private ArrayList<ChooseGroupModel> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ChooseGroupModel> getResult() {
        return result;
    }

    public void setResult(ArrayList<ChooseGroupModel> result) {
        this.result = result;
    }

}

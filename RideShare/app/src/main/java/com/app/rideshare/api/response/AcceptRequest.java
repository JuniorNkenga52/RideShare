package com.app.rideshare.api.response;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-19 on 20-Jun-2017.
 */

public class AcceptRequest
{
    private String status;
    private ArrayList<AcceptRider> msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<AcceptRider> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<AcceptRider> msg) {
        this.msg = msg;
    }
}

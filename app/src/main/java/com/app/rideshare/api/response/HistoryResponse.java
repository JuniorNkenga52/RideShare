package com.app.rideshare.api.response;

import com.app.rideshare.model.HistoryBean;

import java.util.ArrayList;

public class HistoryResponse
{
    private String message;
    private ArrayList<HistoryBean> result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<HistoryBean> getResult() {
        return result;
    }

    public void setResult(ArrayList<HistoryBean> result) {
        this.result = result;
    }
}
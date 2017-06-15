package com.app.rideshare.api.request;

import com.app.rideshare.model.ContactBean;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-19 on 15-Jun-2017.
 */

public class ContactRequest
{
    private String user_id;
    private ArrayList<ContactBean> contact;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ArrayList<ContactBean> getContact() {
        return contact;
    }

    public void setContact(ArrayList<ContactBean> contact) {
        this.contact = contact;
    }
}

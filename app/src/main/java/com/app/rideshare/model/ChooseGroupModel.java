package com.app.rideshare.model;

/**
 * Created by rlogical-dev-48 on 11/7/2017.
 */

public class ChooseGroupModel {

    private int user_id;
    private String group_name;

    public ChooseGroupModel(int gropid, String groupname) {
        this.user_id = gropid;
        this.group_name = groupname;
    }

    public int getGropid() {
        return user_id;
    }

    public String getGroupname() {
        return group_name;
    }

}

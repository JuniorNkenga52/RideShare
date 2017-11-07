package com.app.rideshare.model;

/**
 * Created by rlogical-dev-48 on 11/7/2017.
 */

public class ChooseGroupModel {

    private int gropid;
    private String groupname;

    public ChooseGroupModel(int gropid, String groupname) {
        this.gropid = gropid;
        this.groupname = groupname;
    }

    public int getGropid() {
        return gropid;
    }

    public String getGroupname() {
        return groupname;
    }

}

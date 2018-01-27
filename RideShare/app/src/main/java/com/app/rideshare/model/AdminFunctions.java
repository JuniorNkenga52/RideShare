package com.app.rideshare.model;

/**
 * Created by rlogical-dev-48 on 11/7/2017.
 */

public class AdminFunctions {


    private String fname;
    private String lname;
    private String urole;
    private int ap_dr_req;
    private int disble;
    private int ap_admin_priv;

    public AdminFunctions(String fname, String lname, String urole, int ap_dr_req, int disble, int ap_admin_priv) {
        this.fname = fname;
        this.lname = lname;
        this.urole = urole;
        this.ap_dr_req = ap_dr_req;
        this.disble = disble;
        this.ap_admin_priv = ap_admin_priv;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getUrole() {
        return urole;
    }

    public int getAp_dr_req() {
        return ap_dr_req;
    }

    public int getDisble() {
        return disble;
    }

    public int getAp_admin_priv() {
        return ap_admin_priv;
    }
}

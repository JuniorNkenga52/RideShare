package com.app.rideWhiz.model;

/**
 * Created by hiteshsheth on 30/08/17.
 */

public class CarType {

    String cabid = "";
    String cartype = "";
    String caricon = "";
    String carsideicon = "";
    String is_pool = "";
    String active_side_icon = "";
    boolean isselect = false;

    public String getActive_side_icon() {
        return active_side_icon;
    }

    public void setActive_side_icon(String active_side_icon) {
        this.active_side_icon = active_side_icon;
    }



    public String getIs_pool() {
        return is_pool;
    }

    public void setIs_pool(String is_pool) {
        this.is_pool = is_pool;
    }

    public String getCarsideicon() {
        return carsideicon;
    }

    public void setCarsideicon(String carsideicon) {
        this.carsideicon = carsideicon;
    }

    public boolean isselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public String getCabid() {
        return cabid;
    }

    public void setCabid(String cabid) {
        this.cabid = cabid;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }

    public String getCaricon() {
        return caricon;
    }

    public void setCaricon(String caricon) {
        this.caricon = caricon;
    }


}

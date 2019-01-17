package com.app.rideWhiz.model;

/**
 * Created by hiteshsheth on 05/09/17.
 */

public class CarMonth {

    private String month;
    private boolean selected;

    public CarMonth(String month, boolean selected) {
        this.month = month;
        this.selected = selected;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }






}

package com.app.rideshare.model;

/**
 * Created by hiteshsheth on 05/09/17.
 */

public class CarYear {




    private String year;
    private boolean selected;

    public CarYear(String year, boolean selected) {
        this.year = year;
        this.selected = selected;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }






}

package com.app.rideshare.model;

/**
 * Created by hiteshsheth on 05/09/17.
 */

public class CarBrand {




    private String brand;
    private boolean selected;

    public CarBrand(String brand, boolean selected) {
        this.brand = brand;
        this.selected = selected;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }





}

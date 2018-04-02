package com.app.rideshare.model;

/**
 * Created by rlogical-dev-48 on 11/10/2017.
 */

public class Category {

    private String id;
    private String name;

    private String image;
    private String status;
    public boolean isSelect;

    public Category() {
        id = "";
        name = "";
        image = "";
        status = "";
        isSelect = false;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

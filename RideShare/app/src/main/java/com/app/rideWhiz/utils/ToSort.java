package com.app.rideWhiz.utils;

public class ToSort implements Comparable<ToSort> {

    private Double val;
    private String id;

    public ToSort(Double val, String id){
        this.val = val;
        this.id = id;
    }

    public Double getVal() {
        return val;
    }

    public void setVal(Double val) {
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(ToSort f) {

        return val.compareTo(f.val);
    }

    @Override
    public String toString(){
        return this.id;
    }
}
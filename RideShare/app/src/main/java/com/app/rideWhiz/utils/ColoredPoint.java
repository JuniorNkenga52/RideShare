package com.app.rideWhiz.utils;

import com.google.android.gms.maps.model.LatLng;

public class ColoredPoint {
    public LatLng coords;
    public int color;

    public ColoredPoint(LatLng coords, int color) {
        this.coords = coords;
        this.color = color;
    }
}

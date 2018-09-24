package com.app.rideshare.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapDirectionAPI {

    public static Call getDirection(LatLng pickUp, LatLng destination, Context context) {
         OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                //.url(gMapDirection.getUrl(context,pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .url(gMapDirection.getDirectionsUrl(context,pickUp, destination, false))
                .build();

        return client.newCall(request);
    }

    public static Call getDirectionVia(LatLng pickUp, LatLng... destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrlVia(GMapDirection.MODE_DRIVING, false, pickUp, destination))
                .build();

        return client.newCall(request);
    }

}
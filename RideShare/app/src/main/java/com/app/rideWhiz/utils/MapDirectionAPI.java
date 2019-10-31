package com.app.rideWhiz.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapDirectionAPI {

    public static Call getDirection(LatLng pickUp, LatLng destination, Context context) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrl(context, pickUp, destination, GMapDirection.MODE_DRIVING, false))
                //.url(gMapDirection.getDirectionsUrl(context,pickUp, destination, true))
                .build();

        return client.newCall(request);
    }

    public static Call getAlterNativeDirection(LatLng pickUp, LatLng destination, Context context) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrl(context, pickUp, destination, GMapDirection.MODE_DRIVING, true))
                //.url(gMapDirection.getDirectionsUrl(context,pickUp, destination, true))
                .build();

        return client.newCall(request);
    }

    public static Call getDirectionVia(Context context, LatLng pickUp, LatLng... destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrlVia(context, GMapDirection.MODE_DRIVING, false, pickUp, destination))
                .build();

        return client.newCall(request);
    }

    public static Call getWayPointsDirection(LatLng pickUp, LatLng destination, Context context, ArrayList<LatLng> markerPoints) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getWayPointsUrl(context, pickUp, destination, markerPoints))
                .build();

        return client.newCall(request);
    }
}
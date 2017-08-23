package com.app.rideshare.api;

import java.io.File;
import java.util.LinkedHashMap;

public class DSMTrackerApi {

    public static final String SERVER_URL = "http://php.rlogical.com/rideshare/api/";

    public static String postApiCall(String uid,String type)
    {

        try {

            String URL = SERVER_URL + "auth/select_ride";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("u_id", uid);
            params.put("u_ride_type", type);
            params.put("u_lat", "");
            params.put("u_long", "");



            return DSMTrackerApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getApiCall() {

        try {

            String URL = SERVER_URL + "";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("", "");

            return DSMTrackerApiCall.getWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }
}
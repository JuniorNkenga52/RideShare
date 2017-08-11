package com.dsmtracker.api;

import java.io.File;
import java.util.LinkedHashMap;

public class DSMTrackerApi {

    public static final String SERVER_URL = "";

    public static String postApiCall() {

        try {

            String URL = SERVER_URL + "";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("", "");

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

    public static String mpeApiCall() {
        try {

            String URL = SERVER_URL + "";

            DSMTrackerMPEApiCall mpu = new DSMTrackerMPEApiCall(URL);

            mpu.addFormField("", "");

            mpu.addFilePart("", new File(""));

            return mpu.execute();

        } catch (Exception e) {
            return null;
        }
    }
}
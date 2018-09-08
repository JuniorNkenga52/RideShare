package com.app.rideshare.api;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

public class RideShareApiCall {

    private static final int TIMEOUT = 60000;

    public static String postWebserviceCall(String URL, LinkedHashMap<String, String> params,Context context) {

        //String response = "";
        StringBuilder response;

        try {

            URL url = new URL(URL);

            initializeSSLContext(context);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("APIKEY","$2y$10$lDBHZhvyNzWTdsgdgsg4cOivLqQAVTGppmV4yEeggsdtttwilio");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            if (params != null)
                writer.write(getPostDataString(params));

            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            int responseCode = conn.getResponseCode();

            InputStream iStream;

            response = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK)
                iStream = conn.getInputStream();
            else
                iStream = conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            /*if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    response += line;
                }

            } else {
                response = null;
            }*/
        } catch (Exception e) {
            response = null;
        }
        return response.toString();
    }

    public static void initializeSSLContext(Context mContext){
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public static String getWebserviceCall(String URL, LinkedHashMap<String, String> params) {

        String response = "";


        try {

            if (params != null)
                URL = URL + "?" + getPostDataString(params);

            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestMethod("GET");

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                String line;

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = null;
            }

        } catch (Exception e) {
            response = null;
        }

        return response;
    }

    public static String getPostDataString(LinkedHashMap<String, String> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();

        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
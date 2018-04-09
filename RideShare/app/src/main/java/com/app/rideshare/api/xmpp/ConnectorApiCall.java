package com.app.rideshare.api.xmpp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConnectorApiCall {

    private static final int TIMEOUT = 30000;

    public static String postWebserviceCall(String URL, LinkedHashMap<String, String> params) {

        String response = "";

        try {

            java.net.URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Add Header Key-Value here
            conn.addRequestProperty("Apikey", "#$kjjfhcxcvxvxn*&874590-0412321vcgvGGHHNJH");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            if (params != null)
                writer.write(getPostDataString(params));

            writer.flush();
            writer.close();
            os.close();
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

    public static String getWebserviceCall(String URL, LinkedHashMap<String, String> params) {

        String response = "";

        try {

            if (params != null)
                URL = URL + "?" + getPostDataString(params);

            java.net.URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            conn.setRequestMethod("GET");

            // Add Header Key-Value here
            conn.addRequestProperty("Apikey", "#$kjjfhcxcvxvxn*&874590-0412321vcgvGGHHNJH");

            conn.connect();

            int responseCode = conn.getResponseCode();

            Log.w(URL, "" + responseCode);

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

    private static String getPostDataString(LinkedHashMap<String, String> params) throws UnsupportedEncodingException {

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
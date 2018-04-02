package com.app.rideshare.api;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultipartUtility {

    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;

    private static final int TIMEOUT = 30000;

    private final String BOUNDRY;
    private static final String LINE_FEED = "\r\n";
    private static final String DASH = "--";

    public MultipartUtility(String requestURL) throws IOException {

        BOUNDRY = "======" + System.currentTimeMillis() + "======";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // Indicates POST Method
        httpConn.setDoInput(true);

        httpConn.setReadTimeout(TIMEOUT);
        httpConn.setConnectTimeout(TIMEOUT);

        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);
        httpConn.setRequestProperty("Connection", "Keep-Alive");

        // Add Header Key-Value here
        httpConn.addRequestProperty("APIKEY", "$2y$10$lDBHZhvyNzWTdsgdgsg4cOivLqQAVTGppmV4yEeggsdtttwilio");

        // ================
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
    }

    public void addFormField(String name, String value) {
        writer.append(DASH).append(BOUNDRY).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
        writer.append(LINE_FEED).append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addFilePart(String fieldName, File uploadFile) throws IOException {

        try {

            writer.append(DASH).append(BOUNDRY).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(uploadFile.getName()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(uploadFile.getName())).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);

            byte[] buffer = new byte[(int) uploadFile.length()];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        } catch (OutOfMemoryError ignore) {
            Log.w(uploadFile.getAbsolutePath(), ignore.getMessage());
        }
    }

    public String execute() throws IOException {

        String response = "";

        writer.append(LINE_FEED).flush();
        writer.append(DASH).append(BOUNDRY).append(DASH).append(LINE_FEED);
        writer.close();

        int status = httpConn.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                response += line;
            }

            reader.close();

        } else {
            return null;
        }

        httpConn.disconnect();

        return response;
    }
}
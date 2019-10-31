package com.app.rideWhiz.utils;

import com.app.rideWhiz.model.ManageCar;

import java.util.List;

public class Constants {

    public static boolean isGroupDataUpdated = false;
    public static List<ManageCar> manageCarsList;

    // public static final String SERVER_URL = "http://php.rlogical.com/rideshare_new/api/";
    // public static final String SERVER_URL = "http://php.rlogical.com/rideshare/api/";
    // public static final String SERVER_URL = "http://ec2-18-220-69-218.us-east-2.compute.amazonaws.com/rideshare/api/";
    // public static final String SERVER_URL = "http://ec2-13-58-7-10.us-east-2.compute.amazonaws.com/rideshare/api/";// OLD URL
    // public static final String SERVER_URL = "http://ec2-18-222-137-245.us-east-2.compute.amazonaws.com/rideshare/api/";// OLD 2 URL
    // public static final String SERVER_URL = "https://www.myridewhiz.com/rideshare/api/";// NEW URL
    // https://www.myridewhiz.com/
    // private static final String API_ENDPOINT = "http://ec2-18-220-69-218.us-east-2.compute.amazonaws.com/rideshare/api/";
    // public static final String API_ENDPOINT="http://ec2-13-58-7-10.us-east-2.compute.amazonaws.com/rideshare/api/"; OLD URL
    // public static final String API_ENDPOINT="http://ec2-18-222-137-245.us-east-2.compute.amazonaws.com/rideshare/api/";//OLD 2 URL
    // public static final String API_ENDPOINT="https://www.myridewhiz.com/rideshare/api/";//NEW URL
    // http://ec2-18-222-137-245.us-east-2.compute.amazonaws.com/rideshare/
    // public static final String WEBSOCKET_ENDPOINT="ws://ec2-18-220-69-218.us-east-2.compute.amazonaws.com:8090/ride-share-websocket/php-socket.php";//
    // public static final String WEBSOCKET_ENDPOINT="ws://www.myridewhiz.com/ride-share-websocket/php-socket.php";
    // https://www.myridewhiz.com/ride-share-websocket/index.php
    // public static final String WEBSOCKET_ENDPOINT = "ws://www.myridewhiz.com/ride-share-websocket/php-socket.php";
    // public static final String WEBSOCKET_ENDPOINT="ws://ec2-18-218-151-202.us-east-2.compute.amazonaws.com:9090" +"/ride-share-websocket/php-socket.php";
    // public static final String API_ENDPOINT = "http://php.rlogical.com/rideshare_new/api/";
    // public static final String API_ENDPOINT = "http://php.rlogical.com/rideshare/api/";
    // public static final String WEBSOCKET_ENDPOINT = "ws://192.168.0.30:8090/websocketnew/php-socket.php";
    // public static final String WEBSOCKET_ENDPOINT = "ws://180.211.99.75:8090/websocketnew/php-socket.php";
    // public static final String WEBSOCKET_ENDPOINT = "ws://18.218.151.202:8090/ride-share-websocket/php-socket.php";
    // public static final String WEBSOCKET_ENDPOINT = "ws://192.168.0.30:8090/websocketnew/php-socket.php";

    public static final String WEBSOCKET_ENDPOINT = "wss://www.myridewhiz.com:8090/ride-share-websocket/php-socket.php";
    public static final String SERVER_URL = "http://ridewhiz.rlogical.com/api/";

    //public static final String DOMAIN = "ec2-18-218-151-202.us-east-2.compute.amazonaws.com";
    public static final String DOMAIN = "ec2-18-218-151-202.us-east-2.compute.amazonaws.com";
    //public static final String DOMAIN = "http://180.211.99.75:9090";
    //public static final String DOMAIN = "http://18.218.151.202";
    public static final String RESOURCE_NAME = "RideWhiz";
    public static final int PORT = 9090;

    public interface intentKey {
        String MyGroup = "MyGroup";
        String isEditGroup = "isEditGroup";
        String groupDetail = "groupDetail";
        String jabberPrefix = "RideWhiz_";
        String SelectedChatUser = "SelectedChatUser";
    }
}

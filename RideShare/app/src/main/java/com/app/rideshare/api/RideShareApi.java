package com.app.rideshare.api;

import android.util.Log;

import java.io.File;
import java.util.LinkedHashMap;

public class RideShareApi {

    //public static final String SERVER_URL = "http://php.rlogical.com/rideshare/api/";
    //public static final String SERVER_URL = "http://ec2-18-220-69-218.us-east-2.compute.amazonaws.com/rideshare/api/";
    public static final String SERVER_URL = "http://ec2-13-58-7-10.us-east-2.compute.amazonaws.com/rideshare/api/";

    public static String postApiCall(String uid, String type) {
        try {
            String URL = SERVER_URL + "auth/select_ride";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("u_id", uid);
            params.put("u_ride_type", type);
            params.put("u_lat", "");
            params.put("u_long", "");


            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getApiCall() {
        try {
            String URL = SERVER_URL + "";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("", "");

            return RideShareApiCall.getWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String sendTextMessageNew(String mobile_number) {
        try {
            String URL = SERVER_URL + "user/sendTextMessageNew";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("mobile_number", mobile_number);
            params.put("type", "android");

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String groupNew(String user_id) {
        try {
            String URL = SERVER_URL + "group/groupNew";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFirstLoginGroup(String user_id) {
        try {
            String URL = SERVER_URL + "group/getFirstLoginGroup";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String mygroups(String user_id) {
        try {
            String URL = SERVER_URL + "user/mygroups";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }


    public static String mygroupsForLogin(String user_id,String adminID) {
        try {
            String URL = SERVER_URL + "user/mygroupsForLogin";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);
            params.put("login_user_id", adminID);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String groupJoinRequestList(String user_id) {
        try {
            String URL = SERVER_URL + "joinGroup/groupJoinRequestList";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getGroupDetailFromId(String user_id, String group_id) {
        try {
            String URL = SERVER_URL + "group/getGroupDetails";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);
            params.put("group_id", group_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUpdateUserGroup(String user_id, String group_id) {
        try {
            String URL = SERVER_URL + "user/updateUserGroup";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);
            params.put("group_id", group_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String groupusers(String user_id, String group_id) {
        try {
            String URL = SERVER_URL + "user/groupusers";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);
            params.put("group_id", group_id);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String category() {
        try {
            String URL = SERVER_URL + "group/category";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String verifyOTP(String OTP, String UserId, String mobile_token) {
        try {
            String URL = SERVER_URL + "user/verifyMobileNew";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", UserId);
            params.put("token_number", OTP);
            params.put("mobile_token", mobile_token);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String createGroup(String UserId, String category_id, String group_name, String group_description) {
        try {
            String URL = SERVER_URL + "group/createNew";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", UserId);
            params.put("category_id", category_id);
            params.put("group_name", group_name);
            params.put("group_description", group_description);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String editGroup(String UserId, String group_id, String category_id, String group_name, String group_description) {
        try {
            String URL = SERVER_URL + "group/editGroup";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", UserId);
            params.put("group_id", group_id);
            params.put("category_id", category_id);
            params.put("group_name", group_name);
            params.put("group_description", group_description);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String updateProfileNew(String user_id, String u_firstname, String u_lastname, String address, String u_email, String profile_image,
                                          String vehicle_model, String vehicle_type, String max_passengers) {
        try {
            String SERVER_WS_URL = SERVER_URL + "user/updateProfileNew";

            MultipartUtility multipart = new MultipartUtility(SERVER_WS_URL);

            multipart.addFormField("user_id", user_id);
            multipart.addFormField("u_firstname", u_firstname);
            multipart.addFormField("u_lastname", u_lastname);
            multipart.addFormField("address", address);
            multipart.addFormField("u_email", u_email);

            File file = new File(profile_image);
            if (file.exists()) {
                multipart.addFilePart("profile_image", file);
            } else {
                multipart.addFormField("profile_image", "");
            }

            multipart.addFormField("vehicle_model", vehicle_model);
            multipart.addFormField("vehicle_type", vehicle_type);
            multipart.addFormField("max_passengers", max_passengers);


            return multipart.execute();

        } catch (Exception e) {
            return null;
        }
    }

    public static String joinGroup(String user_id, String group_id, String status) {

        try {

            String URL = SERVER_URL + "group/joinGroup";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("user_id", user_id);
            params.put("group_id", group_id);
            params.put("status", status);

            Log.d("User id",user_id);
            Log.d("Group Id",group_id);
            Log.d("Status",status);

            return RideShareApiCall.postWebserviceCall(URL, params);
        } catch (Exception e) {
            return null;
        }
    }

}
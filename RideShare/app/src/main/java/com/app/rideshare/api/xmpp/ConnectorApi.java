package com.app.rideshare.api.xmpp;


public class ConnectorApi {

    private static final String SERVER_URL = "http://php.rlogical.com/connector/api/";

    // Done
    public static String getUserPresence(String user) {

        try {

            String SERVER_WS_URL = "http://192.168.0.30:9090/plugins/presence/status?jid=" + user + "@win-2i67mca8hqp&type=xml";

            return ConnectorApiCall.getWebserviceCall(SERVER_WS_URL, null);

        } catch (Exception e) {
            return null;
        }
    }

//    public static String register(User user) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "auth/register";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("facebook_id", user.getFacebookId());
//            params.put("email", user.getEmail());
//            params.put("name", user.getUserName());
//            params.put("first_name", user.getFirstName());
//            params.put("last_name", user.getLastName());
//            params.put("device_token", PrefUtils.getString(PrefUtils.PREF_GCM_REG_ID));
//            params.put("device_type", user.getUserDevice());
//            params.put("profile_image", user.getProfilePicture());
//            params.put("location_distance", user.getLocationDistance());
//            params.put("birth_date", user.getBirthDate());
//            params.put("gender", user.getGender());
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String updateUserProfile(String location_distance, String gender, String show_male, String show_female) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/userProfileUpdate";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("location_distance", location_distance);
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("gender", gender);
//            params.put("show_male", show_male);
//            params.put("show_female", show_female);
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String findMyMatches() {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/find_my_match";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String companiesList() {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "companies";
//            return ConnectorApiCall.getWebserviceCall(SERVER_WS_URL, null);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String requestList() {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/request_list";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String getGalleryImages() {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/gallery_images";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String matchDetail(String matched_user_id) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/match_detail";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("matched_user_id", matched_user_id);
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String sendProfileRequest(String matched_user_id) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/send_profile_request";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("matched_user_id", matched_user_id);
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    public static String acceptRequest(String matched_user_id) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/accept_profile_request";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("matched_user_id", matched_user_id);
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String declineRequest(String matched_user_id) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/decline_profile_request";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("matched_user_id", matched_user_id);
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String addAnswer(String question_answer) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/addanswers";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("question_answer", question_answer);
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String matchedUserList() {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/matched";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String userLocation(String latitude, String longitude) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/userLocations";
//
//            LinkedHashMap<String, String> params = new LinkedHashMap<>();
//
//            params.put("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//            params.put("latitude", latitude);
//            params.put("longitude", longitude);
//            params.put("device_token", PrefUtils.getString(PrefUtils.PREF_GCM_REG_ID));
//            params.put("device_type", "android");
//
//            return ConnectorApiCall.postWebserviceCall(SERVER_WS_URL, params);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String editProfile(User user) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/editProfile";
//
//            MultipartUtility multipart = new MultipartUtility(SERVER_WS_URL);
//
//            multipart.addFormField("user_id", user.getUserId());
//            multipart.addFormField("first_name", user.getFirstName());
//            multipart.addFormField("last_name", user.getLastName());
//            multipart.addFormField("email_id", user.getEmail());
//
//            if (new File(user.getProfilePicture()).exists())
//                multipart.addFilePart("profile_picture", new File(user.getProfilePicture()));
//
//            return multipart.execute();
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static String addUpdatePhoto(String image1, String image2, String image3, String image4, String image5, String image6) {
//
//        try {
//
//            String SERVER_WS_URL = SERVER_URL + "users/addphotos";
//
//            MultipartUtility multipart = new MultipartUtility(SERVER_WS_URL);
//
//            multipart.addFormField("user_id", PrefUtils.getString(PrefUtils.USER_ID));
//
//
//            if (image1.length() != 0) {
//                if (image1.split(":")[0].equalsIgnoreCase("http")) {
//                    multipart.addFormField("image_1", AppUtils.getFileNameFromUrl(new URL(image1)));
//                } else {
//                    File img1 = new File(new URL(image1).toURI());
//                    if (img1.exists())
//                        multipart.addFilePart("image_1", img1);
//                    else
//                        multipart.addFormField("image_1", "");
//                }
//            } else {
//                multipart.addFormField("image_1", "");
//            }
//
//            if (image2.length() != 0) {
//                if (image2.split(":")[0].equalsIgnoreCase("http")) {
//                    multipart.addFormField("image_2", AppUtils.getFileNameFromUrl(new URL(image2)));
//                } else {
//                    File img2 = new File(new URL(image2).toURI());
//                    if (img2.exists())
//                        multipart.addFilePart("image_2", img2);
//                    else
//                        multipart.addFormField("image_2", "");
//                }
//            } else {
//                multipart.addFormField("image_2", "");
//            }
//
//            if (image3.length() != 0) {
//                if (image3.split(":")[0].equalsIgnoreCase("http")) {
//                    multipart.addFormField("image_3", AppUtils.getFileNameFromUrl(new URL(image3)));
//                } else {
//                    File img3 = new File(new URL(image3).toURI());
//                    if (img3.exists())
//                        multipart.addFilePart("image_3", img3);
//                    else
//                        multipart.addFormField("image_3", "");
//                }
//            } else {
//                multipart.addFormField("image_3", "");
//            }
//
//            if (image4.length() != 0) {
//                if (image4.split(":")[0].equalsIgnoreCase("http")) {
//                    multipart.addFormField("image_4", AppUtils.getFileNameFromUrl(new URL(image4)));
//                } else {
//                    File img4 = new File(new URL(image4).toURI());
//                    if (img4.exists())
//                        multipart.addFilePart("image_4", img4);
//                    else
//                        multipart.addFormField("image_4", "");
//                }
//            } else {
//                multipart.addFormField("image_4", "");
//            }
//
//            if (image5.length() != 0) {
//                if (image5.split(":")[0].equalsIgnoreCase("http")) {
//                    multipart.addFormField("image_5", AppUtils.getFileNameFromUrl(new URL(image5)));
//                } else {
//                    File img5 = new File(new URL(image5).toURI());
//                    if (img5.exists())
//                        multipart.addFilePart("image_5", img5);
//                    else
//                        multipart.addFormField("image_5", "");
//                }
//            } else {
//                multipart.addFormField("image_5", "");
//            }
//
//            if (image6.length() != 0) {
//                if (image6.split(":")[0].equalsIgnoreCase("http")) {
//                    multipart.addFormField("image_6", AppUtils.getFileNameFromUrl(new URL(image6)));
//                } else {
//                    File img6 = new File(new URL(image6).toURI());
//                    if (img6.exists())
//                        multipart.addFilePart("image_6", img6);
//                    else
//                        multipart.addFormField("image_6", "");
//                }
//            } else {
//                multipart.addFormField("image_6", "");
//            }
//
//            return multipart.execute();
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
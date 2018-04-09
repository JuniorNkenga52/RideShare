package com.app.rideshare.utils;

import com.app.rideshare.chat.MessageModel;

import java.util.ArrayList;

public class Constant {

    public static boolean isGroupDataUpdated = false;

    public static ArrayList<MessageModel> listAllMsg =  new ArrayList<>();

    public interface intentKey {
        String MyGroup = "MyGroup";
        String isEditGroup = "isEditGroup";
        String groupDetail = "groupDetail";
        String jabberPrefix = "RideWhiz_";
        String SelectedChatUser = "SelectedChatUser";
    }

}

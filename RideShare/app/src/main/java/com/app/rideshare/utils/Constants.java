package com.app.rideshare.utils;

import com.app.rideshare.chat.MessageModel;

import java.util.ArrayList;

public class Constants {

    public static boolean isGroupDataUpdated = false;

    public interface intentKey {
        String MyGroup = "MyGroup";
        String isEditGroup = "isEditGroup";
        String groupDetail = "groupDetail";
        String jabberPrefix = "RideWhiz_";
        String SelectedChatUser = "SelectedChatUser";
    }
}
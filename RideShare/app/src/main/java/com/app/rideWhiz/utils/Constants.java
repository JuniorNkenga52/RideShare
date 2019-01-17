package com.app.rideWhiz.utils;

import com.app.rideWhiz.model.CarType;
import com.app.rideWhiz.model.ManageCar;

import java.util.List;

public class Constants {

    public static boolean isGroupDataUpdated = false;
    public static List<CarType> list_cartype;
    public static List<ManageCar> manageCarsList;

    public interface intentKey {
        String MyGroup = "MyGroup";
        String isEditGroup = "isEditGroup";
        String groupDetail = "groupDetail";
        String jabberPrefix = "RideWhiz_";
        String SelectedChatUser = "SelectedChatUser";
    }
}

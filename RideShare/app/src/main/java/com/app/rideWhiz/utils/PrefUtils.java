package com.app.rideWhiz.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.app.rideWhiz.model.GroupusersModel;
import com.app.rideWhiz.model.User;
import com.google.gson.Gson;

import java.util.ArrayList;

public class PrefUtils {

    private static final String PREFS_NAME = "RideWhiz";
    private static SharedPreferences SETTINGS = null;
    private static Editor EDITOR = null;
    public static String PREF_BATTERY_LEVEL="PREF_BATTERY_LEVEL";
    public static final String PREF_USER_INFO = "PREF_USER_INFO";

    public static void initPreference(Context context) {
        if (SETTINGS == null && EDITOR == null) {
            SETTINGS = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            EDITOR = SETTINGS.edit();
        }
    }

    public static boolean contains(String key) {
        return SETTINGS.contains(key);
    }

    public static void putInt(String key, int value) {
        EDITOR.putInt(key, value).commit();
    }

    public static int getInt(String key) {
        return SETTINGS.getInt(key, 0);
    }

    public static void putString(String key, String value) {
        EDITOR.putString(key, value).commit();
    }

    public static String getString(String key) {
        return SETTINGS.getString(key, "");
    }

    public static void putBoolean(String key, boolean value) {
        EDITOR.putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key) {
        return SETTINGS.getBoolean(key, false);
    }


    public static boolean getBooleantr(String key) {
        return SETTINGS.getBoolean(key, true);
    }

    public static void putLong(String key, long value) {
        EDITOR.putLong(key, value).commit();
    }

    public static Long getLong(String key) {
        return SETTINGS.getLong(key, 0);
    }

    public static void remove(String key) {
        if (contains(key))
            EDITOR.remove(key).commit();
    }

    public static void addUserInfo(User user) {
        putString(PREF_USER_INFO, new Gson().toJson(user));
    }

    public static User getUserInfo() {
        return new Gson().fromJson(getString(PREF_USER_INFO), User.class);
    }

    public static ArrayList<GroupusersModel> listAdminData;

    public static ArrayList<GroupusersModel> getAdminData() {
        return listAdminData;
    }
}
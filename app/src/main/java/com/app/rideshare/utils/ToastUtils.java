package com.app.rideshare.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void showShort(Context context, String errorMessage){
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int resId){
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String errorMessage){
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int resId){
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}

package com.app.rideshare.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideshare.R;

public class ToastUtils {

    private static final int TYPE_INTERNET = 0;
    private static final int TYPE_SUCCESS = 1;
    private static final int TYPE_INFO = 2;
    private static final int TYPE_FAIL = 3;

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

    public static void showSuccessMessage(Context context, String message) {
        showCustomToast(context, TYPE_SUCCESS, message);
    }

    public static void showInfoMessage(Context context, String message) {
        showCustomToast(context, TYPE_INFO, message);
    }

    public static void showFailureMessage(Context context, String message) {
        showCustomToast(context, TYPE_FAIL, message);
    }

    private static void showCustomToast(Context context, int type, String message) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView txt_toast_msg = (TextView) layout.findViewById(R.id.txt_toast_msg);
        txt_toast_msg.setText(message);

        ImageView img_toast_icon = (ImageView) layout.findViewById(R.id.img_toast_icon);

        switch (type) {

            case TYPE_SUCCESS:
                txt_toast_msg.setTextColor(Color.GREEN);
                break;
            case TYPE_FAIL:
                txt_toast_msg.setTextColor(Color.RED);
                break;
            case TYPE_INFO:
                txt_toast_msg.setTextColor(Color.BLUE);
                break;
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

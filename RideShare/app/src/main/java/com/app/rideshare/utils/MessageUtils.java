package com.app.rideshare.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideshare.R;

public class MessageUtils {

    private static final int TYPE_INTERNET = 0;
    private static final int TYPE_SUCCESS = 1;
    private static final int TYPE_WARNING = 2;
    private static final int TYPE_FAIL = 3;

    public static void showSuccessMessage(Context context, String message) {
        showCustomToast(context, TYPE_SUCCESS, message);
    }

    public static void showWarningMessage(Context context, String message) {
        showCustomToast(context, TYPE_WARNING, message);
    }

    public static void showFailureMessage(Context context, String message) {
        showCustomToast(context, TYPE_FAIL, message);
    }

    public static void showNoInternetAvailable(Context context) {
        showCustomToast(context, TYPE_INTERNET, context.getString(R.string.txt_msg_no_internet_available));
    }

    public static void showPleaseTryAgain(Context context) {
        showCustomToast(context, TYPE_FAIL, context.getString(R.string.txt_msg_please_try_again));
    }

    @SuppressLint("InflateParams")
    private static void showCustomToast(Context context, int type, String message) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_toast, null);

        TextView txt_toast_msg = layout.findViewById(R.id.txt_toast_msg);
        txt_toast_msg.setText(message);

        switch (type) {
            case TYPE_INTERNET:
                txt_toast_msg.setTextColor(Color.BLACK);
                txt_toast_msg.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen._5sdp));
                txt_toast_msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_internet, 0, 0, 0);
                break;
            case TYPE_SUCCESS:
                txt_toast_msg.setTextColor(Color.parseColor("#2B9C46"));
                break;
            case TYPE_FAIL:
                txt_toast_msg.setTextColor(Color.RED);
                break;
            case TYPE_WARNING:
                txt_toast_msg.setTextColor(context.getResources().getColor(R.color.blue));
                break;
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
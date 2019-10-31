package com.app.rideWhiz.utils;

import android.app.Dialog;

public interface CallbackCarType {
    void onSuccess(Dialog dialog, String cartype, String carid, String caricon, String ispooling);
    void onCancel(Dialog dialog);
    void onRequestType(String type);
}

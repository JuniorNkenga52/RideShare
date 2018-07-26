package com.app.rideshare.utils;

import android.app.Dialog;

public interface CallbackCarType {
    void onSuccess(Dialog dialog, String cartype, String carid, String caricon, String ispooling);
    void onCancel(Dialog dialog);
}

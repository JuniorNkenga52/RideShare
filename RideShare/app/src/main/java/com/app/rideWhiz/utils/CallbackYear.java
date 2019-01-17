package com.app.rideWhiz.utils;

import android.app.Dialog;

public interface CallbackYear {
    void onSuccess(Dialog dialog, String transmission);
    void onCancel(Dialog dialog);

}

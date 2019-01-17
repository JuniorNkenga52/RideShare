package com.app.rideWhiz.utils;

import android.app.Dialog;

public interface CallbackMessage {
    void onSuccess(Dialog dialog);
    void onCancel(Dialog dialog);
}

package com.app.rideWhiz.listner;

import android.app.Dialog;
import android.widget.TextView;

public interface CallbackStartRider {

    void onCreate(Dialog dialog);
    void onCancle(Dialog dialog);
    void onStart(Dialog dialog);
    void onChat(Dialog dialog);
}
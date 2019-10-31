package com.app.rideWhiz.listner;

import android.app.Dialog;

public interface CallbackFinishRider {

    void onCreate(Dialog dialog);
    void onCancle(Dialog dialog);
    void onError(String error,Dialog dialog);
    void onselectCarFeatures(String listoffeatures,String userID,String driverID,int pos, Dialog dialog);
}
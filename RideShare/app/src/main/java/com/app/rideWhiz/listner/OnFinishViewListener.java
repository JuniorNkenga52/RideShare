package com.app.rideWhiz.listner;

public interface OnFinishViewListener {
    void onAccepted(int pos);

    void onRejected(int pos);

    void onFinished();

}
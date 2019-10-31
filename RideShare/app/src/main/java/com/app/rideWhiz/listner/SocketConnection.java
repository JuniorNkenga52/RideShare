package com.app.rideWhiz.listner;

public interface SocketConnection {
    void onMessageReceived(String response);
    void onConnected();
}

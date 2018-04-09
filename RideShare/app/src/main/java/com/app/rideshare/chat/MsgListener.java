package com.app.rideshare.chat;

import org.jivesoftware.smack.packet.Message;

public interface MsgListener {
    void transferMessage(Message msg);
    void isTyping();
}
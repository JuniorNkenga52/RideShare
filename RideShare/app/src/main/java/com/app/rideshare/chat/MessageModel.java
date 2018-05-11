package com.app.rideshare.chat;

public class MessageModel {

    public final static String MEG_TYPE_TEXT = "TEXT";

    private String Sender;
    private String Receiver;
    private String MessageText;
    private String Type;
    private boolean IsMine;
    private int MsgIdl;
    private String Time;

    public MessageModel() {}

    public MessageModel(String Sender, String Receiver, String MessageText, String Type, boolean whoIsSender, int Id, String time) {
        this.Sender = Sender;
        this.Receiver = Receiver;
        this.MessageText = MessageText;
        this.Type = Type;
        this.IsMine = whoIsSender;
        this.MsgIdl = Id;
        this.Time = time;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getMessageText() {
        return MessageText;
    }

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getMsgIdl() {
        return MsgIdl;
    }

    public void setMsgIdl(int msgIdl) {
        MsgIdl = msgIdl;
    }

    public boolean isMine() {
        return IsMine;
    }

    public void setIsMine(boolean mine) {
        IsMine = mine;
    }

    public void setMine(boolean mine) {
        IsMine = mine;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
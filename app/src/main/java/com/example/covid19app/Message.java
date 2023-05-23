package com.example.covid19app;

public class Message {

    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    String message;
    String sentBy;

    //Click chuột trái và click generate để tạo
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
}

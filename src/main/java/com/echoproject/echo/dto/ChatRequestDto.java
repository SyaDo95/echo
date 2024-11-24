package com.echoproject.echo.dto;

public class ChatRequestDto {
    private String uid;
    private int botIndex;
    private String message;
    private String sender;

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getBotIndex() {
        return botIndex;
    }

    public void setBotIndex(int botIndex) {
        this.botIndex = botIndex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "ChatRequestDto{" +
                "uid='" + uid + '\'' +
                ", botIndex=" + botIndex +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}

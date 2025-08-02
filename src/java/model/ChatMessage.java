package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ChatMessage implements Serializable {
    private int messageID;
    private int userID;
    private String message;
    private String response;
    private Timestamp timestamp;
    private String messageType; // "user" hoặc "ai"
    private boolean isRead;

    // Constructors
    public ChatMessage() {
    }

    public ChatMessage(int userID, String message, String response, String messageType) {
        this.userID = userID;
        this.message = message;
        this.response = response;
        this.messageType = messageType;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.isRead = false;
    }

    public ChatMessage(int messageID, int userID, String message, String response, Timestamp timestamp, String messageType, boolean isRead) {
        this.messageID = messageID;
        this.userID = userID;
        this.message = message;
        this.response = response;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.isRead = isRead;
    }

    // Getters and Setters
    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "messageID=" + messageID + ", userID=" + userID + ", message=" + message + ", response=" + response + ", timestamp=" + timestamp + ", messageType=" + messageType + ", isRead=" + isRead + '}';
    }
} 
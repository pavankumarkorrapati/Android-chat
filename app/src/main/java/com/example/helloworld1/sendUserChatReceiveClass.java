package com.example.helloworld1;

public class sendUserChatReceiveClass {
    public String userID, message, time;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public sendUserChatReceiveClass(String userID, String message, String time) {
        this.userID = userID;
        this.message = message;
        this.time = time;
    }

    public sendUserChatReceiveClass() {

    }
}

package com.example.helloworld1;

public class retrieveFriendSearchClass {

    public String userName;
    public String sentDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public retrieveFriendSearchClass(String userName, String sentDate) {
        this.userName = userName;
        this.sentDate = sentDate;
    }

    public retrieveFriendSearchClass() {
    }
}

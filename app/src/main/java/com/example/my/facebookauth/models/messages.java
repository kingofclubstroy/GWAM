package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2016-11-12.
 */

public class messages {

    private String user;
    private String last_message;
    private long timestamp;

    public messages() {

    }

    public messages(String user, String last_message, long timestamp) {
        this.user = user;
        this.last_message = last_message;
        this.timestamp = timestamp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return  user;
    }

    public String getLast_message() {
        return last_message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

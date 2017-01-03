package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2016-11-12.
 */

public class user_messages {

    private String From;
    private String body;

    public user_messages() {

    }

    public user_messages(String From, String body) {
        this.From = From;
        this.body = body;
    }

    public void setFrom(String From) {
        this.From = From;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return From;
    }

    public String getBody() {
        return body;
    }
}

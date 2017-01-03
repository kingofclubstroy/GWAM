package com.example.my.facebookauth.models;

import java.util.ArrayList;

/**
 * Created by Owner on 2016-11-12.
 */


//// TODO: 2016-11-12 not completed
public class event {

    private String host;
    private String title;
    private String description;
    private String category;
    private ArrayList<String> invites;
    private String id;


    public event() {

    }

    public event(String host, String title, String description, String category, ArrayList<String> invites) {
        this.host = host;
        this.title = title;
        this.description = description;
        this.category = category;
        this.invites = invites;

    }

    public event(String host, String title, String description, String category, ArrayList<String> invites, String id) {
        this.host = host;
        this.title = title;
        this.description = description;
        this.category = category;
        this.invites = invites;
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setInvites(ArrayList<String> invites) {
        this.invites = invites;
    }

    public ArrayList<String> getInvites() {
        return invites;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2016-11-17.
 */

public class friend {

    private String name;
    private String id;

    public friend() {

    }

    public friend (String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

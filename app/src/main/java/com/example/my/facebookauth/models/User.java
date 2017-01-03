package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2016-11-11.
 */

//// TODO: 2016-11-17 change to public and private user objects 
public class User {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String photo;
    private boolean first_time;
    private String public_id;

    public User() {

    }

    public User (String id, String name, String phoneNumber, String email, String password, String photo, boolean first_time, String public_id) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.first_time = first_time;
        this.public_id = public_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return " ";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail (String email) {
        this.email = email;
    }
    
    //dont know if passwords should be set or got
    public  void setPassword (String password) {
        this.password = password;
    }

    public String getPassword () {
        return password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPublic_id() {
        return public_id;
    }
    //dont know if id's should be set
    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }
}

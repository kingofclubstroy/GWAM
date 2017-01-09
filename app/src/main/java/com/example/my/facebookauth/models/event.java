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
    private long startTime;
    private long endTime;
    private String startTimeString;
    private String endTimeString;


    public event() {

    }

    public event(String host, String title, String description, String category, ArrayList<String> invites) {
                 //long startTime, long endTime, String startTimeString, String endTimeString) {
        this.host = host;
        this.title = title;
        this.description = description;
        this.category = category;
        this.invites = invites;
        this.startTime = startTime;
        this.endTime = endTime;
        this.endTimeString = endTimeString;
        this.startTimeString = startTimeString;
    }

    public event(String host, String title, String description, String category, ArrayList<String> invites,
                 String id) { //long startTime, long endTime, String startTimeString, String endTimeString ) {
        this.host = host;
        this.title = title;
        this.description = description;
        this.category = category;
        this.invites = invites;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.endTimeString = endTimeString;
        this.startTimeString = startTimeString;
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

//    public long getEndTime() {
//        return endTime;
//    }
//
//    public long getStartTime() {
//        return startTime;
//    }
//
//    public String getEndTimeString() {
//        return endTimeString;
//    }
//
//    public String getStartTimeString() {
//        return startTimeString;
//    }
//
//    public void setEndTime(long endTime) {
//        this.endTime = endTime;
//    }
//
//    public void setStartTime(long startTime) {
//        this.startTime = startTime;
//    }
//
//    public void setEndTimeString(String endTimeString) {
//        this.endTimeString = endTimeString;
//    }
//
//    public void setStartTimeString(String startTimeString) {
//        this.startTimeString = startTimeString;
//    }
}

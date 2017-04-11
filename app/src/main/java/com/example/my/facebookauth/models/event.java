package com.example.my.facebookauth.models;

import android.util.Log;

import com.example.my.facebookauth.R;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.example.my.facebookauth.R.id.endTime;
import static com.example.my.facebookauth.R.id.startTime;

/**
 * Created by Owner on 2017-02-02.
 */

public class event {

        private String host;
        private String title;
        private String description;
        private String category;
        private ArrayList<String> invites;
        private String id;
        private String startTimeString;
        private String endTimeString;
        private long startTime;
        private long endTime;
        private String AMPM;
        private int red;
        private int green;
        private int blue;



        public event() {

        }

        public event(String host, String title, String description, String category, ArrayList<String> invites) {
            //long startTime, long endTime, String startTimeString, String endTimeString) {
            this.host = host;
            this.title = title;
            this.description = description;
            this.category = category;
            this.invites = invites;
        }

        public event(String host, String title, String description, String category, ArrayList<String> invites,
                     String id, long startTime, long endTime) { //long startTime, long endTime, String startTimeString, String endTimeString ) {
            this.host = host;
            this.title = title;
            this.description = description;
            this.category = category;
            this.invites = invites;
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;

            calculateStartString(startTime);

            calculateColorId(category);

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



    public void setEndTimeString(String endTimeString) {
        this.endTimeString = endTimeString;
    }

    public void calculateStartString(long startTime) {


        DateTime start = new DateTime(startTime);

        String AMPM = "AM";

        int startHour = start.getHourOfDay();

        int timeNumber = ((startHour) % 12);

        if (timeNumber == 0) {
            timeNumber = 12;
        }
        if (timeNumber >= 12) {
            AMPM = "PM";
        }

        int startMin = start.getMinuteOfHour();
        String addZero = "";
        if (startMin < 10) {

            addZero = "0";
        }

        this.startTimeString = "" + timeNumber + ":" + addZero + startMin;
        this.AMPM = AMPM;


    }


    public void setStartTimeString(String startTime) {

        this.startTimeString = startTime;

    }


    public String getEndTimeString() {
        return endTimeString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public DateTime startTime() {

        DateTime start = new DateTime(this.startTime);
        return start;
    }


    public DateTime endTime() {

        DateTime end = new DateTime(this.endTime);
        return end;
    }

    public String getAMPM() {
        return AMPM;
    }

    public void setAMPM(String AMPM) {
        this.AMPM = AMPM;
    }

    public void calculateColorId(String category) {

        switch (category) {

            case "Recreation":
                this.red = 59;
                this.green = 190;
                this.blue = 255;
                break;
            case "Food":
                this.red = 255;
                this.green = 124;
                this.blue = 124;
                break;
            case "Party":
                this.red = 183;
                this.green = 115;
                this.blue = 255;
                break;
            case "Games":
                this.red = 255;
                this.green = 200;
                this.blue = 45;
                break;
            case "Music":

                this.red = 74;
                this.green = 211;
                this.blue = 194;
                break;
        }


    }


    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getRed() {
        return red;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setRed(int red) {
        this.red = red;
    }
}


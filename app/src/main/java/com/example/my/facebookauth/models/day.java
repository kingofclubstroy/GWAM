package com.example.my.facebookauth.models;

import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Owner on 2017-02-02.
 */

public class day {

    private int startHour;
    private int endHour;
    private List<event> events;
    private List<eventBox> handledEvents;
    private List<block> blocks;
    private String dayString;
    private DateTime dayInfo;
    private List<hourText> hourText;
    private List<headerText> headerText;
    private ArrayList lines = new ArrayList();
    private float headerYbot;
    private float headerYtop;
    private float headerYBotBot;
    private float headerYBotTop;
    private boolean below = false;


    //for now these are used to separate event from paining information
    private List<eventBox> eventBoxes;


    public day() {

    }

    public day(int startHour, int endHour, List<event> events, DateTime dayInfo, String timeString) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.events = new ArrayList<>(events);
        this.dayInfo = dayInfo;
        this.dayString = timeString;
        this.blocks = new ArrayList<>();
        this.handledEvents = new ArrayList<>();
        this.headerYBotBot = 0;

    }


    public int getEndHour() {
        return endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public List<event> getEvents() {
        return events;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEventBoxes(List<eventBox> eventBoxes) {
        this.eventBoxes = eventBoxes;
    }

    public void setEvents(List<event> events) {
        this.events = events;
    }

    public DateTime getDayInfo() {
        return dayInfo;
    }

    public void setDayInfo(DateTime dayInfo) {
        this.dayInfo = dayInfo;
    }

    public List<eventBox> getEventBoxes() {
        return eventBoxes;
    }

    public void setHeaderText(List<com.example.my.facebookauth.models.headerText> headerText) {
        this.headerText = headerText;
    }

    public void setHourText(List<com.example.my.facebookauth.models.hourText> hourText) {
        this.hourText = hourText;
    }

    public List<com.example.my.facebookauth.models.headerText> getHeaderText() {
        return headerText;
    }

    public List<com.example.my.facebookauth.models.hourText> getHourText() {
        return hourText;
    }

    public void setLines(float line) {
        this.lines.add(line);
    }

    public ArrayList getLines() {
        return this.lines;
    }

    public float getHeaderYtop() {
        return headerYtop;
    }

    public float getHeaderYbot() {
        return headerYbot;
    }

    public float getHeaderYBotBot() {
        return headerYBotBot;
    }

    public float getHeaderYBotTop() {
        return headerYBotTop;
    }

    public void setHeaderYBotBot(float headerYBotBot) {
        this.headerYBotBot = headerYBotBot;
        this.headerYBotTop = headerYBotBot - 75;
    }

    public void setHeaderY(float headerY) {

        this.headerYbot = headerY;
        this.headerYtop = headerY - 75;

    }

    public void setBelow(boolean below) {
        this.below = below;
    }

    public boolean getBelow() {

        return this.below;
    }

    public void addBlock(float top, float bottom, int id, float startX) {

        block block = new block(top, bottom, startX);

        block.setId(id);

        this.blocks.add(block);



    }

    public List<block> getBlocks() {
        return blocks;
    }

    public List<eventBox> getHandledEvents() {
        return handledEvents;
    }


}

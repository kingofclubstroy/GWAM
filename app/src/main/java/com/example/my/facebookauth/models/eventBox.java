package com.example.my.facebookauth.models;

import android.graphics.RectF;

import static android.R.attr.endX;

/**
 * Created by Owner on 2017-02-04.
 */

public class eventBox {

    private float startX, Top, bottom, length, flagRight, poleBottom, poleRight;

    private event event;

    private RectF flag;

    private RectF pole;

    private int id = -1;

    public eventBox() {

    }

    public eventBox(event event, float startX, float Top, float length, float flagRight, float bottom, float poleBottom, float poleRight) {
        this.startX = startX;
        this.Top = Top;
        this.length = length;
        this.flagRight = flagRight;
        this.event = event;
        this.bottom = bottom;
        this.poleBottom = poleBottom;
        this.poleRight = poleRight;
    }

    public eventBox(eventBox copy) {
        this.startX = copy.getStartX();
        this.Top = copy.getTop();
        this.bottom = copy.getBottom();
        this.length = copy.getLength();
        this.flagRight = copy.getFlagRight();


        this.event = copy.getEvent();

        this.flag = copy.getflag();

    }




    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartX() {
        return startX;
    }

    public float getTop() {
        return Top;
    }

    public float getLength() {
        return length;
    }

    public float getFlagRight() {
        return flagRight;
    }


    public void setTop(float top) {
        this.Top = top;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void setFlagRight(float flagRight) {
        this.flagRight = flagRight;
    }

    public event getEvent() {
        return event;
    }

    public void setEvent(com.example.my.facebookauth.models.event event) {
        this.event = event;
    }




    public RectF getflag() {
        return flag;
    }

    public float getBottom() {
        return bottom;
    }

    public RectF getPole() {
        return pole;
    }


    public void setBottom(float bottom) {
        bottom = bottom;
    }

    public float getPoleBottom() {
        return poleBottom;
    }

    public void setOffset(float offset) {
        this.startX += offset;
        this.flagRight += offset;
    }

    public void setFlag() {
        this.flag = new RectF(this.startX, this.Top, this.flagRight, this.bottom);
    }

    public void setPole() {
        this.pole = new RectF(this.startX, this.bottom, this.poleRight, this.poleBottom);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

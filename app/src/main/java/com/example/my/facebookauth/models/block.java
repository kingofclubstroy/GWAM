package com.example.my.facebookauth.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 2017-03-02.
 */

public class block {

    private float top, bottom;
    private int id;
    private float offset;
    private float startX;
    private float endX;

    public block() {

    }

    public block(float top, float bottom, float startX) {
        this.top = top;
        this.bottom = bottom;
        this.startX = startX;
    }


    public float getBottom() {
        return bottom;
    }

    public float getTop() {
        return top;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public void addEvent(float bottom, float endX) {
        this.bottom = bottom;
        this.endX = endX;

    }

   public boolean collides(float top) {
       if (top < this.bottom) {
           return true;
       }
       return false;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOffset(float offset) {
        this.offset += offset;
    }

    public float getOffset() {
        return offset;
    }

    public float getEndX() {
        return endX;
    }

    public float getStartX() {
        return startX;
    }

    public void makeOffsetValue(float value) {
        this.offset = value;
    }

    //    public void addKey(int i) {
//        this.handledKeys.add(i);
//    }
}

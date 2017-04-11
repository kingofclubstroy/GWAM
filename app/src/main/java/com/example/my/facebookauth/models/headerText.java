package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2017-03-08.
 */

public class headerText {

    private float yStart;
    private String text;

    public headerText() {

    }

    public headerText(float yStart, String text) {

        this.text = text;
        this.yStart = yStart;
    }

    public float getyStart() {
        return yStart;
    }

    public String getText() {
        return text;
    }

    public void setyStart(float yStart) {
        this.yStart = yStart;
    }

    public void setText(String text) {
        this.text = text;
    }


}

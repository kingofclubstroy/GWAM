package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2017-03-08.
 */

public class hourText {

    private float yStart;
    private String numberText;
    private String AmPm;

    public hourText() {

    }

    public hourText(float yStart, String numberText, String AmPm) {

        this.yStart = yStart;
        this.numberText = numberText;
        this.AmPm = AmPm;

    }

    public float getyStart() {
        return yStart;
    }

    public String getAmPm() {
        return AmPm;
    }

    public String getNumberText() {
        return numberText;
    }

    public void setAmPm(String amPm) {
        AmPm = amPm;
    }

    public void setNumberText(String numberText) {
        this.numberText = numberText;
    }

    public void setyStart(float yStart) {
        this.yStart = yStart;
    }
}

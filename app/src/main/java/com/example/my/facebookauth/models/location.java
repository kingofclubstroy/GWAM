package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2016-11-22.
 */

public class location {
    private double lat;
    private double lon;
    private String city;
    private String country;

    public location() {

    }

    public location(double lat, double lon, String city, String country) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}


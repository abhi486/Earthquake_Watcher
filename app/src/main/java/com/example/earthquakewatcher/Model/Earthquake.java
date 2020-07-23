package com.example.earthquakewatcher.Model;

public class Earthquake {
    private String place;
    private double magnitude;
    private long time;
    private long date;
    private double lat;
    private double lgn;
    private String detailLink;
    private String type;

    public Earthquake(String place, long time, long date, double lat, double lgn, double magnitude, String detailLink, String type) {
        this.place = place;
        this.time = time;
        this.date = date;
        this.lat = lat;
        this.lgn = lgn;
        this.magnitude = magnitude;
        this.detailLink = detailLink;
        this.type = type;
    }

    public Earthquake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLgn() {
        return lgn;
    }

    public void setLgn(double lgn) {
        this.lgn = lgn;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

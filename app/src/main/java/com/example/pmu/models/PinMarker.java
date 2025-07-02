package com.example.pmu.models;

import java.util.Locale;

public class PinMarker {
    private double x;
    private double y;
    private String title;
    private String location;
    private String id;
    private String type;
    private String fromTime;
    private String toTime;


    public PinMarker(double x, double y, String title, String location, String id, String fromTime, String toTime) {
        this.x = x;
        this.y = y;
        this.title = title;
        this.location = location;
        this.id = id;
        this.toTime = toTime;
        this.fromTime = fromTime;

    }

    public PinMarker() {
        this.x = 0;
        this.y = 0;
        this.title = "";
        this.location = "";
        this.id = "";
        this.fromTime = "";
        this.toTime = "";

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId() {
        this.id = String.format(Locale.ENGLISH, "%f%f", this.x, this.y);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }
}

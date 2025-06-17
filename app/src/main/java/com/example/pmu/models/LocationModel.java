package com.example.pmu.models;

import java.util.Date;

public class LocationModel {

    private String place;
    private String category;
    private String fromDate;
    private String toDate;


    public LocationModel(String place, String category, String fromDate, String toDate) {
        this.place = place;
        this.category = category;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocationModel(){
        this.place = "";
        this.category = "";
        this.toDate = "";
        this.fromDate = "";
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}

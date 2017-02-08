package com.example.razvan.socialeventshelper.Models;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsModel {
    private String title;
    private String coverPhoto;
    private String takingPlace;
    private String eventDay;
    private String eventMonth;
    private String eventHour;

    public MainEventsModel() {
    }

    public MainEventsModel(String title,String coverPhoto,String takingPlace,String eventDay,String eventMonth,String eventHour) {
        this.title = title;
        this.coverPhoto = coverPhoto;
        this.takingPlace = takingPlace;
        this.eventDay = eventDay;
        this.eventMonth = eventMonth;
        this.eventHour = eventHour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getTakingPlace() {
        return takingPlace;
    }

    public void setTakingPlace(String takingPlace) {
        this.takingPlace = takingPlace;
    }

    public String getEventDay() {
        return eventDay;
    }

    public void setEventDay(String eventDay) {
        this.eventDay = eventDay;
    }

    public String getEventMonth() {
        return eventMonth;
    }

    public void setEventMonth(String eventMonth) {
        this.eventMonth = eventMonth;
    }

    public String getEventHour() {
        return eventHour;
    }

    public void setEventHour(String eventHour) {
        this.eventHour = eventHour;
    }
}

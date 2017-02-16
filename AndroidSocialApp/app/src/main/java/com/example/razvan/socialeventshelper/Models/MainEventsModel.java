package com.example.razvan.socialeventshelper.Models;

import java.io.Serializable;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsModel implements Serializable {
    private String title;
    private String coverPhoto;
    private String takingPlace;
    private String eventDay;
    private String eventMonth;
    private String eventHour;
    private String eventDescription;

    public MainEventsModel() {
    }

    public MainEventsModel(String title,String coverPhoto,String takingPlace,String eventDay,String eventMonth,String eventHour,String eventDescription) {
        this.title = title;
        this.coverPhoto = coverPhoto;
        this.takingPlace = takingPlace;
        this.eventDay = eventDay;
        this.eventMonth = eventMonth;
        this.eventHour = eventHour;
        this.eventDescription = eventDescription;
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

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}

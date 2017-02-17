package com.example.razvan.socialeventshelper.Models;

import java.io.Serializable;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsModel implements Serializable {
    private String eventTitle;
    private String eventCoverPhoto;
    private String eventTakingPlace;
    private String eventDay;
    private String eventMonth;
    private String eventHour;
    private String eventDescription;

    public MainEventsModel() {
    }

    public MainEventsModel(String eventTitle,String eventCoverPhoto,String eventTakingPlace,String eventDay,String eventMonth,String eventHour,String eventDescription) {
        this.eventTitle = eventTitle;
        this.eventCoverPhoto = eventCoverPhoto;
        this.eventTakingPlace = eventTakingPlace;
        this.eventDay = eventDay;
        this.eventMonth = eventMonth;
        this.eventHour = eventHour;
        this.eventDescription = eventDescription;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventCoverPhoto() {
        return eventCoverPhoto;
    }

    public void setEventCoverPhoto(String eventCoverPhoto) {
        this.eventCoverPhoto = eventCoverPhoto;
    }

    public String getEventTakingPlace() {
        return eventTakingPlace;
    }

    public void setEventTakingPlace(String eventTakingPlace) {
        this.eventTakingPlace = eventTakingPlace;
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

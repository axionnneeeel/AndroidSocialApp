package com.example.razvan.socialeventshelper.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsModel implements Parcelable {
    private String eventTitle;
    private String eventCoverPhoto;
    private String eventTakingPlace;
    private String eventDay;
    private String eventMonth;
    private String eventHour;
    private String eventDescription;
    private Double eventLatitude;
    private Double eventLongitude;

    public MainEventsModel() {
    }

    public MainEventsModel(Parcel source){
        eventTitle = source.readString();
        eventCoverPhoto = source.readString();
        eventTakingPlace = source.readString();
        eventDay = source.readString();
        eventMonth = source.readString();
        eventHour = source.readString();
        eventDescription = source.readString();
        eventLatitude = source.readDouble();
        eventLongitude = source.readDouble();
    }

    public MainEventsModel(String eventTitle,String eventCoverPhoto,String eventTakingPlace,String eventDay,String eventMonth,String eventHour,String eventDescription,Double eventLatitude,Double eventLongitude) {
        this.eventTitle = eventTitle;
        this.eventCoverPhoto = eventCoverPhoto;
        this.eventTakingPlace = eventTakingPlace;
        this.eventDay = eventDay;
        this.eventMonth = eventMonth;
        this.eventHour = eventHour;
        this.eventDescription = eventDescription;
        this.eventLatitude = eventLatitude;
        this.eventLongitude = eventLongitude;
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

    public Double getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(Double eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public Double getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(Double eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventTitle);
        parcel.writeString(eventCoverPhoto);
        parcel.writeString(eventTakingPlace);
        parcel.writeString(eventDay);
        parcel.writeString(eventMonth);
        parcel.writeString(eventHour);
        parcel.writeString(eventDescription);
        parcel.writeDouble(eventLatitude);
        parcel.writeDouble(eventLongitude);
    }

    public static final Parcelable.Creator<MainEventsModel> CREATOR = new Parcelable.Creator<MainEventsModel>() {
        public MainEventsModel createFromParcel(Parcel in) {
            return new MainEventsModel(in);
        }

        public MainEventsModel[] newArray(int size) {
            return new MainEventsModel[size];
        }
    };
}

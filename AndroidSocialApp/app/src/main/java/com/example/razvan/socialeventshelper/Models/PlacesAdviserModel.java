package com.example.razvan.socialeventshelper.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Razvan on 2/7/2017.
 */

public class PlacesAdviserModel {

    private String placeName;
    private String placeStreet;
    private String placeRating;
    private String placeOpenNow;
    private double placeLatitude;
    private double placeLongitude;

    public PlacesAdviserModel() {
    }


    public PlacesAdviserModel(String placeName,String placeStreet,String placeRating,String placeOpenNow,double placeLatitude,double placeLongitude) {
        this.placeName = placeName;
        this.placeStreet = placeStreet;
        this.placeRating = placeRating;
        this.placeOpenNow = placeOpenNow;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceStreet() {
        return placeStreet;
    }

    public void setPlaceStreet(String placeStreet) {
        this.placeStreet = placeStreet;
    }

    public String getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(String placeRating) {
        this.placeRating = placeRating;
    }

    public String getPlaceOpenNow() {
        return placeOpenNow;
    }

    public void setPlaceOpenNow(String placeOpenNow) {
        this.placeOpenNow = placeOpenNow;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }
}

package com.example.razvan.socialeventshelper.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Razvan on 2/7/2017.
 */

public class PlacesAdviserModel {

    private String placeName;

    public PlacesAdviserModel() {
    }


    public PlacesAdviserModel(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}

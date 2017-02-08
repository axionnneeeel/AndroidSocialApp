package com.example.razvan.socialeventshelper.Models;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsModel {
    private String title;
    private String coverPhoto;

    public MainEventsModel() {
    }

    public MainEventsModel(String title,String coverPhoto) {
        this.title = title;
        this.coverPhoto = coverPhoto;
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
}

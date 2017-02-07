package com.example.razvan.socialeventshelper.Models;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsModel {
    private String title;

    public MainEventsModel() {
    }

    public MainEventsModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }
}

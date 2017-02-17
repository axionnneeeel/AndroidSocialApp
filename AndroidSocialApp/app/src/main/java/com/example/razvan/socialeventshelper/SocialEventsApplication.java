package com.example.razvan.socialeventshelper;

import android.app.Application;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Razvan on 1/4/2017.
 */

public class SocialEventsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
    }
}

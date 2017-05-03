package com.example.razvan.socialeventshelper;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.facebook.appevents.AppEventsLogger;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Razvan on 1/4/2017.
 */

public class SocialEventsApplication extends Application {

    private static SocialEventsApplication singleton;

    public static SocialEventsApplication getInstance() {
        return singleton;
    }

    public void updateEventsList(List<MainEventsModel> eventListt) {
        this.eventsList = eventListt;
    }

    private LocationManager locationManager;
    private List<MainEventsModel> eventsList = new ArrayList<>();
    private NotificationManager notificationManager;
    private Uri soundUri;
    private NotificationCompat.Builder mBuilder;
    private long times[] = new long[100] ;
    private final Long THIRTY_MINUTES_IN_MILI = 1800000L;
    private Socket serverSocket;


    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
        singleton = this;

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        connectToServer();
    }

    public void registerGPS() {

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (!eventsList.isEmpty()) {
                    for (int i=0;i<eventsList.size();i++) {
                        if (times[i] == 0 || (times[i] != 0 && times[i] < System.currentTimeMillis() - THIRTY_MINUTES_IN_MILI)) {
                            times[i] = System.currentTimeMillis();
                            Location eventLocation = new Location(" ");
                            eventLocation.setLatitude(eventsList.get(i).getEventLatitude());
                            eventLocation.setLongitude(eventsList.get(i).getEventLongitude());

                            float distanceToEvent = eventLocation.distanceTo(location);
                            if (distanceToEvent < 700) {
                                mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.mipmap.ic_notification)
                                        .setContentTitle("Alerta event in apropiere")
                                        .setContentText("\"" + eventsList.get(i).getEventTitle() + "\" in " + (int) distanceToEvent + " metri.")
                                        .setSound(soundUri);
                                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                                notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), mBuilder.build());
                            }
                        }
                    }
                }
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void connectToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new Socket("192.168.2.101", 8080);
                }catch(IOException e){
                    Toast.makeText(getApplicationContext(),"Connection to server failed. Try to restart the application.",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    public Socket getServerSocket(){
        return serverSocket;
    }

}

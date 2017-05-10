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
import com.example.razvan.socialeventshelper.Events.MainEventsModel;
import com.facebook.appevents.AppEventsLogger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    private final Long THIRTY_MINUTES_IN_MILI = 1800000L;
    private long times[] = new long[100] ;

    private List<MainEventsModel> eventsList = new ArrayList<>();

    private LocationManager locationManager;
    private NotificationManager notificationManager;
    private Uri soundUri;
    private NotificationCompat.Builder mBuilder;
    
    private Socket serverSocket;
    private ServerCommunication server = null;
    private DataOutputStream output;
    private DataInputStream input;

    private Integer isChatOpen = 0;


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
                    serverSocket = new Socket("192.168.2.100", 8080);
                    listenForMessages();
                }catch(IOException e){
                    Toast.makeText(getApplicationContext(),"Connection to server failed. Try to restart the application.",Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void listenForMessages(){
        server = new ServerCommunication(serverSocket);
        try {
            output = new DataOutputStream(this.serverSocket.getOutputStream());
            input = new DataInputStream(this.serverSocket.getInputStream());

            output.write(0);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Integer value = input.read();
                        if (value == 1) {
                            server.checkIfLogged();
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 2){
                            server.registerOrCheckRegisterValidity();
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 3){
                            server.getUserCredentials();
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 4){
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 5){
                            server.getUserFriends();
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 6){
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 7){
                            server.sendUserToBeAdded();
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 8){
                            server.getConversation();
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 9){
                            server.setWaitForThreadFinish(true);
                        }
                        else if(value == 10){
                            Integer userId = input.readInt();
                            String userName = input.readUTF();
                            String userMessage = input.readUTF();
                            server.setUserNewMessages(userName,userMessage);
                            server.setWaitForThreadFinish(true);

                            if(isChatOpen == 0) {
                                mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.mipmap.ic_notification)
                                        .setContentTitle("New message")
                                        .setContentText(userName + " : " + userMessage)
                                        .setSound(soundUri);
                                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                                notificationManager.notify(userId, mBuilder.build());
                                server.setWaitForThreadFinish(false);
                            }

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Socket getServerSocket(){
        return serverSocket;
    }

    public ServerCommunication getServer() {
        return server;
    }

    public void setServer(ServerCommunication server) {
        this.server = server;
    }

    public Integer getIsChatOpen() {
        return isChatOpen;
    }

    public void setIsChatOpen(Integer isChatOpen) {
        this.isChatOpen = isChatOpen;
    }

    public void updateEventsList(List<MainEventsModel> eventListt) {
        this.eventsList = eventListt;
    }
}

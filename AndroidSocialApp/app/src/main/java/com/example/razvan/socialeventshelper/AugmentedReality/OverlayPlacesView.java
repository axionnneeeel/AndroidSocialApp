package com.example.razvan.socialeventshelper.AugmentedReality;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Models.PlacesAdviserModel;
import com.example.razvan.socialeventshelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razvan on 3/11/2017.
 */


@SuppressWarnings("deprecation")
public class OverlayPlacesView extends View implements SensorEventListener, LocationListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    static final float ALPHA = 0.15f;

    private final Context context;

    private LocationManager locationManager = null;
    private SensorManager sensorsManager = null;

    private Location lastLocation;
    private float[] lastAccelerometer;
    private float[] lastCompass;

    private float verticalViewAngle;
    private float horizontalViewAngle;

    private Sensor accelerometerSensor;
    private Sensor compassSensor;
    private Sensor gyroscopeSensor;

    private Paint targetPaint;
    private ArrayList<PlacesAdviserModel> myPlaces;
    private List<Float> allPlacesBearings = new ArrayList<>();

    private Location placeLocation = new Location("EVENT LOCATION");
    private Location placeLocationCard = new Location("EVENT LOCATION CARD");

    private float rotation[] = new float[9];
    private float identity[] = new float[9];
    private float cameraRotation[] = new float[9];
    private float orientation[] = new float[3];
    private float scale;

    private CardView view ;
    private TextView placeName;
    private TextView placeStreet;
    private TextView placeIsOpen;
    private TextView distanceToLocation;


    public OverlayPlacesView(Context context, ArrayList<PlacesAdviserModel> events) {
        super(context);
        this.context = context;
        this.myPlaces = events;

        scale = context.getResources().getDisplayMetrics().density;
        inflateEventsView();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        sensorsManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorsManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compassSensor = sensorsManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = sensorsManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        registerSensors();
        registerGPS();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_CAMERA);
        Camera camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        verticalViewAngle = params.getVerticalViewAngle();
        horizontalViewAngle = params.getHorizontalViewAngle();
        camera.release();

        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setColor(Color.GREEN);

    }

    public void modifyEvents(ArrayList<PlacesAdviserModel> newPlaces){
        myPlaces = newPlaces;
    }

    private void registerSensors() {
        sensorsManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorsManager.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorsManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void registerGPS() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 0, this);

    }

    private void inflateEventsView(){
        final LayoutInflater factory = LayoutInflater.from(context);
        final View eventsView = factory.inflate(R.layout.augmented_reality_event_card, null);
        view = (CardView) eventsView.findViewById(R.id.cardView);
        placeName = (TextView) eventsView.findViewById(R.id.event_title);
        placeStreet = (TextView) eventsView.findViewById(R.id.event_street);
        placeIsOpen = (TextView) eventsView.findViewById(R.id.event_start_time);
        distanceToLocation = (TextView) eventsView.findViewById(R.id.distance_to);
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null )
            return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        allPlacesBearings.clear();

        if (lastLocation != null) {

            for(PlacesAdviserModel eachEvent : myPlaces) {
                placeLocation.setLatitude(eachEvent.getPlaceLatitude());
                placeLocation.setLongitude(eachEvent.getPlaceLongitude());
                float bearingToEvent = lastLocation.bearingTo(placeLocation);
                allPlacesBearings.add(bearingToEvent);
            }
        }

        if (lastAccelerometer != null && lastCompass != null) {
            boolean gotRotation = SensorManager.getRotationMatrix(rotation, identity, lastAccelerometer, lastCompass);

            if (gotRotation) {
                SensorManager.remapCoordinateSystem(rotation, SensorManager.AXIS_X, SensorManager.AXIS_Z, cameraRotation);
                SensorManager.getOrientation(cameraRotation, orientation);

                canvas.save();
                canvas.rotate((float) (0.0f - Math.toDegrees(orientation[2])));

                for(int i=0 ; i<allPlacesBearings.size() ; i++) {
                    float dxAxePlacement = (float) ((canvas.getWidth() / horizontalViewAngle) * (Math.toDegrees(orientation[0]) - allPlacesBearings.get(i)));
                    float dyAxePlacement = (float) ((canvas.getHeight() / verticalViewAngle) * Math.toDegrees(orientation[1]));

                    canvas.translate(0.0f, 0.0f - dyAxePlacement);
                    canvas.translate(0.0f - dxAxePlacement, 0.0f);

                    placeName.setText(myPlaces.get(i).getPlaceName());
                    placeStreet.setText(myPlaces.get(i).getPlaceStreet());
                    placeIsOpen.setText("");
                    placeLocationCard.setLatitude(myPlaces.get(i).getPlaceLatitude());
                    placeLocationCard.setLongitude(myPlaces.get(i).getPlaceLongitude());
                    distanceToLocation.setText(Float.toString(lastLocation.distanceTo(placeLocationCard)/1000).substring(0,4)+" km");

                    view.setDrawingCacheEnabled(true);
                    view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                    view.buildDrawingCache();


                    Bitmap eventCardBitmap = Bitmap.createScaledBitmap(view.getDrawingCache(), Math.round(scale)*170, Math.round(scale)*80, true);
                    canvas.drawBitmap(eventCardBitmap, canvas.getWidth() / 2 - (85*Math.round(scale)), canvas.getHeight() / 2 - (40*Math.round(scale)), targetPaint);
                    canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 8.0f, targetPaint);

                    canvas.restore();
                    canvas.save();
                }
            }
        }

        canvas.save();
        canvas.translate(15.0f, 15.0f);
        canvas.restore();
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                lastAccelerometer = lowPass(event.values.clone(),lastAccelerometer);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lastCompass = lowPass(event.values.clone(),lastCompass);
                break;
        }

        this.invalidate();
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    public void onProviderDisabled(String provider) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onPause() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        locationManager.removeUpdates(this);
        sensorsManager.unregisterListener(this);
    }

    public void onResume() {
        registerSensors();
        registerGPS();
    }
}
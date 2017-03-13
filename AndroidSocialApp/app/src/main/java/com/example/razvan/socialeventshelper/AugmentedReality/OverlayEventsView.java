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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.razvan.socialeventshelper.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Razvan on 3/11/2017.
 */


@SuppressWarnings("deprecation")
public class OverlayEventsView extends View implements SensorEventListener, LocationListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    static final float ALPHA = 0.25f;

    private final Context context;

    private final static Location mountWashington = new Location("manual");
    static {
        mountWashington.setLatitude(46.6383);
        mountWashington.setLongitude(27.7292);
    }

    String accelerometerData = "Accelerometer Data";
    String compassData = "Compass Data";
    String gyroscopeData = "Gyro Data";

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

    private TextPaint contentPaint;

    private Paint targetPaint;

    public OverlayEventsView(Context context) {
        super(context);
        this.context = context;

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

        contentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Paint.Align.LEFT);
        contentPaint.setTextSize(20);
        contentPaint.setColor(Color.RED);

        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setColor(Color.GREEN);

    }

    private void registerSensors() {
        sensorsManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorsManager.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorsManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void registerGPS() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

        String best = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        locationManager.requestLocationUpdates(best, 50, 0, this);
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

        float curBearingToMW = 0.0f;

        StringBuilder text = new StringBuilder(accelerometerData).append("\n");
        text.append(compassData).append("\n");
        text.append(gyroscopeData).append("\n");

        if (lastLocation != null) {
            text.append(
                    String.format("GPS = (%.3f, %.3f) @ (%.2f meters up)",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude(),
                            lastLocation.getAltitude())).append("\n");

            curBearingToMW = lastLocation.bearingTo(mountWashington);

            text.append(String.format("Bearing to MW: %.3f", curBearingToMW)).append("\n");
        }

        float rotation[] = new float[9];
        float identity[] = new float[9];
        if (lastAccelerometer != null && lastCompass != null) {
            boolean gotRotation = SensorManager.getRotationMatrix(rotation, identity, lastAccelerometer, lastCompass);

            if (gotRotation) {
                float cameraRotation[] = new float[9];
                SensorManager.remapCoordinateSystem(rotation, SensorManager.AXIS_X, SensorManager.AXIS_Z, cameraRotation);

                float orientation[] = new float[3];
                SensorManager.getOrientation(cameraRotation, orientation);

                text.append(
                        String.format("Orientation (%.3f, %.3f, %.3f)",
                                Math.toDegrees(orientation[0]), Math.toDegrees(orientation[1]), Math.toDegrees(orientation[2])))
                        .append("\n");

                canvas.save();
                canvas.rotate((float) (0.0f - Math.toDegrees(orientation[2])));

                float dxAxePlacement = (float) ((canvas.getWidth() / horizontalViewAngle) * (Math.toDegrees(orientation[0]) - curBearingToMW));
                float dyAxePlacement = (float) ((canvas.getHeight() / verticalViewAngle) * Math.toDegrees(orientation[1]));

                canvas.translate(0.0f, 0.0f - dyAxePlacement);
                canvas.drawLine(0f - canvas.getHeight(), canvas.getHeight() / 2, canvas.getWidth() + canvas.getHeight(), canvas.getHeight() / 2, targetPaint);

                canvas.translate(0.0f - dxAxePlacement, 0.0f);
                canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 8.0f, targetPaint);

                final LayoutInflater factory = LayoutInflater.from(context);
                final View textEntryView = factory.inflate(R.layout.augmented_reality_event_card, null);
                CardView view = (CardView) textEntryView.findViewById(R.id.cardView);
                view.setDrawingCacheEnabled(true);
                view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

                view.buildDrawingCache();
                Bitmap bma = Bitmap.createBitmap(view.getDrawingCache());

                float scale = context.getResources().getDisplayMetrics().density;
                Bitmap bmaa = Bitmap.createScaledBitmap(bma,500,100 ,true);
                view.setDrawingCacheEnabled(false);
                canvas.drawBitmap(bmaa,canvas.getWidth() / 2, canvas.getHeight() / 2,targetPaint);

                //canvas.save();
                canvas.restore();
            }
        }

        canvas.save();
        canvas.translate(15.0f, 15.0f);
        StaticLayout textBox = new StaticLayout(text.toString(), contentPaint,
                480, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        textBox.draw(canvas);
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
            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        locationManager.removeUpdates(this);
        sensorsManager.unregisterListener(this);
    }

    public void onResume() {
        registerSensors();
        registerGPS();
    }
}
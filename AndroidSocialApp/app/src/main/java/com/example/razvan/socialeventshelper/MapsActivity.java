package com.example.razvan.socialeventshelper;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Models.PlacesAdviserModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import butterknife.ButterKnife;


/**
 * Created by Razvan on 2/20/2017.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap googleMaps;
    private CameraPosition cameraPosition;
    private GoogleApiClient googleClient;

    private Location lastLocation;
    private final LatLng defaultLocation = new LatLng(47.151726, 27.587914);

    private static final int DEFAULT_ZOOM = 12;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private ProgressDialog dialog;

    private ArrayList<MainEventsModel> eventsList = new ArrayList<>();
    private ArrayList<PlacesAdviserModel> placesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog=new ProgressDialog(this);
        dialog.setMessage("Putting markers on the map...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        if (savedInstanceState != null) {
            lastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        googleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleClient.connect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMaps != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMaps.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMaps = map;

        putEventsMarker();
        moveAndSaveDeviceLocation();
        updateLocationUI();

        dialog.dismiss();
    }

    private void moveAndSaveDeviceLocation() {
        Location currentLocation = getIntent().getParcelableExtra("location");
        lastLocation = currentLocation;

        LatLng myCoord = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        googleMaps.addMarker(new MarkerOptions().position(myCoord).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        if (cameraPosition != null) {
            googleMaps.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (lastLocation != null) {
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(),
                            lastLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.i("MAPS", "Current location is null. Using defaults.");
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            googleMaps.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void updateLocationUI() {

    }

    public void putEventsMarker(){
        
        if(getIntent().hasExtra("all_events")) {
            eventsList = getIntent().getParcelableArrayListExtra("all_events");

            for (MainEventsModel currentPlace : eventsList) {
                LatLng currentEventCoord = new LatLng(currentPlace.getEventLatitude(), currentPlace.getEventLongitude());
                googleMaps.addMarker(new MarkerOptions().position(currentEventCoord).title(currentPlace.getEventTitle()));
            }
        }
        else if(getIntent().hasExtra("all_places")){
            placesList = getIntent().getParcelableArrayListExtra("all_places");

            for (PlacesAdviserModel currentPlace : placesList) {
                LatLng currentEventCoord = new LatLng(currentPlace.getPlaceLatitude(), currentPlace.getPlaceLongitude());
                googleMaps.addMarker(new MarkerOptions().position(currentEventCoord).title(currentPlace.getPlaceName()));
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i("MAPS",result.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("MAPS", "Play services connection suspended");
    }

}

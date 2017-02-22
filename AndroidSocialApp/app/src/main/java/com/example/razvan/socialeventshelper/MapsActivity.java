package com.example.razvan.socialeventshelper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Razvan on 2/20/2017.
 */

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMaps;
    private CameraPosition cameraPosition;
    private GoogleApiClient googleClient;

    private boolean hasPermission;
    private Location lastLocation;
    private final LatLng defaultLocation = new LatLng(47.151726, 27.587914);

    private static final int DEFAULT_ZOOM = 8;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
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
        updateLocationUI();
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (hasPermission) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    lastLocation = location;
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

                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        hasPermission = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                }
            }
        }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (googleMaps == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (hasPermission) {
            googleMaps.setMyLocationEnabled(true);
            googleMaps.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            googleMaps.setMyLocationEnabled(false);
            googleMaps.getUiSettings().setMyLocationButtonEnabled(false);
            lastLocation = null;
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

package com.example.razvan.socialeventshelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.razvan.socialeventshelper.Adapters.MainEventsAdapter;
import com.example.razvan.socialeventshelper.AugmentedReality.AugmentedRealityActivity;
import com.example.razvan.socialeventshelper.Chatbot.ChatbotActivity;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Utils.GeneralUtils;
import com.example.razvan.socialeventshelper.Utils.MapUtil;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsActivity extends AppCompatActivity {

    @BindView(R.id.swipe_refresh_recyclerView)
    RecyclerView eventsView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.events_option)
    LinearLayout eventsOption;

    private final long TEN_DAYS_IN_SECONDS = 864000;
    private final long LOCATION_REFRESH_TIME = 0;
    private final long LOCATION_REFRESH_DISTANCE = 3000;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private MainEventsAdapter eventsAdapter;
    private List<MainEventsModel> eventsList = new ArrayList<>();
    private ProgressDialog dialog;

    private Location currentLocation;

    private String currentCity;
    private String currentCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);
        ButterKnife.bind(this);

        eventsOption.setBackgroundColor(ContextCompat.getColor(MainEventsActivity.this,R.color.colorPrimaryTransp));

        dialog=new ProgressDialog(this);
        dialog.setMessage("Events fetching..");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        findLocationAndCallEvents();
    }

    public void findLocationAndCallEvents() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                currentLocation = location;

                eventsList.clear();
                Geocoder gcd = new Geocoder(MainEventsActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                currentCity = addresses.get(0).getAdminArea();
                currentCountry = addresses.get(0).getCountryName();
                initiateEvents(currentCity);
                toolbarTitle.setText(currentCity+", "+currentCountry);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        SocialEventsApplication.getInstance().registerGPS();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
    }

    private void initiateEvents(final String cityName){
        eventsAdapter = new MainEventsAdapter(eventsList, this, new MainEventsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainEventsModel item) {
                Intent detailsIntent = new Intent(MainEventsActivity.this, EventDetailsActivity.class);
                detailsIntent.putExtra("my_event", item);
                startActivity(detailsIntent);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        eventsView.setLayoutManager(mLayoutManager);
        eventsView.setItemAnimator(new DefaultItemAnimator());
        eventsView.setAdapter(eventsAdapter);

        eventsView.setHasFixedSize(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                eventsList.clear();
                if (GeneralUtils.isNetworkAvailable(MainEventsActivity.this))
                    fetchEvents(cityName);
                else {
                    Toast.makeText(MainEventsActivity.this, "No internet connection. Open it and refresh.", Toast.LENGTH_LONG).show();
                    refreshLayout.setRefreshing(false);
                }
            }
        });

        if (GeneralUtils.isNetworkAvailable(MainEventsActivity.this))
            fetchEvents(cityName);
        else
            Toast.makeText(this, "No internet connection. Open it and refresh.", Toast.LENGTH_LONG).show();
    }

    private void fetchEvents(String cityName) {
        long currentTimeMilliSeconds = System.currentTimeMillis();
        long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeMilliSeconds);

        long timeAfter10Days = currentTimeSeconds + TEN_DAYS_IN_SECONDS;

        GraphRequestAsyncTask task = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/search?q="+cityName+"&type=event&since=" + currentTimeSeconds + "&until=" + timeAfter10Days + "&access_token="
                        + this.getString(R.string.explorer_token),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        transformToJSONAndSortByDate(response);
                    }
                }
        ).executeAsync();
        Log.i("fetchev", task.toString());
    }

    private void transformToJSONAndSortByDate(GraphResponse response) {
        try {
            JSONObject resultJSONObj = response.getJSONObject();
            JSONArray resultJSON = resultJSONObj.getJSONArray("data");
            Map<String, String> eventsIdsTimeSorted = new HashMap<>(100);

            for (int eachJSON = 0; eachJSON < resultJSON.length(); eachJSON++) {
                JSONObject currentObject = resultJSON.getJSONObject(eachJSON);
                if (currentObject.toString().contains("\"description\"") && currentObject.toString().contains("\"place\"") && currentObject.toString().contains("\"location\""))
                    eventsIdsTimeSorted.put(currentObject.getString("id"), currentObject.getString("start_time"));
            }

            eventsIdsTimeSorted = MapUtil.sortByValue(eventsIdsTimeSorted);
            fetchEventsInformation(eventsIdsTimeSorted);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchEventsInformation(final Map<String, String> eventsSorted) {
        StringBuilder allEventIds = new StringBuilder();
        for (Map.Entry<String, String> eachEvent : eventsSorted.entrySet()) {
            allEventIds.append(eachEvent.getKey() + ",");
        }
        allEventIds.setLength(allEventIds.length() - 1);

        String finalIds = allEventIds.toString();
        GraphRequestAsyncTask task = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "?ids=" + finalIds + "&fields=name,place,start_time,cover,description&access_token="
                        + this.getString(R.string.explorer_token),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        transformToJSONAndShow(response, eventsSorted);
                    }
                }
        ).executeAsync();
        Log.i("fetchinfo", task.toString());
    }

    private void transformToJSONAndShow(GraphResponse response, Map<String, String> eventsSorted) {
        try {
            JSONObject resultJSONObj = response.getJSONObject();
            for (Map.Entry<String, String> eachEvent : eventsSorted.entrySet()) {
                String eachEventDataString = resultJSONObj.getString(eachEvent.getKey());

                JSONObject eachEventDataJSON = new JSONObject(eachEventDataString);
                if(eachEventDataJSON.toString().contains("\"cover\"")) {
                    String eventTitle = eachEventDataJSON.getString("name");

                    String takingPlaceString = eachEventDataJSON.getString("place");
                    JSONObject takingPlaceJSON = new JSONObject(takingPlaceString);
                    String takingPlace = takingPlaceJSON.getString("name");

                    String evLatLongString = takingPlaceJSON.getString("location");
                    JSONObject evLatLongJSON = new JSONObject(evLatLongString);
                    String evLat = evLatLongJSON.getString("latitude");
                    String evLong = evLatLongJSON.getString("longitude");

                    String coverPhotoString = eachEventDataJSON.getString("cover");
                    JSONObject coverPhotoJSON = new JSONObject(coverPhotoString);
                    String coverPhoto = coverPhotoJSON.getString("source");

                    String eventTime = eachEventDataJSON.getString("start_time");
                    String[] splittedTime = eventTime.split("-");
                    String eventDay = splittedTime[2].substring(0, 2);
                    String eventMonth = GeneralUtils.getMonthNameFromNumber(splittedTime[1]);
                    String eventHour = splittedTime[2].substring(3, 8);

                    String eventDescription = eachEventDataJSON.getString("description");

                    MainEventsModel currentEvent = new MainEventsModel(eventTitle, coverPhoto, takingPlace, eventDay, eventMonth, eventHour, eventDescription, Double.parseDouble(evLat), Double.parseDouble(evLong));
                    eventsList.add(currentEvent);
                }
            }
            SocialEventsApplication.getInstance().updateEventsList(eventsList);
            eventsAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
            dialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.see_map)
    void onMapOptionClick(View view){
        Intent mapIntent = new Intent(this,MapsActivity.class);
        mapIntent.putParcelableArrayListExtra("all_events", (ArrayList<? extends Parcelable>) eventsList);
        mapIntent.putExtra("location",currentLocation);
        startActivity(mapIntent);
    }

    @OnClick(R.id.places_option)
    void onPlacesOptionClick(View view){
        Intent placesIntent = new Intent(this,ChatbotActivity.class);
        placesIntent.putExtra("location",currentLocation);
        placesIntent.putExtra("city_country",currentCity+", "+currentCountry);
        placesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(placesIntent);
        finish();
    }

    @OnClick(R.id.ag_option)
    void onAgOptionClick(View view){
        Intent agIntent = new Intent(this,AugmentedRealityActivity.class);
        agIntent.putParcelableArrayListExtra("all_events", (ArrayList<? extends Parcelable>) eventsList);
        startActivity(agIntent);
    }
}

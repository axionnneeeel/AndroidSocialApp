package com.example.razvan.socialeventshelper;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Adapters.MainEventsAdapter;
import com.example.razvan.socialeventshelper.Adapters.PlacesAdviserAdapter;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Models.PlacesAdviserModel;
import com.example.razvan.socialeventshelper.Utils.DownloadUrl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 4/7/2017.
 */

public class PlacesAdviserActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.places_option)
    LinearLayout placesOption;

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.places_adviser_recyclerView)
    RecyclerView placesView;

    private Location currentLocation;

    private PlacesAdviserAdapter placesAdapter;
    private List<PlacesAdviserModel> placesList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_adviser);
        ButterKnife.bind(this);

        currentLocation = getIntent().getParcelableExtra("location");

        String currentCityCountry = getIntent().getStringExtra("city_country");
        toolbarTitle.setText(currentCityCountry);

        placesOption.setBackgroundColor(ContextCompat.getColor(PlacesAdviserActivity.this,R.color.colorPrimaryTransp));
    }

    public String buildURL(Location location,Integer checkedOption) {

        double myLatitude = location.getLatitude();
        double myLongitude = location.getLongitude();

        View radioButton = radioGroup.findViewById(checkedOption);
        int index = radioGroup.indexOfChild(radioButton);

        String checkedOptionString;
        if(index == 0){
            checkedOptionString = "restaurant";
        }
        else if(index == 1){
            checkedOptionString = "bar";
        }
        else if(index == 2){
            checkedOptionString = "pharmacy";
        }
        else if(index == 3){
            checkedOptionString = "hospital";
        }
        else if(index == 4){
            checkedOptionString = "gas_station";
        }
        else{
            checkedOptionString = "shopping_mall";
        }

        Log.i("WTFDA",checkedOption+"");

        StringBuilder urlBuild = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        urlBuild.append("location=" + myLatitude + "," + myLongitude);
        urlBuild.append("&radius=5000");
        urlBuild.append("&types=" + checkedOptionString );
        urlBuild.append("&sensor=true");
        urlBuild.append("&key=");
        urlBuild.append("AIzaSyC_YDPo6qqafnaKEJtQsI_7M97SUl83ku4");

        Log.d("PlacesURL", "api: " + urlBuild.toString());

        return urlBuild.toString();
    }

    public class Place_JSON {

        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray myPlaces = null;
            try {
                myPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return getPlaces(myPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray myPlaces) {
            int placesCount = myPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<>();
            HashMap<String, String> place ;

            for (int i = 0; i < placesCount; i++) {
                try {
                    place = getPlace((JSONObject) myPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        private HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude ;
            String longitude ;
            String reference ;
            String rating = "";
            String open_now = "";

            try {
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                if (!jPlace.isNull("rating")) {
                    rating = jPlace.getString("rating");
                }

                if (!jPlace.isNull("opening_hours")) {
                    open_now = jPlace.getJSONObject("opening_hours").getString("open_now");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("rating", rating);
                place.put("open_now", open_now);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            Log.d("Map", "list size: " + list.size());

            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> hmPlace = list.get(i);

                double lat = Double.parseDouble(hmPlace.get("lat"));
                double lng = Double.parseDouble(hmPlace.get("lng"));

                String name = hmPlace.get("place_name");
                String vicinity = hmPlace.get("vicinity");
                String rating = hmPlace.get("rating");
                String openNow = hmPlace.get("open_now");

                Log.i("SIGUR",openNow +" " + rating);

                placesList.add(new PlacesAdviserModel(name));
            }

            placesAdapter = new PlacesAdviserAdapter(placesList, PlacesAdviserActivity.this);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            placesView.setLayoutManager(mLayoutManager);
            placesView.setItemAnimator(new DefaultItemAnimator());
            placesView.setAdapter(placesAdapter);
            placesView.setHasFixedSize(true);
        }
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = new DownloadUrl().downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    @OnClick(R.id.check_option)
    public void onCheckOptionClick(){
        Integer checkedOption = radioGroup.getCheckedRadioButtonId();
        String urlValue = buildURL(currentLocation,checkedOption);
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(urlValue);

    }
}

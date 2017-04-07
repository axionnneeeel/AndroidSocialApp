package com.example.razvan.socialeventshelper;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.razvan.socialeventshelper.Utils.DownloadUrl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Razvan on 4/7/2017.
 */

public class PlacesAdviserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Location currentLocation = getIntent().getParcelableExtra("location");

        String urlValue = buildURL(currentLocation);
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(urlValue);
    }

    public String buildURL(Location location) {

        double myLatitude = location.getLatitude();
        double myLongitude = location.getLongitude();

        StringBuilder urlBuild = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        urlBuild.append("location=" + myLatitude + "," + myLongitude);
        urlBuild.append("&radius=5000");
        urlBuild.append("&types=" + "restaurant" );
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

            try {
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
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
            // Clears all the existing markers;
            /*mGoogleMap.clear();

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);


                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d("Map", "place: " + name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                Marker m = mGoogleMap.addMarker(markerOptions);

            }*/
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
}

package com.example.razvan.socialeventshelper.AugmentedReality;

/**
 * Created by Razvan on 3/11/2017.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import com.example.razvan.socialeventshelper.Events.MainEventsModel;
import com.example.razvan.socialeventshelper.PlacesAdviser.PlacesAdviserModel;
import com.example.razvan.socialeventshelper.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AugmentedRealityActivity extends AppCompatActivity {

    private OverlayEventsView arContent;
    private OverlayPlacesView arContentPlaces;
    private Integer contentFlag;

    private CameraHolderView arDisplay;

    private AugmentedRealityExpandableListAdapter listAdapter;
    private ExpandableListView explandableList;

    private List<String> eventsHeader;
    private HashMap<String, List<String>> eventsTitles;

    private List<String> placesHeader;
    private HashMap<String, List<String>> placesTitles;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        findViewById(R.id.appBar).bringToFront();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        if(getIntent().hasExtra("all_events")) {
            contentFlag = 1;

            final ArrayList<MainEventsModel> eventsList = getIntent().getParcelableArrayListExtra("all_events");
            final ArrayList<MainEventsModel> deletedEventsList = new ArrayList<>();

            explandableList = (ExpandableListView) findViewById(R.id.expandable_list);

            eventsHeader = new ArrayList<>();
            eventsTitles = new HashMap<>();

            eventsHeader.add("Displayed events");

            List<String> eachEventTitle = new ArrayList<>();
            for (MainEventsModel eachEvent : eventsList)
                eachEventTitle.add(eachEvent.getEventTitle());
            eventsTitles.put(eventsHeader.get(0), eachEventTitle);

            listAdapter = new AugmentedRealityExpandableListAdapter(this, eventsHeader, eventsTitles);
            explandableList.setAdapter(listAdapter);

            final FrameLayout phoneScreen = (FrameLayout) findViewById(R.id.entire_screen);

            arDisplay = new CameraHolderView(getApplicationContext(), this);
            phoneScreen.addView(arDisplay);

            arContent = new OverlayEventsView(getApplicationContext(), eventsList);
            phoneScreen.addView(arContent);

            explandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    MainEventsModel eventToBeDeleted = null;
                    MainEventsModel eventToBeAdded = null;

                    CheckBox itemCheckBox = (CheckBox) parent.getChildAt(childPosition + 1).findViewById(R.id.item_radio_button);

                    if (itemCheckBox.isChecked()) {
                        for (MainEventsModel eachEvent : eventsList) {
                            if (eachEvent.getEventTitle().equals(eventsTitles.get(eventsHeader.get(groupPosition)).get(childPosition))) {
                                eventToBeDeleted = eachEvent;
                                deletedEventsList.add(eventToBeDeleted);
                            }
                        }
                        itemCheckBox.setChecked(false);
                        eventsList.remove(eventToBeDeleted);
                        arContent.modifyEvents(eventsList);
                    } else {
                        for (MainEventsModel eachDeletedEvent : deletedEventsList) {
                            if (eachDeletedEvent.getEventTitle().equals(eventsTitles.get(eventsHeader.get(groupPosition)).get(childPosition))) {
                                eventToBeAdded = eachDeletedEvent;
                            }
                        }
                        itemCheckBox.setChecked(true);
                        deletedEventsList.remove(eventToBeAdded);
                        eventsList.add(eventToBeAdded);
                        arContent.modifyEvents(eventsList);
                    }

                    return false;
                }
            });
        } else if (getIntent().hasExtra("all_places")) {

            contentFlag = 2;
            final ArrayList<PlacesAdviserModel> placesList = getIntent().getParcelableArrayListExtra("all_places");
            final ArrayList<PlacesAdviserModel> deletedPlacesList = new ArrayList<>();

            explandableList = (ExpandableListView) findViewById(R.id.expandable_list);

            placesHeader = new ArrayList<>();
            placesTitles = new HashMap<>();

            placesHeader.add("Displayed places");

            List<String> eachPlaceTitle = new ArrayList<>();
            for (PlacesAdviserModel eachPlace : placesList)
                eachPlaceTitle.add(eachPlace.getPlaceName());
            placesTitles.put(placesHeader.get(0), eachPlaceTitle);

            listAdapter = new AugmentedRealityExpandableListAdapter(this, placesHeader, placesTitles);
            explandableList.setAdapter(listAdapter);

            final FrameLayout phoneScreen = (FrameLayout) findViewById(R.id.entire_screen);

            arDisplay = new CameraHolderView(getApplicationContext(), this);
            phoneScreen.addView(arDisplay);

            arContentPlaces = new OverlayPlacesView(getApplicationContext(), placesList);
            phoneScreen.addView(arContentPlaces);

            explandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    PlacesAdviserModel placeToBeDeleted = null;
                    PlacesAdviserModel placeToBeAdded = null;

                    CheckBox itemCheckBox = (CheckBox) parent.getChildAt(childPosition + 1).findViewById(R.id.item_radio_button);

                    if (itemCheckBox.isChecked()) {
                        for (PlacesAdviserModel eachPlace : placesList) {
                            if (eachPlace.getPlaceName().equals(placesTitles.get(placesHeader.get(groupPosition)).get(childPosition))) {
                                placeToBeDeleted = eachPlace;
                                deletedPlacesList.add(placeToBeDeleted);
                            }
                        }
                        itemCheckBox.setChecked(false);
                        placesList.remove(placeToBeDeleted);
                        arContentPlaces.modifyEvents(placesList);
                    } else {
                        for (PlacesAdviserModel eachDeletedPlace : deletedPlacesList) {
                            if (eachDeletedPlace.getPlaceName().equals(placesTitles.get(placesHeader.get(groupPosition)).get(childPosition))) {
                                placeToBeAdded = eachDeletedPlace;
                            }
                        }
                        itemCheckBox.setChecked(true);
                        deletedPlacesList.remove(placeToBeAdded);
                        placesList.add(placeToBeAdded);
                        arContentPlaces.modifyEvents(placesList);
                    }

                    return false;
                }
            });
        }
    }

    @Override
    protected void onPause() {
        if(contentFlag == 2)
            arContentPlaces.onPause();
        else arContent.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(contentFlag == 2)
            arContentPlaces.onResume();
        else arContent.onResume();
    }


}

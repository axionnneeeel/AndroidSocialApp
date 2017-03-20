package com.example.razvan.socialeventshelper.AugmentedReality;

/**
 * Created by Razvan on 3/11/2017.
 */

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import com.example.razvan.socialeventshelper.Adapters.AugmentedRealityExpandableListAdapter;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.Toast;


public class AugmentedRealityActivity extends AppCompatActivity {

    private OverlayEventsView arContent;
    private CameraHolderView arDisplay;

    private AugmentedRealityExpandableListAdapter listAdapter;
    private ExpandableListView expandableEvents;

    private List<String> eventsHeader;
    private HashMap<String, List<String>> eventsTitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        findViewById(R.id.appBar).bringToFront();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        final ArrayList<MainEventsModel> eventsList = getIntent().getParcelableArrayListExtra("all_events");
        final ArrayList<MainEventsModel> deletedEventsList = new ArrayList<>();

        expandableEvents = (ExpandableListView) findViewById(R.id.expandable_list);

        eventsHeader = new ArrayList<>();
        eventsTitles = new HashMap<>();

        eventsHeader.add("Displayed events");

        List<String> eachEventTitle = new ArrayList<>();
        for (MainEventsModel eachEvent : eventsList)
            eachEventTitle.add(eachEvent.getEventTitle());
        eventsTitles.put(eventsHeader.get(0), eachEventTitle);

        listAdapter = new AugmentedRealityExpandableListAdapter(this, eventsHeader, eventsTitles);
        expandableEvents.setAdapter(listAdapter);

        final FrameLayout phoneScreen = (FrameLayout) findViewById(R.id.entire_screen);

        arDisplay = new CameraHolderView(getApplicationContext(), this);
        phoneScreen.addView(arDisplay);

        arContent = new OverlayEventsView(getApplicationContext(), eventsList);
        phoneScreen.addView(arContent);

        expandableEvents.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                MainEventsModel eventToBeDeleted = null;
                MainEventsModel eventToBeAdded = null;

                CheckBox itemCheckBox = (CheckBox) parent.getChildAt(childPosition + 1).findViewById(R.id.item_radio_button);

                if(itemCheckBox.isChecked()) {
                    for (MainEventsModel eachEvent : eventsList) {
                        if (eachEvent.getEventTitle().equals(eventsTitles.get(eventsHeader.get(groupPosition)).get(childPosition))) {
                            eventToBeDeleted = eachEvent;
                            deletedEventsList.add(eventToBeDeleted);
                        }
                    }
                    itemCheckBox.setChecked(false);
                    eventsList.remove(eventToBeDeleted);
                    arContent.modifyEvents(eventsList);
                }
                else{
                    for(MainEventsModel eachDeletedEvent : deletedEventsList){
                        if (eachDeletedEvent.getEventTitle().equals(eventsTitles.get(eventsHeader.get(groupPosition)).get(childPosition))){
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
    }

    @Override
    protected void onPause() {
        arContent.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arContent.onResume();
    }

}

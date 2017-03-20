package com.example.razvan.socialeventshelper.AugmentedReality;


import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import com.example.razvan.socialeventshelper.Adapters.ExpandableListAdapter;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Razvan on 3/11/2017.
 */

public class AugmentedRealityActivity extends AppCompatActivity {

    private OverlayEventsView arContent;
    private CameraHolderView arDisplay;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        findViewById(R.id.appBar).bringToFront();

        ArrayList<MainEventsModel> eventsList = getIntent().getParcelableArrayListExtra("all_events");

        expListView = (ExpandableListView) findViewById(R.id.expandable_list);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Displayed events");

        List<String> top250 = new ArrayList<String>();

        for(MainEventsModel eachEvent : eventsList)
            top250.add(eachEvent.getEventTitle());

        listDataChild.put(listDataHeader.get(0), top250);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        FrameLayout phoneScreen = (FrameLayout) findViewById(R.id.entire_screen);

        arDisplay = new CameraHolderView(getApplicationContext(),this);
        phoneScreen.addView(arDisplay);

        arContent = new OverlayEventsView(getApplicationContext(),eventsList);
        phoneScreen.addView(arContent);
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

package com.example.razvan.socialeventshelper.AugmentedReality;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.R;

import java.util.ArrayList;

/**
 * Created by Razvan on 3/11/2017.
 */

public class AugmentedRealityActivity extends AppCompatActivity {

    private OverlayEventsView arContent;
    private CameraHolderView arDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality);

        ArrayList<MainEventsModel> eventsList = getIntent().getParcelableArrayListExtra("all_events");

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

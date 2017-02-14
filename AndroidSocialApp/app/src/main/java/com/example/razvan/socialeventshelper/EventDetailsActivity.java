package com.example.razvan.socialeventshelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Utils.ImageLoadTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Razvan on 2/14/2017.
 */

public class EventDetailsActivity extends AppCompatActivity {
    @BindView(R.id.event_name)
    TextView eventName;

    @BindView(R.id.event_streetName)
    TextView takingPlace;

    @BindView(R.id.event_date_day)
    TextView eventDay;

    @BindView(R.id.event_date_month)
    TextView eventMonth;

    @BindView(R.id.event_hour)
    TextView eventHour;

    @BindView(R.id.cover_photo)
    ImageView coverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ButterKnife.bind(this);

        MainEventsModel myEvent = (MainEventsModel) getIntent().getSerializableExtra("myEvent");
        bindEventInfo(myEvent);
    }

    private void bindEventInfo(MainEventsModel event){
        eventName.setText(event.getTitle());
        takingPlace.setText(event.getTakingPlace());
        eventDay.setText(event.getEventDay());
        eventMonth.setText(event.getEventMonth());
        eventHour.setText("Start time: "+event.getEventHour());
        new ImageLoadTask(event.getCoverPhoto(), coverPhoto).execute();
    }
}

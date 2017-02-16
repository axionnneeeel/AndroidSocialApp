package com.example.razvan.socialeventshelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Utils.ImageLoadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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

    @BindView(R.id.toolbar_title)
    TextView title;

    @BindView(R.id.event_location_name)
    TextView eventLocation;

    @BindView(R.id.event_details)
    TextView eventDetails;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ButterKnife.bind(this);

        final MainEventsModel myEvent = (MainEventsModel) getIntent().getSerializableExtra("myEvent");
        bindEventInfo(myEvent);
        setCollapsingToolbarBehaviour(myEvent.getTitle());

    }

    private void bindEventInfo(MainEventsModel event){
        eventName.setText(event.getTitle());
        takingPlace.setText(event.getTakingPlace());
        eventDay.setText(event.getEventDay());
        eventMonth.setText(event.getEventMonth());
        eventHour.setText(event.getEventDay()+" "+event.getEventMonth().toLowerCase()+" at "+event.getEventHour());
        eventLocation.setText(event.getTakingPlace());
        eventDetails.setText(event.getEventDescription());
        Picasso.with(this).load(event.getCoverPhoto()).fit().into(coverPhoto);
    }

    private void setCollapsingToolbarBehaviour(final String eventTitle){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    title.setText(eventTitle);
                    isShow = true;
                } else if(isShow) {
                    title.setText(" ");
                    isShow = false;
                }
            }
        });
    }
}

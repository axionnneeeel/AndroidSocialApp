package com.example.razvan.socialeventshelper;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Razvan on 2/14/2017.
 */

public class EventDetailsActivity extends AppCompatActivity {
    @BindView(R.id.event_name)
    TextView eventTitle;

    @BindView(R.id.event_street)
    TextView eventTakingPlace;

    @BindView(R.id.event_day)
    TextView eventDay;

    @BindView(R.id.event_month)
    TextView eventMonth;

    @BindView(R.id.event_hour)
    TextView eventHour;

    @BindView(R.id.event_cover_photo)
    ImageView eventCoverPhoto;

    @BindView(R.id.event_location_name)
    TextView eventLocation;

    @BindView(R.id.event_details)
    TextView eventDetails;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ButterKnife.bind(this);

        final MainEventsModel myEvent = (MainEventsModel) getIntent().getSerializableExtra("myEvent");
        bindEventInfo(myEvent);
        setCollapsingToolbarBehaviour(myEvent.getEventTitle());

    }

    private void bindEventInfo(MainEventsModel event){
        eventTitle.setText(event.getEventTitle());
        eventTakingPlace.setText(event.getEventTakingPlace());
        eventDay.setText(event.getEventDay());
        eventMonth.setText(event.getEventMonth());
        eventHour.setText(event.getEventDay()+" "+event.getEventMonth().toLowerCase()+" at "+event.getEventHour());
        eventLocation.setText(event.getEventTakingPlace());
        eventDetails.setText(event.getEventDescription());
        Picasso.with(this).load(event.getEventCoverPhoto()).fit().into(eventCoverPhoto);
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
                    toolbarTitle.setText(eventTitle);
                    isShow = true;
                } else if(isShow) {
                    toolbarTitle.setText(" ");
                    isShow = false;
                }
            }
        });
    }
}

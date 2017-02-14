package com.example.razvan.socialeventshelper;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*Intent eventsIntent = new Intent(this,MainEventsActivity.class);
        startActivity(eventsIntent);*/

        Intent eventsIntent = new Intent(this,EventDetailsActivity.class);
        startActivity(eventsIntent);
    }

    @OnClick(R.id.fab)
    void onFabClick(View view){
        Snackbar.make(view, "Da", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}

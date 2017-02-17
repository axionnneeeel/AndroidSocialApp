package com.example.razvan.socialeventshelper;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent eventsIntent = new Intent(this,MainEventsActivity.class);
        eventsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(eventsIntent);
        finish();
    }
}

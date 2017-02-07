package com.example.razvan.socialeventshelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.razvan.socialeventshelper.Adapters.MainEventsAdapter;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsActivity extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.swipe_refresh_recyclerView)
    RecyclerView eventsView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private MainEventsAdapter mAdapter;
    private List<MainEventsModel> movieList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);
        ButterKnife.bind(this);

        mAdapter = new MainEventsAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        eventsView.setLayoutManager(mLayoutManager);
        eventsView.setItemAnimator(new DefaultItemAnimator());
        eventsView.setAdapter(mAdapter);

        eventsView.setHasFixedSize(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        fetchEvents();
    }

    private void fetchEvents(){
        long currentTimeMilliSeconds = System.currentTimeMillis();
        long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeMilliSeconds);

        long timeAfter10Days = currentTimeSeconds + 864000;

        GraphRequestAsyncTask task = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/search?q=Iasi&type=event&since="+currentTimeSeconds+"&until="+timeAfter10Days+"&access_token="
                        + this.getString(R.string.explorer_token),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        transformToJSONAndFilter(response);
                    }
                }
        ).executeAsync();
        Log.i("taskul",task.toString());
    }

    private void transformToJSONAndFilter(GraphResponse response){
        try {
            JSONObject resultJSONObj = response.getJSONObject();
            JSONArray resultJSON = resultJSONObj.getJSONArray("data");

            for (int eachJSON = 1; eachJSON < resultJSON.length(); eachJSON++) {
                JSONObject currentObject = resultJSON.getJSONObject(eachJSON);
                Log.i("ceplm",currentObject.getString("name"));
                MainEventsModel currentEvent = new MainEventsModel(currentObject.getString("name"));
                movieList.add(currentEvent);

            }
            mAdapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab)
    void onFabClick(View view){
        Snackbar.make(view, "Da", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}

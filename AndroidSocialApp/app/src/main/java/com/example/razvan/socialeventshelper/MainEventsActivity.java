package com.example.razvan.socialeventshelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

/**
 * Created by Razvan on 2/7/2017.
 */

public class MainEventsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/search?q=Dublin&type=event&access_token=" + this.getString(R.string.explorer_token),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i("event", response.toString());
                        Toast.makeText(MainEventsActivity.this, "" + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ).executeAsync();
    }
}

package com.example.razvan.socialeventshelper.Friends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.example.razvan.socialeventshelper.AccountActivity;
import com.example.razvan.socialeventshelper.ChatActivity;
import com.example.razvan.socialeventshelper.Chatbot.ChatbotActivity;
import com.example.razvan.socialeventshelper.Events.MainEventsActivity;
import com.example.razvan.socialeventshelper.PlacesAdviser.PlacesAdviserActivity;
import com.example.razvan.socialeventshelper.R;
import com.example.razvan.socialeventshelper.ServerCommunication;
import com.example.razvan.socialeventshelper.SocialEventsApplication;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 5/3/2017.
 */

public class FriendsActivity extends AppCompatActivity {

    @BindView(R.id.friens_option)
    LinearLayout friendsOption;

    @BindView(R.id.swipe_refresh_recyclerView)
    RecyclerView friendsView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private FriendsAdapter friendsAdapter;
    private List<FriendsModel> friendsList = new ArrayList<>();

    private Socket serverSocket;
    private ServerCommunication server = null;

    private Location currentLocation;
    private String currentCityCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);

        currentLocation = getIntent().getParcelableExtra("location");
        currentCityCountry = getIntent().getStringExtra("city_country");

        friendsOption.setBackgroundColor(ContextCompat.getColor(FriendsActivity.this,R.color.colorPrimaryTransp));

        serverSocket = SocialEventsApplication.getInstance().getServerSocket();

        server = SocialEventsApplication.getInstance().getServer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.sendFriendsFlag();
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        server.setWaitForThreadFinish(false);

        friendsList = server.getFriendsList();

        friendsAdapter = new FriendsAdapter(friendsList, this, new FriendsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final FriendsModel item) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server.sendUserToBeDeletedFlag(item.getFriendId());
                    }
                }).start();

                while(!server.isWaitForThreadFinish()){

                }

                server.setWaitForThreadFinish(false);

                AlertDialog.Builder alertDialog  = new AlertDialog.Builder(FriendsActivity.this);

                alertDialog.setMessage("Friend successfully deleted! Please refresh the page!");
                alertDialog.setTitle("Delete done!");
                alertDialog.setPositiveButton("OK", null);
                alertDialog.setCancelable(true);
                alertDialog.create().show();
            }

            @Override
            public void onItemClickChat(FriendsModel item) {
                Intent accountIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                accountIntent.putExtra("friendId",item.getFriendId());
                accountIntent.putExtra("friendName",item.getFriendName());
                startActivity(accountIntent);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        friendsView.setLayoutManager(mLayoutManager);
        friendsView.setItemAnimator(new DefaultItemAnimator());
        friendsView.setAdapter(friendsAdapter);

        friendsView.setHasFixedSize(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                friendsList.clear();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server.sendFriendsFlag();
                    }
                }).start();

                while(!server.isWaitForThreadFinish()){

                }

                server.setWaitForThreadFinish(false);

                friendsList = server.getFriendsList();
                friendsAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @OnClick(R.id.add_friend)
    public void onAddFriendClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter friend username.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for(FriendsModel eachFriend : friendsList)
                    if(eachFriend.getFriendName().equals(input.getText().toString()) ){
                        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(FriendsActivity.this);

                        alertDialog.setMessage("User already exists in your friend list!");
                        alertDialog.setTitle("Error!");
                        alertDialog.setPositiveButton("OK", null);
                        alertDialog.setCancelable(true);
                        alertDialog.create().show();
                        return;
                    }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server.sendUserToBeAddedFlag(input.getText().toString());
                    }
                }).start();

                while(!server.isWaitForThreadFinish()){

                }

                server.setWaitForThreadFinish(false);

                if(server.getUserExists() == -1){
                    AlertDialog.Builder alertDialog  = new AlertDialog.Builder(FriendsActivity.this);

                    alertDialog.setMessage("User does not exist in our database!");
                    alertDialog.setTitle("Error!");
                    alertDialog.setPositiveButton("OK", null);
                    alertDialog.setCancelable(true);
                    alertDialog.create().show();
                }
                else{
                    AlertDialog.Builder alertDialog  = new AlertDialog.Builder(FriendsActivity.this);

                    alertDialog.setMessage("Friend added! Please refresh the page!");
                    alertDialog.setTitle("Friend added!");
                    alertDialog.setPositiveButton("OK", null);
                    alertDialog.setCancelable(true);
                    alertDialog.create().show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @OnClick(R.id.events_option)
    void onEventsOptionClick(View view){
        Intent eventsIntent = new Intent(this,MainEventsActivity.class);
        eventsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(eventsIntent);
        finish();
    }

    @OnClick(R.id.places_option)
    void onPlacesOptionClick(View view){
        Intent placesIntent = new Intent(this,PlacesAdviserActivity.class);
        placesIntent.putExtra("location",currentLocation);
        placesIntent.putExtra("city_country",currentCityCountry);
        placesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(placesIntent);
        finish();
    }

    @OnClick(R.id.chatbot_option)
    void onChatBotOptionClick(View view){
        Intent chatBotIntent = new Intent(this,ChatbotActivity.class);
        chatBotIntent.putExtra("location",currentLocation);
        chatBotIntent.putExtra("city_country",currentCityCountry);
        chatBotIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(chatBotIntent);
        finish();
    }

    @OnClick(R.id.account_option)
    void onAccountOptionClick(View view){
        Intent accountIntent = new Intent(this,AccountActivity.class);
        accountIntent.putExtra("location",currentLocation);
        accountIntent.putExtra("city_country",currentCityCountry);
        accountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accountIntent);
        finish();
    }
}

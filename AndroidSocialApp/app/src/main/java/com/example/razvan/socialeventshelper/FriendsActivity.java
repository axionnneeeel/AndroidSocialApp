package com.example.razvan.socialeventshelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.razvan.socialeventshelper.Adapters.FriendsAdapter;
import com.example.razvan.socialeventshelper.Adapters.MainEventsAdapter;
import com.example.razvan.socialeventshelper.Adapters.PlacesAdviserAdapter;
import com.example.razvan.socialeventshelper.Chatbot.ChatbotActivity;
import com.example.razvan.socialeventshelper.Models.FriendsModel;
import com.example.razvan.socialeventshelper.Models.MainEventsModel;
import com.example.razvan.socialeventshelper.Utils.GeneralUtils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);

        friendsOption.setBackgroundColor(ContextCompat.getColor(FriendsActivity.this,R.color.colorPrimaryTransp));

        serverSocket = SocialEventsApplication.getInstance().getServerSocket();

        server = new ServerCommunication(serverSocket);

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.getUserFriends();
                server.setWaitForThreadFinish(true);
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
                        server.sendUserToBeDeleted(item.getFriendId());
                        server.setWaitForThreadFinish(true);
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
                        server.getUserFriends();
                        server.setWaitForThreadFinish(true);
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
                        server.sendUserToBeAdded(input.getText().toString());
                        server.setWaitForThreadFinish(true);
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
}

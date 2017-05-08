package com.example.razvan.socialeventshelper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.razvan.socialeventshelper.Chatbot.ChatMessage;
import com.example.razvan.socialeventshelper.Chatbot.ChatMessageAdapter;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Razvan on 5/8/2017.
 */

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView friendNameToolbar;

    private String friendName;
    private Integer friendId;

    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ChatMessageAdapter mAdapter;

    private Socket serverSocket;
    private ServerCommunication server = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        friendName = getIntent().getStringExtra("friendName");
        friendId = getIntent().getIntExtra("friendId",0);

        friendNameToolbar.setText(friendName);

        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        serverSocket = SocialEventsApplication.getInstance().getServerSocket();

        server = new ServerCommunication(serverSocket);

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.getConversation(friendName);
                server.setWaitForThreadFinish(true);
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        server.setWaitForThreadFinish(false);

        Integer numberMessages = server.getMessagesNumber();
        List<String> messagesList = server.getMessagesList();
        if(numberMessages != -1){
            for(String eachMessage : messagesList){
                if(eachMessage.split(" ",2)[0].equals(friendName)) {
                    mimicOtherMessage(eachMessage.split(" ",2)[1]);
                }
                else sendMessage(eachMessage.split(" ",2)[1]);
            }
        }



        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = mEditTextMessage.getText().toString();

                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server.sendMessage(friendName,message);
                        server.setWaitForThreadFinish(true);
                    }
                }).start();

                while(!server.isWaitForThreadFinish()){

                }

                server.setWaitForThreadFinish(false);
            }
        });

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }
}

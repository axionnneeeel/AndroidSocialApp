package com.example.razvan.socialeventshelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.razvan.socialeventshelper.Chatbot.ChatMessage;
import com.example.razvan.socialeventshelper.Chatbot.ChatMessageAdapter;
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

        server = SocialEventsApplication.getInstance().getServer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.getConversationFlag(friendName);
            }
        }).start();

        while (!server.isWaitForThreadFinish()) {

        }

        server.setWaitForThreadFinish(false);

        final Integer numberMessages = server.getMessagesNumber();
        final List<String> messagesList = server.getMessagesList();
        if (numberMessages != -1) {
            for (String eachMessage : messagesList) {
                if (eachMessage.split(" ", 2)[0].equals(friendName)) {
                    mimicOtherMessage(eachMessage.split(" ", 2)[1]);
                } else sendMessage(eachMessage.split(" ", 2)[1]);
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
                        server.sendMessageFlag(friendName,message);
                    }
                }).start();

                while(!server.isWaitForThreadFinish()){

                }

                server.setWaitForThreadFinish(false);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    while (server.getNewMessageUser().isEmpty() || !server.isWaitForThreadFinish()) {

                    }
                    server.setWaitForThreadFinish(false);
                    if (server.getNewMessageUser().equals(friendName)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mimicOtherMessage(server.getMessage());
                                server.setNewMessageUser("");
                                server.setMessage("");
                            }
                        });
                    }
                    else{
                        server.setMessage("");
                        server.setNewMessageUser("");
                    }
                }

            }
        }).start();

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    @Override
    public void onStart() {
        super.onStart();
        SocialEventsApplication.getInstance().setIsChatOpen(1);
    }

    @Override
    public void onStop() {
        super.onStop();
        SocialEventsApplication.getInstance().setIsChatOpen(0);
    }
}

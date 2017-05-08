package com.example.razvan.socialeventshelper.Chatbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.razvan.socialeventshelper.AccountActivity;
import com.example.razvan.socialeventshelper.Events.MainEventsActivity;
import com.example.razvan.socialeventshelper.Friends.FriendsActivity;
import com.example.razvan.socialeventshelper.PlacesAdviser.PlacesAdviserActivity;
import com.example.razvan.socialeventshelper.R;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 4/25/2017.
 */

public class ChatbotActivity extends AppCompatActivity {

    @BindView(R.id.menu)
    CardView menu;

    @BindView(R.id.send_message_layout)
    LinearLayout sendMessage;

    @BindView(R.id.chatbot_option)
    LinearLayout chatBotOption;

    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;
    private ProgressDialog dialog;

    private Location currentLocation;
    private String currentCityCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        ButterKnife.bind(this);

        currentLocation = getIntent().getParcelableExtra("location");
        currentCityCountry = getIntent().getStringExtra("city_country");

        chatBotOption.setBackgroundColor(ContextCompat.getColor(ChatbotActivity.this,R.color.colorPrimaryTransp));

        dialog=new ProgressDialog(this);
        dialog.setMessage("Loading bot brain..");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();

                String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mimicOtherMessage(response);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });

        new Thread(){
            public void run(){
                //checking SD card availablility
                boolean a = isSDCARDAvailable();
                //receiving the assets from the app directory
                AssetManager assets = getResources().getAssets();
                File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");
                boolean b = jayDir.mkdirs();
                if (jayDir.exists()) {
                    //Reading the file
                    try {
                        for (String dir : assets.list("Hari")) {
                            File subdir = new File(jayDir.getPath() + "/" + dir);
                            boolean subdir_check = subdir.mkdirs();
                            for (String file : assets.list("Hari/" + dir)) {
                                File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                                if (f.exists()) {
                                    continue;
                                }
                                InputStream in = null;
                                OutputStream out = null;
                                in = assets.open("Hari/" + dir + "/" + file);
                                out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                                //copy file from assets to the mobile's SD card or any secondary memory
                                copyFile(in, out);
                                in.close();
                                in = null;
                                out.flush();
                                out.close();
                                out = null;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //get the working directory
                MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
                System.out.println("Working Directory = " + MagicStrings.root_path);
                AIMLProcessor.extension =  new PCAIMLProcessorExtension();
                //Assign the AIML files to bot for processing
                bot = new Bot("Hari", MagicStrings.root_path, "chat");
                chat = new Chat(bot);
                String[] args = null;
                mainFunction(args);
                dialog.dismiss();
            }
        }.start();
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }
    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    //Request and response of user and the bot
    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }

    @OnClick(R.id.show_menu)
    public void onShowMenuClick(View view){
        if(menu.getVisibility() == View.GONE) {
            menu.setVisibility(View.VISIBLE);
            sendMessage.setVisibility(View.GONE);
        }
        else{
            menu.setVisibility(View.GONE);
            sendMessage.setVisibility(View.VISIBLE);
        }
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

    @OnClick(R.id.account_option)
    void onAccountOptionClick(View view){
        Intent accountIntent = new Intent(this,AccountActivity.class);
        accountIntent.putExtra("location",currentLocation);
        accountIntent.putExtra("city_country",currentCityCountry);
        accountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accountIntent);
        finish();
    }

    @OnClick(R.id.friens_option)
    void onFriendsOptionClick(View view){
        Intent friendsIntent = new Intent(this,FriendsActivity.class);
        friendsIntent.putExtra("location",currentLocation);
        friendsIntent.putExtra("city_country",currentCityCountry);
        friendsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(friendsIntent);
        finish();
    }

}
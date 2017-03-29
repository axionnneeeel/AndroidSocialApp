package com.example.razvan.socialeventshelper;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Socket serverSocket = SocialEventsApplication.getInstance().getServerSocket();
        try {
            DataOutputStream dOut = new DataOutputStream(serverSocket.getOutputStream());
            DataInputStream dIn = new DataInputStream(serverSocket.getInputStream());

            dOut.writeUTF("SALUT");
            dOut.writeUTF("MAAA");
            dOut.writeUTF("QUIT");
            dOut.flush();
            dOut.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        Intent eventsIntent = new Intent(this,LoginActivity.class);
        eventsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(eventsIntent);
        finish();
    }
}

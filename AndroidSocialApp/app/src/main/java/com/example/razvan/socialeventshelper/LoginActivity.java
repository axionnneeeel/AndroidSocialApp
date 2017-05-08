package com.example.razvan.socialeventshelper;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.razvan.socialeventshelper.Events.MainEventsActivity;

import java.net.Socket;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 3/29/2017.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    private Socket serverSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            AlertDialog.Builder alertDialog  = new AlertDialog.Builder(LoginActivity.this);

            alertDialog.setMessage("Register successfully! Please login with your username and password.");
            alertDialog.setTitle("Register done!");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.setCancelable(true);
            alertDialog.create().show();
        }


        serverSocket = SocialEventsApplication.getInstance().getServerSocket();
    }

    @OnClick(R.id.login)
    public void onLoginButtonClick(View view){
        String usernameString = this.username.getText().toString();
        String passwordString = this.password.getText().toString();

        Integer loginValue = sendCreditentialsAndReceiveConfirmation(usernameString,passwordString);

        if(loginValue == 1){
            Intent eventsIntent = new Intent(LoginActivity.this, MainEventsActivity.class);
            eventsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(eventsIntent);
            finish();
        }
        else{
            AlertDialog.Builder alertDialog  = new AlertDialog.Builder(LoginActivity.this);

            alertDialog.setMessage("Wrong username and/or password. Please try again.");
            alertDialog.setTitle("Error...");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.setCancelable(true);
            alertDialog.create().show();
        }
    }

    @OnClick(R.id.sing_up)
    public void onRegisterButtonClick(View view){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private Integer sendCreditentialsAndReceiveConfirmation(final String user, final String pass) {
        final ServerCommunication server = new ServerCommunication(serverSocket);

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.checkIfLogged(user,pass);
                server.setWaitForThreadFinish(true);
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        return server.getCheckLogin();
    }

}

package com.example.razvan.socialeventshelper;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 4/3/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.mail)
    EditText email;

    private Socket serverSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        serverSocket = SocialEventsApplication.getInstance().getServerSocket();
    }

    @OnClick(R.id.register)
    public void onClickRegister(View view){
        String usernameString = this.username.getText().toString();
        String passwordString = this.password.getText().toString();
        String emailString = this.email.getText().toString();

        Integer registerValue = sendCreditentialsAndReceiveConfirmation(usernameString,passwordString,emailString);

        if(registerValue == 1){

            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            loginIntent.putExtra("register",1);
            startActivity(loginIntent);
            finish();
        }
        else{
            AlertDialog.Builder alertDialog  = new AlertDialog.Builder(RegisterActivity.this);

            alertDialog.setMessage("Invalid username/password or already exists. Please try again with other credentials.");
            alertDialog.setTitle("Error...");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.setCancelable(true);
            alertDialog.create().show();
        }
    }

    private Integer sendCreditentialsAndReceiveConfirmation(final String user, final String pass,final String email) {
        final ServerCommunication server = new ServerCommunication(serverSocket);

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.registerOrCheckRegisterValidity(user,pass,email);
                server.setWaitForThreadFinish(true);
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        return server.getRegisterFlag();
    }
}

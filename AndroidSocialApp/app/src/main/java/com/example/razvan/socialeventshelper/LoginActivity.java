package com.example.razvan.socialeventshelper;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.razvan.socialeventshelper.Events.MainEventsActivity;
import com.example.razvan.socialeventshelper.Friends.FriendsActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.Arrays;

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

    @BindView(R.id.facebook_login)
    LoginButton facebookLogin;

    private CallbackManager callbackManager;

    private Socket serverSocket;
    private ServerCommunication server = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        server = SocialEventsApplication.getInstance().getServer();

        SharedPreferences prefs = getSharedPreferences("credentials", MODE_PRIVATE);
        String usernamePref = prefs.getString("username", null);
        String passwordPref = prefs.getString("password", null);
        if (usernamePref != null && passwordPref != null) {
            Integer loginValue = sendCreditentialsAndReceiveConfirmation(usernamePref,passwordPref);

            if(loginValue == 1){
                Intent eventsIntent = new Intent(LoginActivity.this, MainEventsActivity.class);
                eventsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(eventsIntent);
                finish();
            }
        }

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

        facebookLogin.clearPermissions();
        facebookLogin.setReadPermissions(Arrays.asList("email"));
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequestAsyncTask task = new GraphRequest(
                        loginResult.getAccessToken(),
                        "/me?fields=first_name,last_name,email,cover" + "&access_token="
                                + getString(R.string.explorer_token),
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                JSONObject resultJSONObj = response.getJSONObject();

                                try {
                                    final String first_name = resultJSONObj.getString("first_name");
                                    final String last_name = resultJSONObj.getString("last_name");
                                    final String email = resultJSONObj.getString("email");

                                    JSONObject resultJSON = resultJSONObj.getJSONObject("cover");
                                    final String picture = resultJSON.getString("source");

                                    sendCreditentialsAndReceiveConfirmationRegister(email, loginResult.getAccessToken().getUserId(), "");
                                    sendCreditentialsAndReceiveConfirmation(email, loginResult.getAccessToken().getUserId());
                                    saveUsernameAndPassword(email, loginResult.getAccessToken().getUserId());

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            server.sendUserDetails(first_name,last_name,email,picture);
                                        }
                                    }).start();

                                    while (!server.isWaitForThreadFinish()) {

                                    }

                                    server.setWaitForThreadFinish(false);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();

                Intent eventsIntent = new Intent(LoginActivity.this, MainEventsActivity.class);
                eventsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(eventsIntent);
                finish();

            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @OnClick(R.id.login)
    public void onLoginButtonClick(View view){
        String usernameString = this.username.getText().toString();
        String passwordString = this.password.getText().toString();

        Integer loginValue = sendCreditentialsAndReceiveConfirmation(usernameString,passwordString);

        if(loginValue == 1){
            saveUsernameAndPassword(usernameString,passwordString);
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
        final ServerCommunication server = SocialEventsApplication.getInstance().getServer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.sendLoginFlag(user,pass);
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        server.setWaitForThreadFinish(false);

        return server.getCheckLogin();
    }

    private Integer sendCreditentialsAndReceiveConfirmationRegister(final String user, final String pass,final String email) {
        final ServerCommunication server = SocialEventsApplication.getInstance().getServer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.registerOrCheckRegisterValidityFlag(user,pass,email);
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        server.setWaitForThreadFinish(false);

        return server.getRegisterFlag();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void saveUsernameAndPassword(String username,String password){
        SharedPreferences.Editor editor = getSharedPreferences("credentials", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

}

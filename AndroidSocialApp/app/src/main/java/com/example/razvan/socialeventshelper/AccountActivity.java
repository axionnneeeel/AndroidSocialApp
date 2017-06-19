package com.example.razvan.socialeventshelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.razvan.socialeventshelper.Chatbot.ChatbotActivity;
import com.example.razvan.socialeventshelper.Events.MainEventsActivity;
import com.example.razvan.socialeventshelper.Friends.FriendsActivity;
import com.example.razvan.socialeventshelper.PlacesAdviser.PlacesAdviserActivity;
import com.example.razvan.socialeventshelper.Utils.RoundedTransformation;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Razvan on 4/27/2017.
 */

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.account_option)
    LinearLayout accountOption;

    @BindView(R.id.firstname)
    EditText firstName;

    @BindView(R.id.lastname)
    EditText lastName;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.username)
    TextView username;

    @BindView(R.id.profile_image)
    ImageView profileImage;

    @BindView(R.id.logout_option)
    ImageView logoutOption;

    private Location currentLocation;
    private String currentCityCountry;

    private Socket serverSocket;
    private ServerCommunication server = null;

    private final int PICK_PHOTO_FOR_AVATAR = 11;

    byte[] inputData;
    private int updatedProfilePic = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        currentLocation = getIntent().getParcelableExtra("location");
        currentCityCountry = getIntent().getStringExtra("city_country");

        accountOption.setBackgroundColor(ContextCompat.getColor(AccountActivity.this,R.color.colorPrimaryTransp));

        serverSocket = SocialEventsApplication.getInstance().getServerSocket();

        server = SocialEventsApplication.getInstance().getServer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.getUserCredentialsFlag();
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        server.setWaitForThreadFinish(false);

        String[] userDetails = server.getUserDetails();

        username.setText(userDetails[0]);
        firstName.setText(userDetails[2]);
        lastName.setText(userDetails[3]);
        email.setText(userDetails[1]);
        if(server.getAvatarSize() != 0){
            if(server.getAvatarSize() == -1){
                Picasso.with(this).load(server.getFacebookImagePath()).transform(new RoundedTransformation()).into(profileImage);
            }
            else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("myPic", null, null);
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(server.getAvatar());
                    Picasso.with(this).load(tempFile).transform(new RoundedTransformation()).into(profileImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    @OnClick(R.id.chatbot_option)
    void onChatBotOptionClick(View view){
        Intent chatBotIntent = new Intent(this,ChatbotActivity.class);
        chatBotIntent.putExtra("location",currentLocation);
        chatBotIntent.putExtra("city_country",currentCityCountry);
        chatBotIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(chatBotIntent);
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

    @OnClick(R.id.logout_option)
    void onLogoutClick(View view){
        Intent loginIntent = new Intent(this,LoginActivity.class);
        SharedPreferences.Editor editor = getSharedPreferences("credentials", MODE_PRIVATE).edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.apply();
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    @OnClick(R.id.check_option)
    public void onCheckOptionClick(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                server.sendUserDetailsChangesAndReceiveConfirmationFlag(firstName.getText().toString(),lastName.getText().toString(),email.getText().toString(),inputData,updatedProfilePic);
            }
        }).start();

        while(!server.isWaitForThreadFinish()){

        }

        server.setWaitForThreadFinish(false);

        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(AccountActivity.this);

        alertDialog.setMessage("Details successfully modified!");
        alertDialog.setTitle("Details modified!");
        alertDialog.setPositiveButton("OK", null);
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }

    @OnClick(R.id.profile_editProfilePhoto)
    public void onProfileEditPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true).start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri photoUri = result.getUri();
            Picasso.with(this).load(photoUri).transform(new RoundedTransformation()).into(profileImage);

            try {
                InputStream inputStream = this.getContentResolver().openInputStream(photoUri);
                inputData = getBytes(inputStream);
                updatedProfilePic = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

}

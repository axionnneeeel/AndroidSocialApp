package com.example.razvan.socialeventshelper;

import com.example.razvan.socialeventshelper.Models.FriendsModel;
import com.example.razvan.socialeventshelper.Utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razvan on 4/3/2017.
 */

public class ServerCommunication {
    private Socket serverSocket;
    private DataOutputStream output;
    private DataInputStream input;

    private Integer checkLogin = 0;
    private Integer registerFlag = 0;
    private String[] userDetails = new String[4];
    private Integer avatarSize;
    private byte[] avatar;

    private List<FriendsModel> friendsList = new ArrayList<>();

    public boolean waitForThreadFinish = false;


    ServerCommunication(Socket serverSocket){
        this.serverSocket = serverSocket;

        try {
            output = new DataOutputStream(this.serverSocket.getOutputStream());
            input = new DataInputStream(this.serverSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkIfLogged(String user,String pass){
        try {
            output.write(1);
            output.writeUTF(user);
            output.writeUTF(pass);
            output.flush();

            Integer loginFlag = input.read();
            checkLogin = loginFlag;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerOrCheckRegisterValidity(String user,String pass,String email){
        try {
            output.write(2);
            output.writeUTF(user);
            output.writeUTF(pass);
            output.writeUTF(email);
            output.flush();

            Integer registerFlag = input.read();
            this.registerFlag = registerFlag;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserCredentials(){
        try {
            output.write(3);
            output.flush();

            this.userDetails[0] = input.readUTF();
            this.userDetails[1] = input.readUTF();
            this.userDetails[2] = input.readUTF();
            this.userDetails[3] = input.readUTF();

            this.avatarSize = input.readInt();
            if(avatarSize != 0){
                avatar = new byte[avatarSize];
                input.readFully(this.avatar,0,avatarSize);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserDetailsChangesAndReceiveConfirmation(String firstName,String lastName,String email,byte[] avatar,Integer updatedProfilePicture){
        try {
            output.write(4);
            output.writeUTF(firstName);
            output.writeUTF(lastName);
            output.writeUTF(email);

            if(updatedProfilePicture == 1) {
                int avatarLength = avatar.length;
                output.writeInt(avatarLength);
                output.write(avatar, 0, avatarLength);
                output.flush();
            }
            else output.writeInt(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserFriends(){
        try {
            output.write(5);
            output.flush();

            Integer friendsNumber = input.readInt();
            for(int i=0;i<friendsNumber;i++){
                String friendName = input.readUTF();

                Integer avatarSize = input.readInt();
                if(avatarSize != 0){
                    byte[] avatar = new byte[avatarSize];
                    input.readFully(avatar,0,avatarSize);
                    File tempFile = null;
                    try {
                        tempFile = File.createTempFile(System.currentTimeMillis()+"", null, null);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        fos.write(avatar);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FriendsModel thisFriend = new FriendsModel(friendName,0,tempFile);
                    friendsList.add(thisFriend);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isWaitForThreadFinish() {
        return waitForThreadFinish;
    }

    public void setWaitForThreadFinish(boolean waitForThreadFinish) {
        this.waitForThreadFinish = waitForThreadFinish;
    }

    public Integer getCheckLogin() {
        return checkLogin;
    }

    public void setCheckLogin(Integer checkLogin) {
        this.checkLogin = checkLogin;
    }

    public Integer getRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(Integer registerFlag) {
        this.registerFlag = registerFlag;
    }

    public String[] getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(String[] userDetails) {
        this.userDetails = userDetails;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Integer getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(Integer avatarSize) {
        this.avatarSize = avatarSize;
    }

    public List<FriendsModel> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<FriendsModel> friendsList) {
        this.friendsList = friendsList;
    }
}

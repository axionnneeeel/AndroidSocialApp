package com.example.razvan.socialeventshelper;

import com.example.razvan.socialeventshelper.Friends.FriendsModel;
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

    private Integer userExists = 0;

    private List<FriendsModel> friendsList = new ArrayList<>();

    private List<String> messagesList = new ArrayList<>();
    private Integer messagesNumber;

    private String newMessageUser="";
    private String message="";

    public boolean waitForThreadFinish = false;


    public ServerCommunication(Socket serverSocket){
        this.serverSocket = serverSocket;

        try {
            output = new DataOutputStream(this.serverSocket.getOutputStream());
            input = new DataInputStream(this.serverSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginFlag(String user,String pass){
        try {
            output.write(1);
            output.writeUTF(user);
            output.writeUTF(pass);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkIfLogged(){
        try {
            Integer loginFlag = input.read();
            checkLogin = loginFlag;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerOrCheckRegisterValidityFlag(String user,String pass,String email){
        try {
            output.write(2);
            output.writeUTF(user);
            output.writeUTF(pass);
            output.writeUTF(email);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerOrCheckRegisterValidity(){
        try {
            Integer registerFlag = input.read();
            this.registerFlag = registerFlag;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserCredentialsFlag(){
        try {
            output.write(3);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserCredentials(){
        try {
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

    public void sendUserDetailsChangesAndReceiveConfirmationFlag(String firstName,String lastName,String email,byte[] avatar,Integer updatedProfilePicture){
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

    public void sendFriendsFlag(){
        try {
            output.write(5);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserFriends(){
        try {
            friendsList.clear();
            Integer friendsNumber = input.readInt();
            for(int i=0;i<friendsNumber;i++){
                Integer friendID = input.readInt();
                String friendName = input.readUTF();
                Integer friendStatus = input.readInt();

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
                    FriendsModel thisFriend = new FriendsModel(friendID,friendName,friendStatus,tempFile);
                    friendsList.add(thisFriend);
                }
                else{
                    FriendsModel thisFriend = new FriendsModel(friendID,friendName,friendStatus);
                    friendsList.add(thisFriend);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserToBeDeletedFlag(Integer friendId){
        try {
            output.write(6);
            output.writeInt(friendId);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendUserToBeAddedFlag(String userToBeAdded){
        try {
            output.write(7);
            output.writeUTF(userToBeAdded);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserToBeAdded(){
        try {
            Integer noUser = input.readInt();
            this.userExists = noUser;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getConversationFlag(String userToChat){
        try {
            output.write(8);
            output.writeUTF(userToChat);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getConversation(){
        try {
            messagesList.clear();
            Integer noMessages = input.readInt();
            this.messagesNumber = noMessages;
            if(noMessages != -1){
                for(int i=0;i<noMessages;i++){
                    this.messagesList.add(input.readUTF());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageFlag(String userToChat,String message){
        try {
            output.write(9);
            output.writeUTF(userToChat);
            output.writeUTF(message);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserNewMessages(String friendName,String message){
        this.newMessageUser = friendName;
        this.message = message;
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


    public Integer getUserExists() {
        return userExists;
    }

    public void setUserExists(Integer userExists) {
        this.userExists = userExists;
    }

    public Integer getMessagesNumber() {
        return messagesNumber;
    }

    public void setMessagesNumber(Integer messagesNumber) {
        this.messagesNumber = messagesNumber;
    }

    public List<String> getMessagesList() {
        return messagesList;
    }

    public void setMessagesList(List<String> messagesList) {
        this.messagesList = messagesList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNewMessageUser() {
        return newMessageUser;
    }

    public void setNewMessageUser(String newMessageUser) {
        this.newMessageUser = newMessageUser;
    }
}

package com.example.razvan.socialeventshelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserDetailsChangesAndReceiveConfirmation(String firstName,String lastName,String email,byte[] avatar){
        try {
            output.write(4);
            output.writeUTF(firstName);
            output.writeUTF(lastName);
            output.writeUTF(email);
            output.write(avatar.length);
            output.write(avatar);
            output.flush();

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
}

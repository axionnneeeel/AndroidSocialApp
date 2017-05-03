package com.example.razvan.socialeventshelper.Models;

import java.io.File;

/**
 * Created by Razvan on 5/3/2017.
 */

public class FriendsModel {
    private String friendName;
    private Integer friendStatus;
    private File friendAvatar;

    public FriendsModel(){

    }

    public FriendsModel(String friendName,Integer friendStatus,File friendAvatar){
        this.friendName = friendName;
        this.friendStatus = friendStatus;
        this.friendAvatar = friendAvatar;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public Integer getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(Integer friendStatus) {
        this.friendStatus = friendStatus;
    }

    public File getFriendAvatar() {
        return friendAvatar;
    }

    public void setFriendAvatar(File friendAvatar) {
        this.friendAvatar = friendAvatar;
    }
}

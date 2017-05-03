package com.example.razvan.socialeventshelper.Models;

import java.io.File;

/**
 * Created by Razvan on 5/3/2017.
 */

public class FriendsModel {
    private Integer friendId;
    private String friendName;
    private Integer friendStatus;
    private File friendAvatar;

    public FriendsModel(){

    }

    public FriendsModel(Integer friendId,String friendName,Integer friendStatus,File friendAvatar){
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendStatus = friendStatus;
        this.friendAvatar = friendAvatar;
    }

    public FriendsModel(Integer friendId,String friendName,Integer friendStatus){
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendStatus = friendStatus;
        this.friendAvatar = null;
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

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }
}

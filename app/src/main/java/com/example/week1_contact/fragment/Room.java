package com.example.week1_contact.fragment;

import java.util.ArrayList;

public class Room {
    private ArrayList<String> userName;
    private int userNum;
    private String userScore;
    private String roomName;


    public void setUserName(ArrayList<String> userName){
        this.userName = userName;
    }
    public void setUserScore(String userScore){
        this.userScore = userScore;
    }
    public void setUserNum(int userNum){
        this.userNum = userNum;
    }
    public void setRoomNum(String roomName){
        this.roomName = roomName;
    }

    public ArrayList<String> getUserName(){
        return this.userName;
    }
    public String getUserScore(){
        return this.userScore;
    }
    public int getUserNum(){
        return this.userNum;
    }
    public String getRoomName(){
        return this.roomName;
    }
}

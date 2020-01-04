package com.example.week1_contact.fragment;

import android.net.Uri;

public class Player {
    private String userName;
    private String userScore;

    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setUserScore(String userScore){
        this.userScore = userScore;
    }

    public String getUserName(){
        return this.userName;
    }
    public String getUserScore(){
        return this.userScore;
    }
}

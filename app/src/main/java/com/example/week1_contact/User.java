package com.example.week1_contact;

import android.widget.Gallery;

import java.util.List;

public class User {
    private int content_id;
    private String username;
    private String password;
    private List<ContactData> contacts;
    private List<GalleryData> gallery;
    private int score;
    private boolean isOn;
    private List<Problem> problem;

    public User(int content_id, String username, String password, List<ContactData> contactsData, List<GalleryData> gallerysData){
        this.content_id = content_id;
        this.username = username;
        this.password = password;
        this.contacts = contactsData;
        this.gallery = gallerysData;
    }

    public int getContent_id() {
        return content_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<ContactData> getContacts() {
        return contacts;
    }

    public List<GalleryData> getGallery() {
        return gallery;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContactsData(List<ContactData> contactsData) {
        this.contacts = contactsData;
    }

    public void setGallerysData(List<GalleryData> gallerysData) {
        this.gallery = gallerysData;
    }
}

package com.example.week1_contact;

public class GalleryData {
    private int photo;
    private String path;

    public GalleryData(int photo, String path){
        this.photo = photo;
        this.path = path;
    }

    public void setPhoto(int photo){
        this.photo = photo;
    }
    public void setPath(String path){
        this.path = path;
    }

    public int getPhoto(){
        return photo;
    }
    public String getPath(){
        return path;
    }
}

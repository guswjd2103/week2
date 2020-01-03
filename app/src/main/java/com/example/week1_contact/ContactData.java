package com.example.week1_contact;

public class ContactData implements  Comparable<ContactData> {
    private int photo;
    private String name;
    private String phoneNumber;
    private int id;

    public ContactData(int photo, String name, String phoneNumber, int id) {
        this.photo = photo;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    public int getPhoto() {
        return this.photo;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int compareTo(ContactData contactData) {
        return this.name.compareTo(contactData.getName());
    }
}

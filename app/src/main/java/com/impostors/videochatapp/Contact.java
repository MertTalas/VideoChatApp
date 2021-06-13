package com.impostors.videochatapp;

public class Contact {
    private String phoneNumber,name,contact_id;

    public Contact() {
    }

    public Contact(String phoneNumber,String name) {
        this.phoneNumber = phoneNumber;
        this.name=name;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

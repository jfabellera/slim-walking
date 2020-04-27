package com.example.slim_walking;

public class Contact {
    String name;
    String phoneNumber;

    public Contact(String n, String p) {
        this.name = n;
        this.phoneNumber = p;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

package com.example.slim_walking;

import android.content.Context;

public class EmergencyController {
    private Context context;
    Contact guardian1;
    Contact guardian2;
    String location;

    public EmergencyController(Context current) {
        location = "Richardson";
        this.context = current;
    }

    public String callGuardian1() {
        // call guardian

        return "Calling " + guardian1.getName();
    }

    public String callGuardian2() {
        // call guardian

        return "Calling " + guardian2.getName();
    }

    public String callLocal() {
        // determine local first responder number
        // call them

        Contact locationContact = new Contact(location, "972-883-2222");

        return "Calling " + locationContact.getName() + " police.";
    }

    public Contact getGuardian1() {
        return guardian1;
    }

    public Contact getGuardian2() {
        return guardian2;
    }

    public String getLocation() {
        return location;
    }

    public void setGuardian1(Contact guardian1) {
        this.guardian1 = guardian1;
    }

    public void setGuardian2(Contact guardian2) {
        this.guardian2 = guardian2;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}

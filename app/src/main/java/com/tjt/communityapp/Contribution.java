package com.tjt.communityapp;

import java.util.HashMap;

/**
 * Created by Ryan on 4/21/2018.
 */

public class Contribution {
    public Contribution(){}

    public String user;
    public String event;
    public float hours;
    public int highFives;
    public HashMap<String, Boolean> highFivers;
    public boolean validated;

    public String getUser() {
        return user;
    }
    public String getEvent() {
        return event;
    }
    public float getHours() {
        return hours;
    }
    public int getHighFives() {
        return highFives;
    }
    public HashMap<String, Boolean> getHighFivers() {
        return highFivers;
    }
    public boolean isValidated() {
        return validated;
    }
}

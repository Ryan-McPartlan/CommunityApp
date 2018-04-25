package com.tjt.communityapp;

import java.util.HashMap;

/**
 * Created by Ryan on 4/20/2018.
 */

public class Pin {
    public Pin(){}

    public String title;
    public long lat;
    public long lon;
    public boolean isActive;
    public boolean testPin;
    public HashMap<String, Boolean> organizers;
    public int support;
    public HashMap<String, Boolean> supporters;

    public String getTitle() {
        return title;
    }
    public long getLat() {
        return lat;
    }
    public long getLon() {
        return lon;
    }
    public boolean isActive() {
        return isActive;
    }
    public boolean isTestPin() {
        return testPin;
    }
    public HashMap<String, Boolean> getOrganizers() {
        return organizers;
    }
    public int getSupport() {
        return support;
    }
    public HashMap<String, Boolean> getSupporters() {
        return supporters;
    }

}

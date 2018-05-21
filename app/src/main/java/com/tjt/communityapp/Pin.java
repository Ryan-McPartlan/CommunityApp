package com.tjt.communityapp;

import java.util.HashMap;

/**
 * Created by Ryan on 4/20/2018.
 */

public class Pin {
    public Pin(){}

    public String title;
    public double lat;
    public double lng;
    public boolean isActive;
    public boolean testPin;
    public HashMap<String, Boolean> organizers; //IDs for users running the event
    public HashMap<String, Boolean> sponsors; //IDs for communities running the event
    public int support;
    public HashMap<String, Boolean> supporters;

    public String getTitle() {
        return title;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
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
    public HashMap<String, Boolean> getSponsors() {return sponsors;}
    public int getSupport() {
        return support;
    }
    public HashMap<String, Boolean> getSupporters() {
        return supporters;
    }

}

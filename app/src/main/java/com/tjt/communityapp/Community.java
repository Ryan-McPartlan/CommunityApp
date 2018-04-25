package com.tjt.communityapp;

import java.util.HashMap;

/**
 * Created by Ryan on 4/21/2018.
 */

public class Community {
    public Community(){}

    public String name;
    public String description;
    public HashMap<String, Boolean> members;
    public HashMap<String, Boolean> admins;
    public HashMap<String, Boolean> eventsHosted;

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public HashMap<String, Boolean> getMembers() {
        return members;
    }
    public HashMap<String, Boolean> getAdmins() {
        return admins;
    }
    public HashMap<String, Boolean> getEventsHosted() {
        return eventsHosted;
    }
}

package com.tjt.communityapp;

import java.util.HashMap;

/**
 * Created by Ryan on 4/21/2018.
 */

public class User {
    public User(){}

    public String name;
    public long registerDate;
    public HashMap<String, Boolean> contributions;
    public HashMap<String, Boolean> eventsOrganized;

    public String getName() {
        return name;
    }
    public long getRegisterDate() {
        return registerDate;
    }
    public HashMap<String, Boolean> getContributions() {
        return contributions;
    }
    public HashMap<String, Boolean> getEventsOrganized() {
        return eventsOrganized;
    }
}

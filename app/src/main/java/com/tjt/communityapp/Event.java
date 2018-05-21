package com.tjt.communityapp;

import java.util.HashMap;

/**
 * Created by Ryan on 4/20/2018.
 */

public class Event extends Pin {
    public Event(){}

    public String description;
    public String meetingAdress;
    public HashMap<String, Boolean> participants; //Strings are the users IDs

    public long startDate;
    public long endDate; //Convert these to dates when trying to use them, only long when stored.
    public HashMap<String, Boolean> tags;

    public String getDescription(){return description;}
    public String getMeetingAdress() {return meetingAdress;}
    public HashMap<String, Boolean> getParticipants() {return participants;    }
    public long getStartDate() {return startDate;}
    public long getEndDate() {        return endDate;}
    public HashMap<String, Boolean> getTags() {return tags;}
}

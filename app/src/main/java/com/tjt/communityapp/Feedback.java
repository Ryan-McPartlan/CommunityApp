package com.tjt.communityapp;

/**
 * Created by Ryan on 5/20/2018.
 */

public class Feedback {

    public Feedback(){

    }

    public String submitter;
    public String content;
    public long dateSubmitted;
    public boolean read;

    public String getSubmitter(){
        return submitter;
    }
    public String getContent(){
        return content;
    }
    public long getDateSubmitted(){
        return dateSubmitted;
    }
    public boolean isRead(){
        return read;
    }
}

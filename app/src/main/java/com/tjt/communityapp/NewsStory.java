package com.tjt.communityapp;

import android.support.annotation.NonNull;

/**
 * Created by Ryan on 5/16/2018.
 */

public class NewsStory implements Comparable<NewsStory> {
    public NewsStory(){}

    public String title;
    public String content;
    public long postDate;

    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public long getPostDate(){
        return postDate;
    }

    @Override
    public int compareTo(@NonNull NewsStory newsStory) {
        return (int)(newsStory.getPostDate() - this.postDate);
    }
}

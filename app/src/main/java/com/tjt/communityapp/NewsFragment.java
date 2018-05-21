package com.tjt.communityapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Ryan on 5/15/2018.
 */

public class NewsFragment extends Fragment implements View.OnClickListener{

    TextView title;
    TextView date;
    TextView content;

    Button nextStoryButton;
    Button previousStoryButton;

    private ArrayList<NewsStory> stories = new ArrayList<>();
    private ArrayList<NewsStory> storyBacklog = new ArrayList<>();
    private int currentIndex = 0;

    public NewsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_news, container, false);
        getActivity().setTitle("News");

        //Get the views we need to update or
        nextStoryButton = rootView.findViewById(R.id.nextButton);
        previousStoryButton = rootView.findViewById(R.id.prevButton);
        nextStoryButton.setOnClickListener(this);
        previousStoryButton.setOnClickListener(this);

        title = rootView.findViewById(R.id.newsTitle);
        date = rootView.findViewById(R.id.newsDate);
        content = rootView.findViewById(R.id.newsContent);

        //Get all the news stories and add them to our newsStory arraylist
        App.s.fDatabase.child("news").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> rawStories = dataSnapshot.getChildren();

                while (rawStories.iterator().hasNext()){
                    NewsStory newStory = rawStories.iterator().next().getValue(NewsStory.class);
                    if(newStory != null){
                        Log.d("Test", "Not null");
                        stories.add(newStory);
                    }
                }

                Collections.sort(stories);
                showStory(stories.get(0));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                App.s.dismissProgress();
            }
        });

        return rootView;
    }

    private void showStory(NewsStory story){
        title.setText(story.getTitle());

        Date storyDate = new Date(story.postDate);
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
        date.setText(ft.format(storyDate));

        content.setText(story.getContent());
    }

    //Stories are sorted in decending order, with the most recent at index 0.
    //Next takes us to the more recent stories at a lower index, prev does the opposite
    public void nextStory(){
        //
        if(currentIndex != 0){
            currentIndex -= 1;
            showStory(stories.get(currentIndex));
        }

        //If we leave the oldest story, enable the other button
        if(currentIndex == stories.size()-2){
            previousStoryButton.setEnabled(true);
        }

        //If we reach the most recent story, disable the button
        if(currentIndex == 0){
            nextStoryButton.setEnabled(false);
        }
    }
    public void previousStory(){
        if(currentIndex != stories.size()-1){
            currentIndex += 1;
            showStory(stories.get(currentIndex));
        }

        //If we leave the newest story, enable the other button
        if(currentIndex == 1){
            nextStoryButton.setEnabled(true);
        }

        //If we go to the oldest story, disable the button
        if(currentIndex == stories.size() - 1){
            previousStoryButton.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prevButton:
                previousStory();
                break;
            case R.id.nextButton:
                nextStory();
                break;
        }
    }
}

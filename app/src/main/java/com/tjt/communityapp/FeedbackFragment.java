package com.tjt.communityapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

/**
 * Created by Ryan on 5/20/2018.
 */

public class FeedbackFragment extends Fragment {

    EditText feedbackText;
    Button submitFeedbackButton;

    public FeedbackFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_feedback, container, false);
        getActivity().setTitle("Feedback");

        feedbackText = rootView.findViewById(R.id.feedbackEditText);
        submitFeedbackButton = rootView.findViewById(R.id.submitFeedbackButton);

        //Call submitFeedback when we click the button
        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFeedback();
            }
        });

        return rootView;
    }

    public void submitFeedback(){
        //Create the new feedback object
        Feedback newFeedback = new Feedback();
        newFeedback.submitter = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newFeedback.dateSubmitted = new Date().getTime();
        newFeedback.content = feedbackText.getText().toString();
        newFeedback.read = false;

        //Save it to firebase
        DatabaseReference newFeedbackRef = App.s.fDatabase.child("feedback").push();
        newFeedbackRef.setValue(newFeedback);

        //Return to the map
        App.s.toast("Feedback submitted successfully! Returning to map!");
        getActivity().finish();
        App.s.setActivity(MainActivity.class);
    }
}

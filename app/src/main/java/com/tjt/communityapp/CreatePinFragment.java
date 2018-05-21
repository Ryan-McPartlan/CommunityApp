package com.tjt.communityapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ryan on 5/16/2018.
 */

public class CreatePinFragment extends Fragment {

    int MIN_TITLE_LENGTH = 4;
    int MIN_DESCRIPTION_LENGTH = 30;
    int MAX_DAYS_BEFORE_EVENT = 31;
    int MIN_DURATION = 15;//Minutes


    EditText titleView;
    EditText descriptionView;
    DatePicker datePickerView;
    TimePicker startTimeView;
    TimePicker endTimeView;
    Button createEventButton;

    public CreatePinFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_create_pin, container, false);
        getActivity().setTitle("Create Event");

        titleView = rootView.findViewById(R.id.createEventTitle);
        descriptionView = rootView.findViewById(R.id.createDescription);
        datePickerView = rootView.findViewById(R.id.createDate);
        startTimeView = rootView.findViewById(R.id.createStartTime);
        endTimeView = rootView.findViewById(R.id.createEndTime);

        //The button cant find the function that is in a fragment.
        // Instead, set it to call the attemptCreateEvent function when clicked here in code
        createEventButton = rootView.findViewById(R.id.createPinButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateEvent();
            }
        });

        return rootView;
    }

    public void attemptCreateEvent(){
        Event newEvent = new Event();

        //Get the dates for the event
        int eventYear = datePickerView.getYear();
        int eventMonth = datePickerView.getMonth();
        int eventDay = datePickerView.getDayOfMonth();
        int startHour = startTimeView.getHour();
        int startMinute = startTimeView.getMinute();
        int endHour = endTimeView.getHour();
        int endMinute = endTimeView.getMinute();
        Calendar startCalendar = new GregorianCalendar(eventYear, eventMonth, eventDay, startHour, startMinute);
        Calendar endCalendar = new GregorianCalendar(eventYear, eventMonth, eventDay, endHour, endMinute);
        Date startTime = new Date(startCalendar.getTimeInMillis());
        Date endTime = new Date(endCalendar.getTimeInMillis());

        //Set the values
        newEvent.title = titleView.getText().toString();
        newEvent.description = descriptionView.getText().toString();
        newEvent.startDate = startTime.getTime();
        newEvent.endDate = endTime.getTime();
        newEvent.organizers = new HashMap<>();
        newEvent.organizers.put(App.s.fAuth.getCurrentUser().getUid(), true);
        newEvent.sponsors = new HashMap<>();
        newEvent.lat = App.s.newPinCoords.latitude;
        newEvent.lng = App.s.newPinCoords.longitude;

        if(validateEvent(newEvent)){
            createEvent(newEvent);
        }
    }

    //Make sure the info provided is good
    private boolean validateEvent(Event event){
        Date today = new Date();

        if(event.title.replaceAll("\\s+","").length() < MIN_TITLE_LENGTH){
            App.s.toast("Event title must be at least " + MIN_TITLE_LENGTH +" non-blank characters");
            return false;
        } else if(event.description.replaceAll("\\s+","").length() < MIN_DESCRIPTION_LENGTH){
            App.s.toast("Event description must be at least " + MIN_DESCRIPTION_LENGTH +" non-blank characters");
            return false;
        } else if(event.startDate < today.getTime()){
            App.s.toast("You can't create a pin for an event that already happened! Is the date you entered a mistake?");
            return false;
        } else if(event.endDate - today.getTime() > TimeUnit.DAYS.toMillis(MAX_DAYS_BEFORE_EVENT)){
            App.s.toast("You can only make an event up to " + MAX_DAYS_BEFORE_EVENT + " days in advance!");
            return false;
        } else if(event.endDate  < event.startDate){
            App.s.toast("Your end time is earlier than your start time. Are you trying to make a multi day event? The app does not support those yet.");
            return false;
        } else if(TimeUnit.MILLISECONDS.toMinutes(event.endDate - event.startDate) < MIN_DURATION){
            App.s.toast("Events must be at least " + MIN_DURATION + " minutes long. Please give us feedback if this seems problematic!");
            return false;
        }

        return true;
    }

    private void createEvent(Event event){
        DatabaseReference newPinLocation = App.s.fDatabase.child("pins").push();

        //Set our pin referance's value to our pin
        newPinLocation.setValue(event);

        //Store our pins kew and latlang in geofire
        GeoLocation pinLocation = new GeoLocation(event.lat, event.lng);
        App.s.geoFire.setLocation(newPinLocation.getKey(), pinLocation, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                //Completion listener needed to prevent some geofire bug
            }
        });

        App.s.toast("Event created! Anyone can see it!");
        getActivity().onBackPressed(); //We use this to finish and go back to the map
    }
}

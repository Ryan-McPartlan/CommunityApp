package com.tjt.communityapp;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class PersonalPageFragment extends Fragment {

    TextView memberSinceText;
    TextView hoursText;
    TextView eventsText;
    Switch toggleVerified;

    ArrayList<Contribution> contributions = new ArrayList<>();

    public PersonalPageFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_personal, container, false);

        memberSinceText = rootView.findViewById(R.id.memberSince);
        hoursText = rootView.findViewById(R.id.serviceHours);
        eventsText = rootView.findViewById(R.id.eventsAttended);
        toggleVerified = rootView.findViewById(R.id.verifiedHoursOnly);

        if(memberSinceText == null){
            Log.d("Test", "test");
        }

        memberSinceText.setText(getMemberSinceText());
        hoursText.setText(getHoursText());
        eventsText.setText(getEventsText());

        getActivity().setTitle("My Page");
        return rootView;
    }

    private String getMemberSinceText(){
        Date registerDate = new Date(App.s.currentUser.registerDate);
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
        String registerDateString = ft.format(registerDate);

        Date newDate = new Date();
        long milliDifference = newDate.getTime() - registerDate.getTime();
        float days = TimeUnit.MILLISECONDS.toDays(milliDifference);

        String unit = "day";
        String numberOfUnits = "1";

        //If this is their first day...
        if(days < 1){
            return "Member since " + registerDateString + ". Welcome!";
        }

        if(days > 365){
            unit = "years";
            numberOfUnits = Integer.toString((int)(days / 365));
        } else if(days > 31){
            unit = "months";
            numberOfUnits = Integer.toString((int)(days / 31));
        } else {
            unit = "days";
            numberOfUnits = Integer.toString((int)days);
        }

        return "Member since " + registerDateString + ". (" + numberOfUnits + " " + unit + ")";
    }

    private String getHoursText(){
        return "Service hours: None yet!";
    }

    private String getEventsText(){
        return "Events attended: None so far!";
    }

}

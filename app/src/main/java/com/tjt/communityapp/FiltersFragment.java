package com.tjt.communityapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ryan on 5/15/2018.
 */

public class FiltersFragment extends Fragment {

    public FiltersFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_filters, container, false); //TODO:

        return rootView;
    }

}

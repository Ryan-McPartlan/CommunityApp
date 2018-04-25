package com.tjt.communityapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //if(App.singleton == null){
            App.singleton.easyToast("Welcome!");
        ///}
    }

    public void tempLogin(View view){
        App.singleton.easyToast("Logging in!");
        App.singleton.changeActivity(MapActivity.class);
    }

}

package com.tjt.communityapp;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Ryan on 4/21/2018.
 */

public class App extends Application{

    public static App singleton;
    public User currentUser;

    @Override
    public void onCreate(){
        super.onCreate();
        singleton = this;
    }

    public void easyToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void setActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}

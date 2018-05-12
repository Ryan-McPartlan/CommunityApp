package com.tjt.communityapp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ryan on 4/21/2018.
 */

public class App extends Application{

    public static App s;
    public User currentUser;

    public FirebaseAuth fAuth;
    public DatabaseReference fDatabase;

    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(){
        super.onCreate();
        s = this;
        fDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    public void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void snackbar(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG ).show();
    }

    public void progress(String message){
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgress(){
        progressDialog.dismiss();
    }

    public void setActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}

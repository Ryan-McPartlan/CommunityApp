package com.tjt.communityapp;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ryan on 4/21/2018.
 */

public class App extends Application{

    //The singleton
    public static App s;

    //The current user
    public User currentUser;

    //References to our other singletons
    public FirebaseAuth fAuth;
    public DatabaseReference fDatabase;

    //Request codes so we know what proccess is being handled in onRequestPermissionsResult
    private final int LOCATION_PERMISION_REQUEST_CODE = 1;

    //A progressDialog we use when loading or requesting data, in progress()
    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(){
        super.onCreate();
        s = this;
        fDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    //Self-explanitory and simple functions for affordance
    public void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void snackbar(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG ).show();
    }
    public void dialog(DialogFragment dialog, Activity activity){
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        dialog.show(ft, "dialog");
    }
    public void progress(String message){
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    public void dismissProgress(){
        progressDialog.dismiss();
    }

    //Change the activity
    public void setActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}

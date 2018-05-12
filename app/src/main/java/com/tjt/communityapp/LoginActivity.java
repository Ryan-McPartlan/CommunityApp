package com.tjt.communityapp;

import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText usernameView;
    EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App.s.toast("Welcome!");

        EditText usernameView = findViewById(R.id.enterEmail);
        EditText passwordView = findViewById(R.id.enterPassword);
    }

    //Test
    public void tempLogin(View view){
        App.s.toast("Logging in!");
        App.s.setActivity(MainActivity.class);
    }

    //Logs us into firebase auth
    public void attemptLogin(View view){
        App.s.progress("Verifying credentials...");

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        App.s.fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    login();
                } else{
                    App.s.toast(task.getException().getMessage());
                    App.s.dismissProgress();
                }
            }
        });
    }

    //If we successfully firebase login, get the user's info and move to the main menu
    public void login() {
        //Show the 'logging in' dialog box
        App.s.progress("Getting user data...");

        //Get the users account from firebase
        App.s.fDatabase.child("users/" + App.s.fAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                App.s.currentUser = dataSnapshot.getValue(User.class);
                App.s.dismissProgress();

                App.s.toast("Login success. Welcome " + App.s.fAuth.getCurrentUser().getEmail() + "!");
                App.s.setActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                App.s.dismissProgress();
            }
        });
    }

    public void register(View view){
        App.s.setActivity(RegisterActivity.class);
    }

    public void forgotPassword(View view){
        App.s.snackbar(findViewById(R.id.mainLayout), "Test snacky wacky!");

        ForgotPasswordDialog forgotPassDialog = new ForgotPasswordDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        forgotPassDialog.show(ft, "forgotPassDialog");
    }

}

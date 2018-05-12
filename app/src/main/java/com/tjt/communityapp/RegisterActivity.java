package com.tjt.communityapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText emailView;
    EditText passwordView;
    EditText confirmPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailView = findViewById(R.id.enterEmail);
        passwordView = findViewById(R.id.enterPassword);
        confirmPasswordView = findViewById(R.id.confirmPassword);
    }

    public void register(View view){
        String email = emailView.getText().toString();
        String password = emailView.getText().toString();
        String passConfirm = emailView.getText().toString();

        if(password == passConfirm){
            App.s.fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        finishRegister();
                        login();
                    }
                }
            });
        } else{
            App.s.toast("Passwords must match.");
        }
    }

    public void finishRegister(){
        User newUser = new User();

        //Get a reference to the firebase database, and set it to newUser
        DatabaseReference userEntry = FirebaseDatabase.getInstance().getReference().child("users/" + App.s.fAuth.getCurrentUser().getUid());
        userEntry.setValue(newUser);
    }

    //This function is copy-pasted from login. Very
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

}

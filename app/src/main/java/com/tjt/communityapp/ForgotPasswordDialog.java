package com.tjt.communityapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View forgotPasswordView = inflater.inflate(R.layout.dialog_forgot_password, null);
        builder.setView(forgotPasswordView);


        builder.setTitle("Forgot password")
                .setMessage("Enter the email adress attached to your account. You will recieve a password reset email.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText textIn = forgotPasswordView.findViewById(R.id.forgotPasswordEmailInput);
                        if(textIn.length() < 5){
                            App.s.toast("Please input a valid email");
                            return;
                        }
                        else {
                            String email = textIn.getText().toString().trim();

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();

                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                App.s.toast("Email sent!");
                                            } else {
                                                App.s.toast("Could not find email");
                                            }
                                        }
                                    });
                        }
                    }
                });
        return builder.create();
    }
}

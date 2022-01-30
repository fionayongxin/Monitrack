package com.example.monitrackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    //defining variables
    private EditText userEmail;
    private Button resetButton;
    private ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        //link with layout
        userEmail = (EditText) findViewById(R.id.email_reset_pass);
        resetButton = (Button) findViewById(R.id.reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //get firebase instance
        mAuth = FirebaseAuth.getInstance();

        //after click reset password button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    //reset password activity
    private void resetPassword() {
        //get email to string
        String email = userEmail.getText().toString().trim();

        //error message if email empty
        if(email.isEmpty()) {
            userEmail.setError("Email field is mandatory!");
            userEmail.requestFocus();
            return;
        }
        //error message if email not available in firebase
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter valid email.");
            userEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //success message
                if(task.isSuccessful()) {
                    Toast.makeText(ResetPassActivity.this, "Check your email to reset password.", Toast.LENGTH_LONG).show();
                }
                //unsuccessful message
                else{
                    Toast.makeText(ResetPassActivity.this, "Email is not found.", Toast.LENGTH_LONG).show();
                }
                //return to login page
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
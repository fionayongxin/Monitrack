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

    private EditText emailEdit;
    private Button resetPasswordButton;
    private ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        emailEdit = (EditText) findViewById(R.id.email_reset_pass);
        resetPasswordButton = (Button) findViewById(R.id.reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();

            }
        });
    }

    private void resetPassword() {
        String email = emailEdit.getText().toString().trim();

        if(email.isEmpty()) {
            emailEdit.setError("Email field is mandatory!");
            emailEdit.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Please enter valid email.");
            emailEdit.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(ResetPassActivity.this, "Check your email to reset password.", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else{
                    Toast.makeText(ResetPassActivity.this, "Email not found.", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });
    }
}
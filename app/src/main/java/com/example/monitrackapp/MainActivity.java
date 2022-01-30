package com.example.monitrackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //defining variable
    private EditText userEmail;
    private EditText userPass;
    private Button btnLogin;
    private TextView forgotPass;
    private TextView goSignup;

    private ProgressDialog mDialog;
    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //display activity_main layout

        mAuth = FirebaseAuth.getInstance();

        //display HomeActivity if user already logged in
        if (mAuth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        mDialog = new ProgressDialog(this);

        //get user login emails and password
        loginDetails();
    }

    //login details function
    private void loginDetails() {

        userEmail = findViewById(R.id.email_login);
        userPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.button_login);
        forgotPass = findViewById(R.id.forgot_password);
        goSignup = findViewById(R.id.goto_signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get user email and password into string
                String email = userEmail.getText().toString().trim();
                String pass = userPass.getText().toString().trim();

                //if no email entered
                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Email is required!");
                    return;
                }
                //if no password entered
                if (TextUtils.isEmpty(pass)) {
                    userPass.setError("Password is required!");
                    return;
                }
                mDialog.setMessage("Processing!");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if login successful, go to HomeActivity
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_LONG).show();
                        }
                        //message if log in not successful
                        else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Incorrect email or password.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //activity for sign up - user click sign up option
        goSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        //activity for forgetting password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetPassActivity.class));
            }
        });
    }
}
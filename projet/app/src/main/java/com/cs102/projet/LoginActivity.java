package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
{
    FirebaseAuth myFirebaseAuth;

    // EditText's id..
    EditText email;
    EditText password;

    Button loginButton;
    Button forgetPasswordButton;
    Button singUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //EditText's id..
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);

        //Button's id..
        loginButton = (Button)findViewById(R.id.loginButton);
        forgetPasswordButton = (Button)findViewById(R.id.forgetPasswordButton);
        singUpButton = (Button)findViewById(R.id.signUpButton);

        // Firebase Auth..
        myFirebaseAuth = FirebaseAuth.getInstance();


        // Check user already sign in or not..


        // Button's ClickListeners..

        // Sing Up Button Sends to register activity..
        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Login Button..
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOut = email.getText().toString().trim();
                String passwordOut = password.getText().toString().trim();

                if (emailOut.isEmpty()){
                    email.setError("Please enter email");
                    email.requestFocus();
                }
                else if (passwordOut.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if (emailOut.isEmpty() && passwordOut.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    myFirebaseAuth.signInWithEmailAndPassword(emailOut, passwordOut).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Logged In ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Login Fail, Try Again", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });


        // Forget PassWord Button..

        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOut = email.getText().toString().trim();
                myFirebaseAuth.sendPasswordResetEmail(emailOut).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Check your mail", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Fail to send you email!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


    }
}

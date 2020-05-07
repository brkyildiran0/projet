package com.cs102.projet.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.MainPageActivity;
import com.cs102.projet.R;
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
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //Button's id..
        loginButton = findViewById(R.id.loginButton);
        forgetPasswordButton = findViewById(R.id.forgetPasswordButton);
        singUpButton = findViewById(R.id.signUpButton);

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

                finish();
            }
        });

        //Forget button on click
        forgetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
            }
        });
    }
}

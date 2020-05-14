package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.activities.ProjetMainPageActivity;
import com.cs102.projet.loginpage.ForgetPasswordActivity;
import com.cs102.projet.loginpage.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
{
    FirebaseAuth myFirebaseAuth;
    EditText email;
    EditText password;
    Button loginButton;
    Button forgetPasswordButton;
    Button singUpButton;

    //OnCreate Method
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View initialize
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgetPasswordButton = findViewById(R.id.forgetPasswordButton);
        singUpButton = findViewById(R.id.signUpButton);

        //Firebase auth initialize
        myFirebaseAuth = FirebaseAuth.getInstance();

        if (myFirebaseAuth.getCurrentUser() != null)
        {
            // if User not logged go to login activity
            Intent intent = new Intent(LoginActivity.this, ProjetMainPageActivity.class);
            startActivity(intent);
            finish();
        }

        //SignUpButton onClick
        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //LoginButton onClick
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!email.getText().toString().equals("") && !password.getText().toString().equals(""))
                {
                    String emailOut = email.getText().toString().trim();
                    String passwordOut = password.getText().toString().trim();

                    myFirebaseAuth.signInWithEmailAndPassword(emailOut, passwordOut).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Logged In ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, ProjetMainPageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Wrong mail or password!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(LoginActivity.this, "Please enter mail & password", Toast.LENGTH_SHORT).show();
            }
        });

        //ForgetPasswordButton onClick
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

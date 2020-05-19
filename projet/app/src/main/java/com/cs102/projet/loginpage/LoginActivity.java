package com.cs102.projet.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;
import com.cs102.projet.activities.ProjetMainPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity
{
    FirebaseAuth myFirebaseAuth;
    EditText email;
    EditText password;
    Button loginButton;
    Button forgetPasswordButton;
    Button singUpButton;
    Boolean darkModePreference;

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

        //Dark mode default value set
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (myFirebaseAuth.getCurrentUser() != null)
        {
            //DARK MODE HANDLING
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            FirebaseUser currentUser = myFirebaseAuth.getCurrentUser();;
            String currentUserMail = currentUser.getEmail();

            //Set the app theme (dark-mode) according to the user's preference
            database.collection("Users").document(currentUserMail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    darkModePreference = documentSnapshot.getBoolean("user_darkmode_preference");

                    if (darkModePreference)
                    {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    else
                    {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });



            //If user already logged in, go to main page.
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

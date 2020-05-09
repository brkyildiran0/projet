package com.cs102.projet.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    EditText userNameInput;
    EditText emailInput;
    EditText passwordInput;
    Button createUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //View initialize
        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        createUserButton = findViewById(R.id.createUser);

        //Firebase Auth & Database Initialize
        myFirebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        //CreateUseButton onClick
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailOut = emailInput.getText().toString().trim();
                String passwordOut = passwordInput.getText().toString().trim();

                //Handling Empty Input
                if (emailInput.getText().toString().isEmpty())
                    emailInput.setError("Please enter your e-mail!");
                else if (passwordInput.getText().toString().isEmpty())
                    passwordInput.setError("Please enter your password");
                else if (emailInput.getText().toString().isEmpty() && passwordInput.getText().toString().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Fill all gaps!", Toast.LENGTH_SHORT).show();
                }
                //After input handling, user creating at firebase auth center
                else
                {
                    //Firebase part..
                    myFirebaseAuth.createUserWithEmailAndPassword(emailOut, passwordOut).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                //TODO this is useless, replace with FirebaseAuth methods.
                                //Creating the registered user at database
                                Map<String, String> mailNameAdder = new HashMap<>();
                                mailNameAdder.put("user_email", emailInput.getText().toString());
                                mailNameAdder.put("user_name_surname", userNameInput.getText().toString());
                                database.collection("Users").document(emailInput.getText().toString()).set(mailNameAdder);
                                DocumentReference createdUser = database.collection("Users").document(emailInput.getText().toString());
                                Map<String, Object> projetsAdder = new HashMap<>();
                                projetsAdder.put("user_current_projets", new ArrayList<>());
                                createdUser.update(projetsAdder);
                                createdUser.update("user_projet_counter", 0);

                                //Sending user back to the login screen by finish()ing register activity
                                finish();
                                Toast.makeText(RegisterActivity.this,"Register successful", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this,"Register unsuccessful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

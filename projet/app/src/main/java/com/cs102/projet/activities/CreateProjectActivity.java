package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateProjectActivity extends AppCompatActivity
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    private EditText editTextProjetName;
    private EditText editTextProjetDesc;
    private EditText editTextProjetDueDate;
    private EditText editTextProjetDueHour;
    String currentUserMail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        //Firebase Initializing
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //connecting views to code
        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);
        editTextProjetName = findViewById(R.id.editTextProjetName);
        editTextProjetDesc = findViewById(R.id.editTextProjetDesc);
        editTextProjetDueDate = findViewById(R.id.editTextProjetDueDate);
        editTextProjetDueHour = findViewById(R.id.editTextDueHour);



        //on click listener of Create New ProJet button
        buttonCreateNewProjet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //these string will be sent to server
                String projetName = editTextProjetName.getText().toString();
                String projetDesc = editTextProjetDesc.getText().toString();
                String projetDueDate = editTextProjetDueDate.getText().toString();
                String projetDueHour = editTextProjetDueHour.getText().toString();

                //checks whether user filled all the required info or not
                if (projetName.equals("") || projetDesc.equals("") || projetDueDate.equals("") || projetDueHour.equals(""))
                {
                    Toast.makeText(CreateProjectActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //TODO find out a way to check whether a projet with desired name already exists.

                    //Hash-map to store and send the above data to server
                    Map<String, String> projetInfo = new HashMap<>();
                    projetInfo.put("projet_name", projetName);
                    projetInfo.put("projet_desc", projetDesc);
                    projetInfo.put("projet_due_date", projetDueDate);
                    projetInfo.put("projet_due_hour", projetDueHour);

                    //Adding hash-map to database
                    database.collection("ProJets").document(projetName)
                            .set(projetInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Toast.makeText(CreateProjectActivity.this, "ProJet successfully created!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(CreateProjectActivity.this, "Error. Can't create ProJet.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    //Adding the required boolean value to be able to finish a project in the future
                    Map<String, Boolean> isFinished = new HashMap<>();
                    isFinished.put("projet_is_complete", false);
                    database.collection("ProJets").document(projetName).set(isFinished, SetOptions.merge());

                    //Adding the required int value to be able to counter how many members are there in given ProJet
                    Map<String, Integer> memberCounter = new HashMap<>();
                    memberCounter.put("projet_member_counter", 1);
                    database.collection("ProJets").document(projetName).set(memberCounter, SetOptions.merge());

                    //Creating users & tasks collection inside of the projet document
                    DocumentReference userRef = database.collection("Users").document(currentUserMail);
                    database.collection("Projets").document(projetName).collection("ProJet Members").document(currentUserMail).update("member_ref", userRef);

                    //closing the creation page, and removing it from backstack
                    startActivity(new Intent(CreateProjectActivity.this, ProjetMainPageActivity.class));
                    finish();
                }
            }
        });
    }

}

package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.fragments.FragmentCreatePageMembers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CreateProjectActivity extends AppCompatActivity
{
    FirebaseFirestore database;

    private EditText editTextProjetName;
    private EditText editTextProjetDesc;
    private EditText editTextProjetDueDate;
    private EditText editTextProjetDueHour;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        //connecting views to code
        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);
        editTextProjetName = findViewById(R.id.editTextProjetName);
        editTextProjetDesc = findViewById(R.id.editTextProjetDesc);
        editTextProjetDueDate = findViewById(R.id.editTextProjetDueDate);
        editTextProjetDueHour = findViewById(R.id.editTextDueHour);

        //Firebase Initializing
        try
        {
            database = FirebaseFirestore.getInstance();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //adding Members Fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("Yüksel Berkay Erdem"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("Burak"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("Berke"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("Deniz"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("Eylül"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("İlayda"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("David"));
        ft.add(R.id.membersLayout, new FragmentCreatePageMembers("Irmak"));
        ft.commit();

        //on click listener of Create New Projet button
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
                boolean projetIsComplete = false;

                //checks whether user filled all the required info or not
                if (projetName.equals("") || projetDesc.equals("") || projetDueDate.equals("") || projetDueHour.equals(""))
                {
                    Toast.makeText(CreateProjectActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Hash-map to store and send the above data to server
                    Map<String, String> projetInfo = new HashMap<>();
                    projetInfo.put("projet_name", projetName);
                    projetInfo.put("projet_desc", projetDesc);
                    projetInfo.put("projet_due_date", projetDueDate);
                    projetInfo.put("projet_due_hour", projetDueHour);

                    //TODO find out a way to check whether a projet with desired name already exists.

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

                    //closing the creation page, and removing it from backstack
                    finish();
                    startActivity(new Intent(CreateProjectActivity.this, MainPageActivity.class));
                }
            }

            //Adding the projet to main page scroll view


        });
    }
}

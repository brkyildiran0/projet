package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateProjectActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    private EditText editTextProjetName;
    private EditText editTextProjetDesc;
    private EditText editTextProjetDueDate;
    private EditText editTextProjetDueHour;
    String currentUserMail;
    String userRealName;
    String projetName;
    String projetDesc;
    String projetDueDate;
    String projetDueHour;

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
        editTextProjetDueHour = findViewById(R.id.editTextProjetDueHour);

        // Using DatePicker in order to get valid dates
        // Date ClickListener..
        editTextProjetDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Using TimePicker in order to get valid times..
        // Hour ClickListener...
        editTextProjetDueHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //on click listener of Create New ProJet button
        buttonCreateNewProjet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //these string will be sent to server
                projetName = editTextProjetName.getText().toString();
                projetDesc = editTextProjetDesc.getText().toString();
                projetDueDate = editTextProjetDueDate.getText().toString();
                projetDueHour = editTextProjetDueHour.getText().toString();

                //Query to check whether a projet with the same name exists
                Query myQuery = database.collection("ProJets").whereEqualTo("projet_name", projetName);

                //Handling empty input with if-else
                if (projetName.equals("") || projetDesc.equals("") || projetDueDate.equals("") || projetDueHour.equals(""))
                {
                    Toast.makeText(CreateProjectActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    myQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            //IF there is no such projet with the desired name exists,
                            if (queryDocumentSnapshots.isEmpty())
                            {
                                //Adding the essential parts of a projet and creating it at ProJets collection
                                Map<String, String> projetInfo = new HashMap<>();
                                projetInfo.put("projet_name", projetName);
                                projetInfo.put("projet_desc", projetDesc);
                                projetInfo.put("projet_due_date", projetDueDate);
                                projetInfo.put("projet_due_hour", projetDueHour);
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

                                //Creating a DocumentReference for current projet to make code cleaner
                                DocumentReference projetReference = database.collection("ProJets").document(projetName);

                                //Adding the required boolean value to be able to finish a project in the future
                                Map<String, Boolean> isFinished = new HashMap<>();
                                isFinished.put("projet_is_complete", false);
                                projetReference.set(isFinished, SetOptions.merge());

                                //Creating users & tasks collection inside of the projet document and initializing them with currentUser's data
                                Map<String, DocumentReference> userInit = new HashMap<>();
                                userInit.put("user_reference", database.collection("Users").document(currentUserMail));
                                projetReference.collection("Members").document(currentUserMail).set(userInit, SetOptions.merge());

                                //Adding real name attribute to the user
                                database.collection("Users").document(currentUserMail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        userRealName = documentSnapshot.getString("user_name");
                                        assert userRealName != null;
                                        Log.d("User's real name is:", userRealName);

                                        Map<String, String> userInit2 = new HashMap<>();
                                        userInit2.put("user_name", userRealName);
                                        database.collection("ProJets").document(projetName).collection("Members").document(currentUserMail).set(userInit2, SetOptions.merge());
                                    }
                                });

                                //DocRef for creator of user, to improve code clarity
                                DocumentReference creatorUser = database.collection("Users").document(currentUserMail);

                                //Adding the projet reference and other properties of it to the creator of projet's current projets document
                                Map<String, DocumentReference> userProjetUpdate1 = new HashMap<>();
                                userProjetUpdate1.put("projet_reference", projetReference);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate1, SetOptions.merge());

                                Map<String, String> userProjetUpdate2 = new HashMap<>();
                                userProjetUpdate2.put("projet_name", projetName);
                                userProjetUpdate2.put("projet_desc", projetDesc);
                                userProjetUpdate2.put("projet_due_date", projetDueDate);
                                userProjetUpdate2.put("projet_due_hour", projetDueHour);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate2, SetOptions.merge());
                            }
                            //If there is another projet with  the desired name, adding an invisible char to the end of the desired projet name
                            else
                            {
                                //Adding the essential parts of a projet and creating it at ProJets collection BY CONSIDERING ANOTHER PROJET EXIST WITH THE SAME NAME
                                //TODO THIS IS A BAD SOLUTION, DOES NOT WORK FOR MORE THAN 2 PROJET WITH SAME NAME, FIX THIS BURAK XD
                                projetName = projetName + "　";
                                Map<String, String> projetInfo = new HashMap<>();
                                projetInfo.put("projet_name", projetName);
                                projetInfo.put("projet_desc", projetDesc);
                                projetInfo.put("projet_due_date", projetDueDate);
                                projetInfo.put("projet_due_hour", projetDueHour);
                                database.collection("ProJets").document(projetName)
                                        .set(projetInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Toast.makeText(CreateProjectActivity.this, "ProJet successfully created! (E)", Toast.LENGTH_SHORT).show();
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

                                //Creating a DocumentReference for current projet to make code cleaner
                                DocumentReference projetReference = database.collection("ProJets").document(projetName);

                                //Adding the required boolean value to be able to finish a project in the future
                                Map<String, Boolean> isFinished = new HashMap<>();
                                isFinished.put("projet_is_complete", false);
                                projetReference.set(isFinished, SetOptions.merge());

                                //Creating users & tasks collection inside of the projet document and initializing them with currentUser's data
                                Map<String, DocumentReference> userInit = new HashMap<>();
                                userInit.put("user_reference", database.collection("Users").document(currentUserMail));
                                projetReference.collection("Members").document(currentUserMail).set(userInit, SetOptions.merge());

                                //Adding real name attribute to the user
                                database.collection("Users").document(currentUserMail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        userRealName = documentSnapshot.getString("user_name");
                                        assert userRealName != null;
                                        Log.d("User's real name is:", userRealName);

                                        Map<String, String> userInit2 = new HashMap<>();
                                        userInit2.put("user_name", userRealName);
                                        database.collection("ProJets").document(projetName).collection("Members").document(currentUserMail).set(userInit2, SetOptions.merge());
                                    }
                                });

                                //DocRef for creator of user, to improve code clarity
                                DocumentReference creatorUser = database.collection("Users").document(currentUserMail);

                                //Adding the projet reference and other properties of it to the creator of projet's current projets document
                                Map<String, DocumentReference> userProjetUpdate1 = new HashMap<>();
                                userProjetUpdate1.put("projet_reference", projetReference);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate1, SetOptions.merge());

                                Map<String, String> userProjetUpdate2 = new HashMap<>();
                                userProjetUpdate2.put("projet_name", projetName);
                                userProjetUpdate2.put("projet_desc", projetDesc);
                                userProjetUpdate2.put("projet_due_date", projetDueDate);
                                userProjetUpdate2.put("projet_due_hour", projetDueHour);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate2, SetOptions.merge());
                            }
                        }
                    });

                    //closing the creation page, and removing it from backstack
                    startActivity(new Intent(CreateProjectActivity.this, ProjetMainPageActivity.class));
                    finish();
                }
            }
        });
    }

    // Method For Date Picker to get valid dates from users..
    public void showDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        editTextProjetDueDate.setText(date);
    }

    // Method For Time Picker to get valid times from users..
    public void showTimePickerDialog()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {

        editTextProjetDueHour.setText(hourOfDay + ":" +minute);
    }
}
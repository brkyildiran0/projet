package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProjetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    private EditText editTextProjetDesc;
    private EditText editTextProjetDueDate;
    private EditText editTextProjetDueHour;
    String projetName;
    String currentUserMail;
    Button buttonApplyChanges;
    DocumentReference projetReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_projet);

        //Firebase Initializing
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting the projet name from ProJet page
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //connecting views to code
        buttonApplyChanges = findViewById(R.id.buttonApplyChanges);
        editTextProjetDesc = findViewById(R.id.editProjetDesc);
        editTextProjetDueDate = findViewById(R.id.editProjetDueDate);
        editTextProjetDueHour = findViewById(R.id.editProjetDueHour);

        //Declaring the current projet's reference for code clarity later on
        projetReference = database.collection("ProJets").document(projetName);

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

        //Filling the edit texts beforehand to let user change those values accordingly.
        projetReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                editTextProjetDesc.setText(documentSnapshot.getString("projet_decs"));
                editTextProjetDueDate.setText(documentSnapshot.getString("projet_due_date"));
                editTextProjetDueHour.setText(documentSnapshot.getString("projet_due_hour"));
            }
        });

        //on click listener of Apply Changes button
        buttonApplyChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Checking for empty input case
                if (editTextProjetDesc.getText().toString().equals("") || editTextProjetDueDate.getText().toString().equals("") || editTextProjetDueHour.getText().toString().equals(""))
                {
                    Toast.makeText(EditProjetActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Editing the projet properties according to the new input
                    Map<String, String> projetInfo = new HashMap<>();
                    projetInfo.put("projet_desc", editTextProjetDesc.getText().toString());
                    projetInfo.put("projet_due_date", editTextProjetDueDate.getText().toString());
                    projetInfo.put("projet_due_hour", editTextProjetDueHour.getText().toString());
                    projetReference.set(projetInfo, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Toast.makeText(EditProjetActivity.this, "ProJet successfully edited!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(EditProjetActivity.this, "Error. Can't edit ProJet.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    //Closing the page after edit process is completed
                    Intent complete = new Intent(getApplicationContext(), ProjetMainPageActivity.class);
                    startActivity(complete);
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

package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMemberActivity extends AppCompatActivity
{
    private String projetName;
    private Button buttonAddMember;
    private Button buttonDone;
    private EditText editTextEmail;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //initializing views
        buttonAddMember = findViewById(R.id.buttonAddMembers);
        buttonDone = findViewById(R.id.buttonDone);
        editTextEmail = findViewById(R.id.editTextEmail);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");


        //AddMember button listener, adds members' emails to an ArrayList, which will be sent to Create New ProJet page
        buttonAddMember.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if ( !editTextEmail.getText().toString().equals("") )
                {
                    //DocumentReference of added member for code clarity
                    String inputMailAdress = editTextEmail.getText().toString();
                    DocumentReference addedMember = database.collection("Users").document(inputMailAdress);

                    //Creation of new member and its email init in the projet members collection
                    Map<String, String> map = new HashMap<>();
                    map.put("user_email", editTextEmail.getText().toString());
                    database.collection("ProJets").document(projetName).collection("Members").document(inputMailAdress).set(map, SetOptions.merge());

                    Map<String, String> nameAdder = new HashMap<>();
                    nameAdder.put("user_name", addedMember.get().getResult().getString("user_name"));
                    database.collection("ProJets").document(projetName).collection("Members").document(inputMailAdress).set(nameAdder, SetOptions.merge());

                    Toast.makeText(AddMemberActivity.this, "Member Added!", Toast.LENGTH_LONG).show();
                    editTextEmail.setText("");
                }
                else
                    Toast.makeText(AddMemberActivity.this, "Please enter the e-mail!", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO modify the buttonDone setOnClicker accordingly in the future
    }
}

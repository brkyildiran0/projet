package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs102.projet.MyNotificationClass;
import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AddMemberActivity extends AppCompatActivity
{
    private String projetName;
    private String projetDescription;
    private String projetDueDate;
    private String projetDueHour;
    private String addedUserName;
    private String addedUserMail;
    private Button buttonAddMember;
    private Button buttonDone;
    private EditText editTextEmail;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    MyNotificationClass myNotificationClass;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // MyNotificationClass initialize
        myNotificationClass = new MyNotificationClass();

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Bundle initialize
        Bundle extras = getIntent().getExtras();
        assert extras != null;

        //initializing views
        buttonAddMember = findViewById(R.id.buttonAddMembers);
        buttonDone = findViewById(R.id.buttonDone);
        editTextEmail = findViewById(R.id.editTextEmail);

        projetName = extras.getString("projetName");
        projetDescription = extras.getString("projetDesc");
        projetDueDate = extras.getString("projetDueDate");
        projetDueHour = extras.getString("projetDueHour");


        //AddMember button listener, adds members' directly to ProJet/Members root and Members/"addedMemberName" root
        buttonAddMember.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if ( !editTextEmail.getText().toString().equals("") )
                {
                    //Checking whether such user with given email exists and continuing accordingly.
                    Query myQuery = database.collection("Users").whereEqualTo("user_email", editTextEmail.getText().toString());


                    // Sending notification to new member
                    myNotificationClass.sendNotification(editTextEmail.getText().toString(), "deneme with my class");

                    myQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            if (!queryDocumentSnapshots.isEmpty())
                            {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                                {
                                    //Now we are certain that such user exists, therefore getting needed values.
                                    addedUserName = document.getString("user_name");
                                    addedUserMail = document.getString("user_email");

                                    //ProJet root update
                                    Map<String, String> userInitializer = new HashMap<>();
                                    userInitializer.put("user_name", addedUserName);
                                    database.collection("ProJets").document(projetName).collection("Members").document(addedUserMail).set(userInitializer, SetOptions.merge());

                                    //Users root update
                                    Map<String, String> user = new HashMap<>();
                                    user.put("projet_desc", projetDescription);
                                    user.put("projet_due_date", projetDueDate);
                                    user.put("projet_due_hour", projetDueHour);
                                    user.put("projet_name", projetName);
                                    database.collection("Users").document(addedUserMail).collection("Current ProJets").document(projetName).set(user, SetOptions.merge());

                                    //Inform app and clear the text input
                                    Toast.makeText(AddMemberActivity.this, "Member Added!", Toast.LENGTH_LONG).show();
                                    editTextEmail.setText("");
                                }

                            }
                            else
                            {
                                Toast.makeText(AddMemberActivity.this, "Such user does not exist!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //Empty input case
                else
                    Toast.makeText(AddMemberActivity.this, "Please enter the e-mail!", Toast.LENGTH_SHORT).show();
            }
        });

        //To finish the adding process
        buttonDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }


}

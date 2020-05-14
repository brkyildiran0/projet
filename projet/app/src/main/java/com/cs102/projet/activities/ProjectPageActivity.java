package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProjectPageActivity extends AppCompatActivity
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    private String projetName;
    private ImageButton membersButton;
    private ImageButton tasksButton;
    private ProgressBar progressBar;
    private TextView projetHeader;
    private TextView projetDescription;
    private TextView projetDueDate;
    private TextView projetDueHour;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_page);

        //Getting the clicked ProJet name
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //View initialize
        membersButton = findViewById(R.id.projetMembersButton);
        tasksButton = findViewById(R.id.projetTasksButton);
        progressBar = findViewById(R.id.projetProgressBar);
        projetHeader = findViewById(R.id.projetPageProjetName);
        projetDescription = findViewById(R.id.projetDescription);
        projetDueDate = findViewById(R.id.projetDueDate);
        projetDueHour = findViewById(R.id.projetDueHour);

        //Setting the header of the ProJet page and document reference
        projetHeader.setText(projetName);


        //Getting projet's info from firebase
        database.collection("ProJets").document(projetName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                projetDescription.setText(documentSnapshot.getString("projet_desc"));
                projetDueDate.setText(documentSnapshot.getString("projet_due_date"));
                projetDueHour.setText(documentSnapshot.getString("projet_due_hour"));

            }
        });

        //tasksButton onClick()
        tasksButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentX = new Intent(ProjectPageActivity.this, TaskPageActivity.class);
                intentX.putExtra("projetName", projetName);
                startActivity(intentX);
            }
        });
    }


    //Method for the AppBar Buttons & Icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_new_projet_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addNewMember:
                Intent newMember = new Intent(getApplicationContext(), AddMemberActivity.class);
                newMember.putExtra("projetName", projetName);
                newMember.putExtra("projetDesc", projetDescription.getText().toString());
                newMember.putExtra("projetDueDate", projetDueDate.getText().toString());
                newMember.putExtra("projetDueHour", projetDueHour.getText().toString());
                startActivity(newMember);
                return true;
            case R.id.addNewTask:
                startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
                return true;
            case R.id.leaveProjet:
                database = FirebaseFirestore.getInstance();
                myFirebaseAuth = FirebaseAuth.getInstance();
                currentUser = myFirebaseAuth.getCurrentUser();

                //Removing the ProJet from User's own document
                database.collection("Users").document(currentUser.getEmail()).collection("Current ProJets").document(projetName).delete().
                        addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(ProjectPageActivity.this, "Abandoned the ProJet!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(ProjectPageActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });

                database.collection("ProJets").document(projetName).collection("Members").document(myFirebaseAuth.getCurrentUser().getEmail()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Log.d("Info", "User removed from ProJet/Members root");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.d("Info", "Failed to remove user from ProJet/Members root");
                            }
                        });

                startActivity(new Intent(getApplicationContext(), ProjetMainPageActivity.class));
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

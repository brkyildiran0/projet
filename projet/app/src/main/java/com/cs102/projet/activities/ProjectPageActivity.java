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

import com.cs102.projet.interfaces.GetInformations;
import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProjectPageActivity extends AppCompatActivity
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    private String projetName;
    private ImageButton membersButton;
    private ImageButton tasksButton;
    private ImageButton projetChatbutton;
    private ProgressBar progressBar;
    private TextView projetHeader;
    private TextView projetDescription;
    private TextView projetDueDate;
    private TextView projetDueHour;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projet_page);

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
        projetChatbutton = findViewById(R.id.projetChatButton);
        //progressBar = findViewById(R.id.projetProgressBar);
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

        //membersButton onClick()
        membersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentY = new Intent(ProjectPageActivity.this, MembersPageActivity.class);
                intentY.putExtra("projetName", projetName);
                startActivity(intentY);
            }
        });
        projetChatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentA = new Intent(ProjectPageActivity.this, ProjetGroupChatActivity.class);
                intentA.putExtra("projetName", projetName);
                startActivity(intentA);
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
                Intent newTask = new Intent(getApplicationContext(), AddTaskActivity.class);
                newTask.putExtra("projetName", projetName);
                startActivity(newTask);
                return true;
                
            case R.id.editProjet:
                Intent editProjet = new Intent(getApplicationContext(), EditProjetActivity.class);
                editProjet.putExtra("projetName", projetName);
                startActivity(editProjet);
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

                //If someone leave from the projet, tha tasks that he/she has will be open to new owner.
                Task<QuerySnapshot> query = database.collection("ProJets").document(projetName)
                        .collection("Tasks").whereEqualTo("task_owner", currentUser.getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(DocumentSnapshot doc : task.getResult()){
                                        DocumentReference theDoc = doc.getReference();
                                        theDoc.update("task_owner", "");
                                    }
                                }
                            }
                        });

                startActivity(new Intent(getApplicationContext(), ProjetMainPageActivity.class));
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    // To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    // What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    // While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(final GetInformations getInformations, Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventList = new ArrayList<>();

                            for(DocumentSnapshot doc : task.getResult()) {
                                doc.get("task_owner").toString();
                            }
                            getInformations.useInfo(eventList);
                        } else {
                            Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                        }
                    }
                });
    }
}

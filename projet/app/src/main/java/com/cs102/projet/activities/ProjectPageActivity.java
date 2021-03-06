package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cs102.projet.adapters.ProgressBarDayAdapter;
import com.cs102.projet.adapters.ProgressBarTaskAdapter;
import com.cs102.projet.classes.ProgressBarDay;
import com.cs102.projet.classes.ProgressBarTask;
import com.cs102.projet.interfaces.GetInformations;
import com.cs102.projet.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectPageActivity extends AppCompatActivity
{
    //Global Variables
    String projetName;
    ImageButton membersButton;
    ImageButton tasksButton;
    ImageButton projetChatbutton;
    ImageButton myTasksButton;
    TextView projetHeader;
    TextView projetDescription;
    TextView projetDueDate;
    TextView projetDueHour;
    RecyclerView recyclerView_task;
    RecyclerView recyclerView_day;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    ProgressBarTaskAdapter adapterTask;
    ProgressBarDayAdapter adapterDay;

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
        projetChatbutton = findViewById(R.id.projetChatButton);
        myTasksButton = findViewById(R.id.buttonMyTasks);
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

        //Getting the UNCOMPLETED task amount & and updating it on the ProJet database root by query below
        Query uncompletedTasks = database.collection("ProJets").document(projetName).collection("Tasks").whereEqualTo("task_status", false);
        uncompletedTasks.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            int uncompletedTasksCounter = 0;
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for (QueryDocumentSnapshot eachUncompleteTask : queryDocumentSnapshots)
                {
                    uncompletedTasksCounter++;
                }
                Map<String, Integer> uncompletedTasksProjetAdder = new HashMap<>();
                uncompletedTasksProjetAdder.put("total_uncompleted_tasks", uncompletedTasksCounter);
                database.collection("ProJets").document(projetName).set(uncompletedTasksProjetAdder, SetOptions.merge());
            }
        });

        //Getting the COMPLETED task amount & and updating it on the ProJet database root by query below
        Query completedTasks = database.collection("ProJets").document(projetName).collection("Tasks").whereEqualTo("task_status", true);
        completedTasks.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            int completedTasksCounter = 0;
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for (QueryDocumentSnapshot eachUncompleteTask : queryDocumentSnapshots)
                {
                    completedTasksCounter++;
                }
                Map<String, Integer> completedTasksProjetAdder = new HashMap<>();
                completedTasksProjetAdder.put("total_completed_tasks", completedTasksCounter);
                database.collection("ProJets").document(projetName).set(completedTasksProjetAdder, SetOptions.merge());
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
        myTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentN = new Intent(ProjectPageActivity.this, MyTasksActivity.class);
                intentN.putExtra("projetName", projetName);
                startActivity(intentN);
            }
        });

        setUpRecyclerViewTask();
        setUpRecyclerViewDay();
    }

    /**
     * This method is responsible for live-updating the task progress bar at the ProJet page.
     */
    private void setUpRecyclerViewTask()
    {
        Query query = database.collection("ProJets").whereEqualTo("projet_name", projetName);

        FirestoreRecyclerOptions<ProgressBarTask> options = new FirestoreRecyclerOptions.Builder<ProgressBarTask>()
                .setQuery(query, ProgressBarTask.class).build();
        adapterTask = new ProgressBarTaskAdapter(options, projetName);
        recyclerView_task = findViewById(R.id.recycler_view_task_progress);
        recyclerView_task.setHasFixedSize(true);
        recyclerView_task.setLayoutManager(new LinearLayoutManager(ProjectPageActivity.this));
        recyclerView_task.setAdapter(adapterTask);
    }

    /**
     * This method is responsible for live-updating the left time progress bar at the ProJet page.
     */
    private void setUpRecyclerViewDay()
    {
        Query query = database.collection("ProJets").whereEqualTo("projet_name", projetName);

        FirestoreRecyclerOptions<ProgressBarDay> options = new FirestoreRecyclerOptions.Builder<ProgressBarDay>().setQuery(query, ProgressBarDay.class).build();
        adapterDay = new ProgressBarDayAdapter(options);
        recyclerView_task = findViewById(R.id.recycler_view_day_progress);
        recyclerView_task.setHasFixedSize(true);
        recyclerView_task.setLayoutManager(new LinearLayoutManager(ProjectPageActivity.this));
        recyclerView_task.setAdapter(adapterDay);
    }

    /**
     * Necessary method for real-time(recycleView) view of page to work
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        adapterTask.startListening();
        adapterDay.startListening();
    }

    /**
     * Necessary method for real-time(recycleView) view of page to work
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        adapterTask.stopListening();
        adapterDay.stopListening();
    }

    /**
     * This method is responsible for showing the customized appbar at the top of the page.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_new_projet_menu, menu);

        return true;
    }

    /**
     * This method is responsible for showing the customized appbar at the top of the page as well.
     */
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
                //Handling the abandoning process of a ProJet here.
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

                //If someone leaves the ProJet, the tasks that they held will be added back to the ProJet.
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

    //The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    //To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    //What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    //While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(final GetInformations getInformations, Query query)
    {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()) {
                    List<String> eventList = new ArrayList<>();

                    for(DocumentSnapshot doc : task.getResult())
                    {
                        doc.get("task_owner").toString();
                    }
                    getInformations.useInfo(eventList);
                }
                else
                {
                    Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                }
            }
        });
    }
}

package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cs102.projet.R;
import com.cs102.projet.adapters.CompletedTasksAdapter;
import com.cs102.projet.classes.Task;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CompletedTasksActivity extends AppCompatActivity
{
    //Global Variables
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    String projetName;
    CompletedTasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        //Getting the clicked ProJet name from ProJet page
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        setUpRecyclerView(projetName);
    }

    private void setUpRecyclerView(String projetName)
    {
        //Query to check tasks with true "task_status" attribute (finds completed tasks at the database)
        Query query = database.collection("ProJets").document(projetName).collection("Tasks").whereEqualTo("task_status", true);

        //Uses query above to get the completed tasks
        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>().setQuery(query, Task.class).build();

        //Setting the recyclerView by found completed tasks
        adapter = new CompletedTasksAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_completed_tasks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CompletedTasksActivity.this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Necessary method for real-time(recycleView) view of page to work
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Necessary method for real-time(recycleView) view of page to work
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
}

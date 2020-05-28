package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.cs102.projet.R;
import com.cs102.projet.adapters.CompletedTasksAdapter;
import com.cs102.projet.classes.Task;
import com.cs102.projet.interfaces.GetInformations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompletedTasksActivity extends AppCompatActivity
{
    //Global Variables
    String projetName;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
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

    // The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    // To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    // What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    // While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(final GetInformations getInformations, Query query)
    {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    List<String> eventList = new ArrayList<>();

                    for(DocumentSnapshot doc : task.getResult())
                    {
                        String e = doc.getId();
                        eventList.add(e);
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

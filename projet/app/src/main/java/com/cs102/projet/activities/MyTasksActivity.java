package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cs102.projet.R;
import com.cs102.projet.adapters.MyTasksAdapter;
import com.cs102.projet.classes.Task;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyTasksActivity extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    String projetName;

    private MyTasksAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        //Getting the clicked ProJet name
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
        String currentUserEmail = currentUser.getEmail();

        setUpRecyclerView(projetName, currentUserEmail);
    }

    public void setUpRecyclerView(String projetName, String userEmail){

        Query query = database.collection("ProJets").document(projetName)
                .collection("Tasks").whereEqualTo("task_owner", userEmail)
                .whereEqualTo("task_status", false);

        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class).build();

        adapter = new MyTasksAdapter(options, projetName);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_my_tasks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyTasksActivity.this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

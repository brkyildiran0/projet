package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cs102.projet.R;
import com.cs102.projet.classes.Task;
import com.cs102.projet.adapters.TaskAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TaskPageActivity extends AppCompatActivity
{
    public String projetName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = db.collection("ProJets");

    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        //Getting info from fragment Handling nullPointerException for incoming intent extra
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        setUpRecyclerView(projetName);
    }


    private void setUpRecyclerView(String getProjetName){

        CollectionReference lastItemRef = itemRef.document(getProjetName).collection("Tasks");
        Query query = lastItemRef.whereEqualTo("task_owner", "");

        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class).build();

        adapter = new TaskAdapter(options, projetName);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_task_page);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskPageActivity.this));
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
                startActivity(new Intent(getApplicationContext(), AddMemberActivity.class));
                return true;
            case R.id.addNewTask:
                startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
                return true;
            case R.id.leaveProjet:
                //TODO will be handled later
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

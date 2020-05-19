package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.cs102.projet.R;
import com.cs102.projet.fragments.FragmentCurrentTasks;
import com.cs102.projet.interfaces.GetInformations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;

public class CurrentTasksActivity extends AppCompatActivity {

    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_tasks);

        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
        currentUserEmail = currentUser.getEmail();
        Query query = database.collection("Users").document(currentUserEmail).collection("Current Tasks");

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final List<String> eventList = new ArrayList<>();

                            for (DocumentSnapshot doc : task.getResult()) {
                                DocumentReference docRef = doc.getDocumentReference(doc.getId());
                                final String taskName = doc.getId();
                                //Log.e("taskName", taskName);
                                final String projetName = docRef.getParent().getParent().getId();
                                //Log.e("projetName", projetName);

                                DocumentReference documentref = database.collection("ProJets").document(projetName)
                                        .collection("Tasks").document(taskName);
                                moveData(new GetInformations() {
                                    @Override
                                    public void useInfo(List<String> eventList) {

                                        ft.add(R.id.current_tasks_container, new FragmentCurrentTasks(eventList.get(0), eventList.get(1),
                                                eventList.get(2), Long.valueOf(eventList.get(3))));

                                    }
                                }, documentref, eventList);

                                Log.e("final", eventList.toString());
                            }
                            ft.commit();
                        } else {
                            Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                        }
                    }
                });

    }



    public void moveData(final GetInformations getInformations, final DocumentReference documentref, final List<String> eventList) {
        documentref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String realTaskName = documentSnapshot.getString("task_name");
                String projetNamex = documentSnapshot.getReference().getParent().getParent().getId();
                String taskDueDate = documentSnapshot.getString("task_due_date");
                //Log.e("taskDueDate", taskDueDate);
                String taskPriority = documentSnapshot.getString("task_priority");
                //Log.e("taskPriority", taskPriority);
                eventList.add(realTaskName);
                eventList.add(projetNamex);
                eventList.add(taskDueDate);
                eventList.add(taskPriority);
                Log.e("all list is this: ", eventList.toString());
                getInformations.useInfo(eventList);
                //ft.add(R.id.current_tasks_container, new FragmentCurrentTasks(eventList.get(0), eventList.get(1),
                //        eventList.get(2), Long.valueOf(eventList.get(3))));
                eventList.remove(0);
                eventList.remove(0);
                eventList.remove(0);
                eventList.remove(0);

            }
        });
    }
}


package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.cs102.projet.classes.Member;
import com.cs102.projet.adapters.MemberAdapter;
import com.cs102.projet.R;
import com.cs102.projet.interfaces.GetInformations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MembersPageActivity extends AppCompatActivity
{
    //Global Variables (some are declared from here to recycle view to work properly)
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference projetRef = db.collection("ProJets");
    String projetName;
    MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_page);

        //Getting the clicked ProJet name from previous page
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        setUpRecyclerView();
    }

    private void setUpRecyclerView()
    {
        //Getting the members of the ProJet
        //***Although android studio says that below two lines are redundant, those two assures that no NullPointerException will occur.***
        CollectionReference membersRef = projetRef.document(projetName).collection("Members");
        Query query = membersRef;

        //Uses query above to get the members
        FirestoreRecyclerOptions<Member> options = new FirestoreRecyclerOptions.Builder<Member>().setQuery(query, Member.class).build();

        adapter = new MemberAdapter(options, projetName);

        //Setting the gathered info(member list) to recycleView(page)
        RecyclerView recyclerView = findViewById(R.id.recycler_view_members_page);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MembersPageActivity.this));
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

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
import android.widget.Toast;

import com.cs102.projet.R;
import com.cs102.projet.adapters.NotificationAdapter;
import com.cs102.projet.classes.NotificationPage;
import com.cs102.projet.interfaces.GetInformations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity
{
    //Global Variables
    String currentUserEmail;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //Firebase & Views initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
        currentUserEmail = currentUser.getEmail();
        CollectionReference notificationReference = database.collection("Users").document(currentUserEmail).collection("Notifications");
        setUpRecyclerView(notificationReference);
    }

    public void setUpRecyclerView(CollectionReference notificationReference)
    {
        //Query to get notifications from database
        Query query = notificationReference.whereEqualTo("delete", false);

        //Applying the query above.
        FirestoreRecyclerOptions<NotificationPage> options = new FirestoreRecyclerOptions.Builder<NotificationPage>()
                .setQuery(query, NotificationPage.class).build();

        adapter = new NotificationAdapter(options);

        //Setting the gathered info(notifications) to recycleView(page)
        RecyclerView recyclerView = findViewById(R.id.recycler_view_notifications);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
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

    /**
     * This method is responsible for showing the customized appbar at the top of the page.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);

        return true;
    }

    /**
     * This method is responsible for showing the customized appbar at the top of the page as well.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //TODO: edit the following comments as other pages gets created.
        switch (item.getItemId())
        {
            case R.id.profile_icon_on_toolbar:
                finish();
                Intent profileGit = new Intent(NotificationsActivity.this, ProfilePageActivity.class);
                startActivity(profileGit);
                return true;
            case R.id.notification_icon_on_toolbar:
                //should stay empty...
                return true;
            case R.id.help_button_on_toolbar:
                finish();
                Intent helpeGit = new Intent(NotificationsActivity.this, SlideActivity.class);
                startActivity(helpeGit);
                return true;
            case R.id.setting_button_on_toolbar:
                finish();
                Intent settingseGit = new Intent(NotificationsActivity.this, SettingsActivity.class);
                startActivity(settingseGit);
                return true;
            case R.id.logout_button_on_toolbar:
                Toast.makeText(this, "Please return to the main page to logout!", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

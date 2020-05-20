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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cs102.projet.R;
import com.cs102.projet.adapters.ProJetAdapter;
import com.cs102.projet.classes.ProJet;
import com.cs102.projet.loginpage.LoginActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.onesignal.OneSignal;

public class ProjetMainPageActivity extends AppCompatActivity
{
    String currentUserMail;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    private ProJetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //View & more initialize
        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);

        //Getting current logged in user's mail address
        currentUserMail = currentUser.getEmail();

        //OneSignal, Settings tags for current user via email..
        OneSignal.sendTag("User_Id", currentUserMail);

        //Setting a reference to user's Current Projets folder
        CollectionReference userCurrentProjets = database.collection("Users").document(currentUserMail).collection("Current ProJets");

        setUpRecyclerView(userCurrentProjets);

        //CreateNewProJetButton onClick
        buttonCreateNewProjet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProjetMainPageActivity.this, CreateProjectActivity.class);
                intent.putExtra("currentUserEmail", currentUserMail);
                startActivity(intent);
            }
        });
    }

    public void setUpRecyclerView(CollectionReference collectRef){

        Query query = collectRef;
        FirestoreRecyclerOptions<ProJet> options = new FirestoreRecyclerOptions.Builder<ProJet>()
                .setQuery(query, ProJet.class).build();

        adapter = new ProJetAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_main_page);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjetMainPageActivity.this));
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
        inflater.inflate(R.menu.appbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //TODO: edit the following comments as other pages gets created.
        switch (item.getItemId())
        {
            case R.id.profile_icon_on_toolbar:
                Intent goToProfile = new Intent(ProjetMainPageActivity.this, ProfilePageActivity.class);
                startActivity(goToProfile);
                return true;
            case R.id.notification_icon_on_toolbar:
                Intent goToNotifications = new Intent(ProjetMainPageActivity.this, NotificationsActivity.class);
                startActivity(goToNotifications);
                return true;
            case R.id.help_button_on_toolbar:
                Intent goToHelp = new Intent(ProjetMainPageActivity.this, SlideActivity.class);
                startActivity(goToHelp);
                return true;
            case R.id.setting_button_on_toolbar:
                Intent goToSettings = new Intent(ProjetMainPageActivity.this, SettingsActivity.class);
                startActivity(goToSettings);

                return true;
            case R.id.logout_button_on_toolbar:
                myFirebaseAuth.signOut();
                Toast.makeText(this, "Signed out.", Toast.LENGTH_SHORT).show();
                Intent intentLogout = new Intent(ProjetMainPageActivity.this, LoginActivity.class);
                startActivity(intentLogout);
                finishAffinity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This assigns the back button to work as home button, so the program wont ask for
     * login again, when the program is closed by pressing back button on the main page.
     */
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}

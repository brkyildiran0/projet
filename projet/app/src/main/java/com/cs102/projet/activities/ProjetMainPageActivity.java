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
        ImageButton buttonRecycle = findViewById(R.id.buttonRecycle);

        //Getting current logged in user's mail address
        currentUserMail = currentUser.getEmail();

        //OneSignal, Settings tags for current user via email..
        OneSignal.sendTag("User_Id", currentUserMail);

        //Setting a reference to user's Current Projets folder
        CollectionReference userCurrentProjets = database.collection("Users").document(currentUserMail).collection("Current ProJets");

        setUpRecyclerView(userCurrentProjets);
        //Checking user's Current ProJets folder, if there are any adding them to a List
        /*userCurrentProjets.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        //Initializing the fragment managers here to display each projet user owns
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        //Processing the projet info and inserting the info to fragment and displaying it
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                //Getting the projet refence to use it at getting projet duedate
                                DocumentReference projetReference = database.collection("ProJets").document(document.getId());
                                ft.add(R.id.fragmentContainer, new FragmentMainPageProject(document.getId(), document.getString("projet_due_date")));
                            }
                        }
                        else
                        {
                            Log.e("Error", "Could not retrieve ProJet info.");
                        }

                        ft.commit();
                    }
                });
        */
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

        //Recycle onClick
        buttonRecycle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), ProjetMainPageActivity.class));
                finish();
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
                //write down lines to switch to the help page
                //...
                //...
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

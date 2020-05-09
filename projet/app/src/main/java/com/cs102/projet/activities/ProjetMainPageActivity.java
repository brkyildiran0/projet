package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs102.projet.R;
import com.cs102.projet.fragments.FragmentMainPageProject;
import com.cs102.projet.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProjetMainPageActivity extends AppCompatActivity
{
    String currentUserMail;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //View & more initialize
        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);

        //Getting current logged in user's mail address
        currentUserMail = currentUser.getEmail();

        //TODO Checking current user's projets and if there are any, adding them to page using fragments

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.fragmentContainer, new FragmentMainPageProject("Burak", "15/04/2020"));

        ft.commit();

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
                //write down lines to switch to the settings page
                //...
                //...
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

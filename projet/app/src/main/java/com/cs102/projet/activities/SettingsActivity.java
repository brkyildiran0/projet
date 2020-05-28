package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity
{
    //Global Variables
    String currentUserMail;
    EditText newNameInput;
    ImageButton buttonChangeName;
    ImageButton enableDarkMode;
    ImageButton disableDarkMode;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting current logged in user's mail address
        currentUserMail = currentUser.getEmail();

        //View initialize
        newNameInput = findViewById(R.id.editTextNameInput);
        buttonChangeName = findViewById(R.id.imageButtonChangeName);
        enableDarkMode = findViewById(R.id.enableDarkMode);
        disableDarkMode = findViewById(R.id.disableDarkMode);

        //Handling user's name change request
        buttonChangeName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Handling empty input
                if (!newNameInput.getText().toString().equals(""))
                {
                    String newName = newNameInput.getText().toString();

                    Map<String, String > userNewName = new HashMap<>();
                    userNewName.put("user_name", newName);
                    database.collection("Users").document(currentUserMail).set(userNewName, SetOptions.merge());

                    newNameInput.setText("");
                    Toast.makeText(SettingsActivity.this, "Name changed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Please enter a name first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        enableDarkMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Updating user's preference at database
                Map<String, Boolean> userPrefUpdate = new HashMap<>();
                userPrefUpdate.put("user_darkmode_preference", true);
                database.collection("Users").document(currentUserMail).set(userPrefUpdate,SetOptions.merge());

                //Updating the app to darkmode immediately
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                startActivity(new Intent(getApplicationContext(), ProjetMainPageActivity.class));
                finish();
            }
        });

        disableDarkMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Updating user's preference at database
                Map<String, Boolean> userPrefUpdate = new HashMap<>();
                userPrefUpdate.put("user_darkmode_preference", false);
                database.collection("Users").document(currentUserMail).set(userPrefUpdate,SetOptions.merge());

                //Updating the app to darkmode immediately
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                startActivity(new Intent(getApplicationContext(), ProjetMainPageActivity.class));
                finish();
            }
        });
    }
}
package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cs102.projet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity
{
    String currentUserMail;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    EditText newNameInput;
    ImageButton buttonChangeName;
    ImageButton enableDarkMode;
    ImageButton disableDarkMode;

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



    }
}
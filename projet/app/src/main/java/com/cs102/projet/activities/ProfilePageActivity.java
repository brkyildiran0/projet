package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePageActivity extends AppCompatActivity
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    TextView userMail;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //View initialize
        userMail = findViewById(R.id.textMailAdress);
        userName = findViewById(R.id.textRealNameSurname);

        //Setting the Name & Email of the current user
        assert currentUser != null;
        userMail.setText(currentUser.getEmail());
        database.collection("Users").document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                userName.setText(documentSnapshot.getString("user_name"));
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
                //should stay as empty
                return true;
            case R.id.notification_icon_on_toolbar:
                finish();
                Intent goToNotifications = new Intent(ProfilePageActivity.this, NotificationsActivity.class);
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
                //write down lines to logout the user
                //...
                //...
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

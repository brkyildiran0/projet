package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs102.projet.fragments.FragmentMainPageProject;
import com.cs102.projet.loginpage.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity
{
    FirebaseAuth myFirebaseAuth;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //Declaring the current user
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            currentUser= (String) bundle.get("currentUser");
        }

        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);
        myFirebaseAuth = FirebaseAuth.getInstance();

        // Checks whether the user logged in or not.. if not logged in sends users to login page..
        if (myFirebaseAuth.getCurrentUser() == null)
        {
            // if User not logged go to login activity
            Intent intent = new Intent(MainPageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        buttonCreateNewProjet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainPageActivity.this, CreateProjectActivity.class));
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMainPageProject deneme1 = new FragmentMainPageProject("DG", "07/05/2020");
        FragmentMainPageProject deneme2 = new FragmentMainPageProject("Yaptım mı yoksa?", "19/05/2020");

        ft.add(R.id.fragmentContainer, deneme1);
        ft.add(R.id.fragmentContainer, deneme2);
        ft.add(R.id.fragmentContainer, new FragmentMainPageProject("Burak", "15/04/2020"));

        ft.commit();
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
                Intent goToProfile = new Intent(MainPageActivity.this, ProfilePageActivity.class);
                startActivity(goToProfile);
                return true;
            case R.id.notification_icon_on_toolbar:
                Intent goToNotifications = new Intent(MainPageActivity.this, NotificationsActivity.class);
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

                Intent intentLogout = new Intent(MainPageActivity.this, LoginActivity.class);
                startActivity(intentLogout);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

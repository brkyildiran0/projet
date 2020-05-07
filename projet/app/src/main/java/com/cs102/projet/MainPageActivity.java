package com.cs102.projet;

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

import com.cs102.projet.fragments.FragmentMainPageProject;

public class MainPageActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);

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


        // Will be deleted ***********************

        //buttonGecici = findViewById(R.id.buttonGecici);
        //buttonGecici.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
            //    Intent deneme = new Intent(MainPageActivity.this, ProjectPageActivity.class);
            //    startActivity(deneme);
           // }
       ////goToDeniz = findViewById(R.id.goToDeniz);
        //goToDeniz.setOnClickListener(new View.OnClickListener()
       // {
        //    @Override
        //    public void onClick(View v)
        //    {
         //       startActivity(new Intent(MainPageActivity.this, CreateProjectActivity.class));
       //     }
       // });

        // Will be deleted ***********************
    }

    //TODO: onCreateOptionsMenu is the method for the AppBar(Toolbar), it will be added to the required pages on followings days as they are produced.
    //Method for the AppBar Buttons & Icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);

        return true;
    }

    //TODO: onOptionsItemSelected is the method for the AppBar buttons' onClick methods, it will be added to the required pages on followings days as they are produced.
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
                //write down lines to logout the user
                //...
                //...
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

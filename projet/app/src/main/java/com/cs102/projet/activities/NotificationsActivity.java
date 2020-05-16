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

import com.cs102.projet.R;
import com.cs102.projet.fragments.FragmentNotifications;

public class NotificationsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //adding notifications fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.notificationsFragmentContainer, new FragmentNotifications("finish", "CS 102 Projesi"));
        ft.add(R.id.notificationsFragmentContainer, new FragmentNotifications("finish", "Ahmet'in Doğum Günü"));
        ft.add(R.id.notificationsFragmentContainer, new FragmentNotifications("remain", "CS 102 Projesi", "9"));
        ft.add(R.id.notificationsFragmentContainer, new FragmentNotifications("added", "Bitirelim şu işi!"));
        ft.add(R.id.notificationsFragmentContainer, new FragmentNotifications("remain", "Coding", "1"));

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
                finish();
                Intent profileGit = new Intent(NotificationsActivity.this, ProfilePageActivity.class);
                startActivity(profileGit);
                return true;
            case R.id.notification_icon_on_toolbar:
                //should stay empty...
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

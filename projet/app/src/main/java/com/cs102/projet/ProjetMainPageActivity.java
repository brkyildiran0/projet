package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProjetMainPageActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projet_main_page);
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
                //write down lines to switch to the profile page
                //...
                //...
                return true;
            case R.id.notification_icon_on_toolbar:
                //write down lines to switch to the notifications page/pop-up
                //...
                //...
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

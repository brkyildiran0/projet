package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProjectPageActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_page);
    }


    //Method for the AppBar Buttons & Icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_new_projet_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //TODO: edit the following comments as other pages gets created.
        switch (item.getItemId())
        {
            case R.id.addNewMember:
                startActivity(new Intent(getApplicationContext(), AddMemberActivity.class));
                return true;
            case R.id.addNewTask:
                startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
                return true;
            case R.id.leaveProjet:
                //TODO will be handled later
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

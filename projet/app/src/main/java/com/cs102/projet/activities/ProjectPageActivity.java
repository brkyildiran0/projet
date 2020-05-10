package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cs102.projet.R;

public class ProjectPageActivity extends AppCompatActivity
{
    private String projetName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_page);

        //Getting info from fragment Handling nullPointerException for incoming intent extra
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");
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

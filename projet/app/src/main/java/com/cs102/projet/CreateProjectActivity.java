package com.cs102.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cs102.projet.fragments.MembersFragment;
import com.cs102.projet.fragments.TasksFragment;

public class CreateProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // adding fragments to project to create activity page
        ft.add(R.id.MembersLayout, new MembersFragment());
        //ft.add(R.id.TasksLayout, new TasksFragment());

        // fragmentları çalıştırma
        ft.commit();
    }
}

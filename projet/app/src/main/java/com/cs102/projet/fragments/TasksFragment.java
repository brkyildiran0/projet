package com.cs102.projet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.AddTaskActivity;
import com.cs102.projet.R;

public class TasksFragment extends Fragment {

    private Button AddTaskButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.members_fragment,container,false);

        // Adding Buttons to Fragment
        AddTaskButton = rootView.findViewById(R.id.AddTaskButton);

        // Setting Button Activity

        // TO DO
        AddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gecIntent = new Intent(getContext(), AddTaskActivity.class);
                startActivity(gecIntent);
            }
        });


        return rootView;
    }
}
package com.cs102.projet.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.cs102.projet.R;

public class FragmentCurrentTasks extends Fragment
{
    //Properties
    private TextView taskName;
    private TextView projetName;
    private TextView taskDueDate;
    private View dividerPriority;
    private String task_name;
    private String projet_name;
    private String task_due_date;
    private Long task_priority_fragment;

    //Constructors
    public FragmentCurrentTasks()
    {
    }

    public FragmentCurrentTasks(String task_name, String projet_name, String task_due_date, Long task_priority_fragment)
    {
        this.task_name = task_name;
        this.projet_name = projet_name;
        this.task_due_date = task_due_date;
        this.task_priority_fragment = task_priority_fragment;
    }

    //Methods
    public String getTask_name() {
        return task_name;
    }

    public String getProjet_name() {
        return projet_name;
    }

    public String getTask_due_date() {
        return task_due_date;
    }

    public Long getTask_priority_fragment() {
        return task_priority_fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_current_tasks, container, false);

        taskDueDate = rootView.findViewById(R.id.taskDueDate_fragment);
        taskName = rootView.findViewById(R.id.taskName_fragment);
        projetName = rootView.findViewById(R.id.projetName_fragment);
        dividerPriority = rootView.findViewById(R.id.dividerPriority_fragment);

        taskName.setText(this.task_name);
        taskDueDate.setText(task_due_date);
        projetName.setText(projet_name);

        //Handling the different colors for properties with different properties
        if(this.task_priority_fragment == 1)
        {
            dividerPriority.setBackgroundColor(Color.GREEN);
        }
        else if(this.task_priority_fragment == 2)
        {
            dividerPriority.setBackgroundColor(Color.YELLOW);
        }
        else
        {
            dividerPriority.setBackgroundColor(Color.RED);
        }

        return rootView;
    }
}

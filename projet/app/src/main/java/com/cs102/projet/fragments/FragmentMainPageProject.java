package com.cs102.projet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.R;
import com.cs102.projet.activities.ProjectPageActivity;

public class FragmentMainPageProject extends Fragment {
    private String projetName;
    private String projetDueDate;
    private TextView unchangedNameText;
    private TextView unchangedDueDateText;

    public FragmentMainPageProject()
    {
    }

    public FragmentMainPageProject(String projetName, String projetDueDate)
    {
        this.projetName = projetName;
        this.projetDueDate = projetDueDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.projet_info_item, container,false);
        unchangedNameText = rootView.findViewById(R.id.projetName_fr);
        unchangedDueDateText = rootView.findViewById(R.id.setDate_fr);
        unchangedNameText.setText(this.projetName);
        unchangedDueDateText.setText(this.projetDueDate);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gecIntent = new Intent(getContext(), ProjectPageActivity.class);
                gecIntent.putExtra("projetName", unchangedNameText.getText().toString());
                startActivity(gecIntent);
            }
        });

        return rootView;
    }
}
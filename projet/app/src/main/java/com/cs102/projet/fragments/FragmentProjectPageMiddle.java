package com.cs102.projet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.R;

public class FragmentProjectPageMiddle extends Fragment {

    private TextView head1;
    private TextView percentage_pp;
    private TextView remainDate_pp;

    private Button leftTaskButton;
    private Button completedTaskButton;

    private ProgressBar progressBar_pp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentMidSide = inflater.inflate(R.layout.fragment_project_page_middle, container, false);

        progressBar_pp = fragmentMidSide.findViewById(R.id.progressBar_pp);
        head1 = fragmentMidSide.findViewById(R.id.head1);
        percentage_pp = fragmentMidSide.findViewById(R.id.percentage_pp);
        remainDate_pp = fragmentMidSide.findViewById(R.id.remainDate_pp);
        leftTaskButton = fragmentMidSide.findViewById(R.id.leftTaskButton);
        completedTaskButton = fragmentMidSide.findViewById(R.id.completedTaskButton);

        percentage_pp.setText("%" + progressBar_pp.getProgress());
        remainDate_pp.setText("xx days remain!");

        //TODO give leftTaskButton missions
        /*leftTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //TODO give completedTaskButton mission
        completedTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        return fragmentMidSide;

    }
}

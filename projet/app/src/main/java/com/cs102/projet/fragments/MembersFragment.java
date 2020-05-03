package com.cs102.projet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.R;

public class MembersFragment extends Fragment {

    // Buttons on Members Fragment
    private Button AddMember;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.members_fragment,container,false);

        // Adding Buttons to Fragment
        AddMember = rootView.findViewById(R.id.AddMember);

        // Setting Button Activity

        // TO DO
        AddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return rootView;
    }
}

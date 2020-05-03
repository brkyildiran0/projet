package com.cs102.projet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.R;

public class FragmentProjectPageTop extends Fragment {

    private Button chatButton_pp;
    private TextView projetName_pp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentTopSide = inflater.inflate(R.layout.fragment_project_page_top, container, false);
        chatButton_pp = fragmentTopSide.findViewById(R.id.chatButton_pp);
        projetName_pp = fragmentTopSide.findViewById(R.id.projetName_pp);

        // TODO: Make button navigate user to chat bot! Button Click Listener!
        chatButton_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return fragmentTopSide;
    }
}

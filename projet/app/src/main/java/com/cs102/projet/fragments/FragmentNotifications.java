package com.cs102.projet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.R;

public class FragmentNotifications extends Fragment {

    private String notificationType;
    private String projectName;
    private String remainDate;
    private TextView notificationMessage;
    private TextView notificationProjectName;
    private ImageButton buttonDelete;


    public FragmentNotifications() {
    }

    public FragmentNotifications(String notificationType, String projectName) {
        this.notificationType = notificationType;
        this.projectName = projectName;
    }

    public FragmentNotifications(String notificationType, String projectName, String remainDate) {
        this.notificationType = notificationType;
        this.projectName = projectName;
        this.remainDate = remainDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationMessage = rootView.findViewById(R.id.notificationMessage);
        notificationProjectName = rootView.findViewById(R.id.notificationProjectName);
        notificationProjectName.setText(this.projectName);
        buttonDelete = rootView.findViewById(R.id.buttonDelete);

        //Delete Button Action Start
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Bildirim Silindi", Toast.LENGTH_SHORT);
            }
        });
        // Delete Button Action Completed

        if (this.notificationType == "finish")
        {
            notificationMessage.setText(projectName + " is finished! Congratulations!");
        }
        else if (this.notificationType == "remain")
        {
            notificationMessage.setText("There is "+ this.remainDate + " days to due date of the project '"+ projectName+"'. Be careful!");
        }
        else if (this.notificationType == "added")
        {
            notificationMessage.setText("You are added to new project called '"+ projectName + "'. Come and meet your group!");
        }
        return rootView;
    }
}

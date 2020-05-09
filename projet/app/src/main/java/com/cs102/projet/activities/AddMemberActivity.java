package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity
{
    private Button buttonAddMember;
    private Button buttonDone;
    private EditText editTextEmail;
    ArrayList<String> userEmails;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        //initializing views
        buttonAddMember = findViewById(R.id.buttonAddMembers);
        buttonDone = findViewById(R.id.buttonDone);
        editTextEmail = findViewById(R.id.editTextEmail);
        userEmails = new ArrayList<>();


        //AddMember button listener, adds members' emails to an ArrayList, which will be sent to Create New ProJet page
        buttonAddMember.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if ( !editTextEmail.getText().toString().equals("") )
                {
                    userEmails.add(editTextEmail.getText().toString());
                    Toast.makeText(AddMemberActivity.this, "Member Added!", Toast.LENGTH_SHORT).show();
                    editTextEmail.setText("");
                }
                else
                    Toast.makeText(AddMemberActivity.this, "Please enter the e-mail!", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO modify the buttonDone setOnClicker accordingly in the future
    }
}

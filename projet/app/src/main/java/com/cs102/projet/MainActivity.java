package com.cs102.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Button signUpButton, loginButton;
    EditText userNameText;
    EditText passwordText;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // database...
        databaseHelper = new DatabaseHelper(this);

        //Button's id..
        signUpButton = (Button)findViewById(R.id.signUpButton);
        loginButton = (Button)findViewById(R.id.loginButton);

        // EditText's id...
        userNameText = (EditText)findViewById(R.id.userName) ;
        passwordText = (EditText)findViewById(R.id.password) ;


        // Click Listener For Buttons...
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                if (databaseHelper.isLoginValid(userName, password)){
                    Intent intent = new Intent(MainActivity.this, ProjetMainPageActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this,"login successful",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(MainActivity.this,"Invalid", Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                goNextPage();

            }
        });
    }
    private void goNextPage(){
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}

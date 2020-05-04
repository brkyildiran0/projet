package com.cs102.projet.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;

public class RegisterActivity extends AppCompatActivity {

    Button completedButton, createUser;
    EditText confirmText, emailInput, userNameInput, passwordInput, passwordCheck;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Database..
        databaseHelper = new DatabaseHelper(this);

        // Button's Id...
        completedButton = (Button)findViewById(R.id.completedButton);
        createUser = (Button)findViewById(R.id.createUser);

        // EditText's Id...
        confirmText = (EditText)findViewById(R.id.confirmationCode);
        userNameInput = (EditText)findViewById(R.id.userNameInput);
        emailInput = (EditText)findViewById(R.id.emailInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        passwordCheck = (EditText)findViewById(R.id.passwordCheck);



        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this,"confirmation code has been send", Toast.LENGTH_SHORT).show();
            }
        });

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameValue = userNameInput.getText().toString().trim();
                String emailValue = emailInput.getText().toString();
                String passwordValue = passwordInput.getText().toString();
                String passwordCheckValue = passwordCheck.getText().toString();

                if (userNameValue.length() > 1){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("username",userNameValue);
                    contentValues.put("email",emailValue);
                    contentValues.put("password", passwordValue);

                    databaseHelper.createUser(contentValues);
                    goLoginPage();
                    Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(RegisterActivity.this, "Invalid", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void goLoginPage(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

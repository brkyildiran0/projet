package com.cs102.projet.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity
{
    private Button resetPass;
    private EditText editTextEmail;

    FirebaseAuth myFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //initialize
        resetPass = findViewById(R.id.buttonSend);
        editTextEmail = findViewById(R.id.editTextEmail);

        // Firebase Auth..
        myFirebaseAuth = FirebaseAuth.getInstance();

        // Forget PassWord Button..
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editTextEmail.getText().toString().equals(""))
                {
                    String emailOut = editTextEmail.getText().toString().trim();

                    myFirebaseAuth.sendPasswordResetEmail(emailOut).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Check your mail", Toast.LENGTH_LONG).show();
                            } else
                            {
                                Toast.makeText(getApplicationContext(), "Fail to send you email!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    finish();
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                }
                else
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter your mail address.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}

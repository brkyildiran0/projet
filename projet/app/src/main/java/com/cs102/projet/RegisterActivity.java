package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth myFirebaseAuth;

    EditText userNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordCheck;

    Button createUserButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //EditTexts's id..
        userNameInput = (EditText)findViewById(R.id.userNameInput);
        emailInput = (EditText)findViewById(R.id.emailInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        passwordCheck = (EditText)findViewById(R.id.passwordCheck);

        //Buttons's id..
        createUserButton = (Button)findViewById(R.id.createUser);

        //Firebase Auth...

        myFirebaseAuth = FirebaseAuth.getInstance();

        // Start to write ClickListeners...

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameOut = userNameInput.getText().toString().trim();
                String emailOut = emailInput.getText().toString().trim();
                String passwordOut = passwordInput.getText().toString().trim();
                String passwordCheckOut = passwordCheck.getText().toString().trim();

                if (emailOut.isEmpty()){
                    emailInput.setError("Please enter email");
                    emailInput.requestFocus();
                }
                else if (passwordOut.isEmpty()){
                    passwordInput.setError("Please enter password");
                    passwordInput.requestFocus();
                }
//                else if (emailOut.isEmpty() && passwordOut.isEmpty()){
//                    emailInput.setError("Please enter email");
//                    emailInput.requestFocus();
//                    passwordInput.setError("Please enter password");
//                    passwordInput.requestFocus();
//                }
                else {

                    //Firebase part..

                    myFirebaseAuth.createUserWithEmailAndPassword(emailOut, passwordOut).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                // ----------



                                // ----------


                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this,"Register successful", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this,"Register unsuccessful, try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });



    }
}

package com.cs102.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfilePageActivity extends AppCompatActivity
{
    private Button firebaseTryout;
    private EditText inputToFirebase;
    private String inputString = "";
    private TextView receivedData;
    private Button recieveData;

    FirebaseFirestore objectFirebaseFirestore;
    DocumentReference objectDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        //Components initializing
        firebaseTryout = findViewById(R.id.firebaseTryout);
        inputToFirebase = findViewById(R.id.inputToFirebase);
        receivedData = findViewById(R.id.recievedData);
        recieveData = findViewById(R.id.buttonRecieve);

        //Firebase Initializing
        try
        {
            objectFirebaseFirestore = FirebaseFirestore.getInstance();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //input sender button on click listener
        firebaseTryout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputString = inputToFirebase.getText().toString();

                addValuesToFirebase(firebaseTryout);
            }
        });

        //input reciever button on click listener
        recieveData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getValuesFromFirebase(recieveData);
            }
        });


    }

    /**
     * This method is the database receiver method.
     */
    public void getValuesFromFirebase(View view)
    {
        try
        {
            objectDocumentReference = objectFirebaseFirestore.collection("users").document("Burak Yıldıran");
            objectDocumentReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    String myInput = documentSnapshot.getString("myInput");
                    receivedData.setText(myInput);
                }
            })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ProfilePageActivity.this, "Can't get the wanted part...", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Cannot Get The Info!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is the database adder method.
     * @param view
     */
    public void addValuesToFirebase(View view)
    {
        try
        {
            Map<String, String> objectMap = new HashMap<>();
            objectMap.put("myInput", inputString);

            objectFirebaseFirestore.collection("users")
                    .document("Burak Yıldıran")
                    .set(objectMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(ProfilePageActivity.this, "Data stored!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ProfilePageActivity.this, "Failure.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }











    //TODO: onCreateOptionsMenu is the method for the AppBar(Toolbar), it will be added to the required pages on followings days as they are produced.
    //Method for the AppBar Buttons & Icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);

        return true;
    }

    //TODO: onOptionsItemSelected is the method for the AppBar buttons' onClick methods, it will be added to the required pages on followings days as they are produced.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //TODO: edit the following comments as other pages gets created.
        switch (item.getItemId())
        {
            case R.id.profile_icon_on_toolbar:
                //should stay as empty
                return true;
            case R.id.notification_icon_on_toolbar:
                Intent goToNotifications = new Intent(ProfilePageActivity.this, NotificationsActivity.class);
                startActivity(goToNotifications);
                return true;
            case R.id.help_button_on_toolbar:
                //write down lines to switch to the help page
                //...
                //...
                return true;
            case R.id.setting_button_on_toolbar:
                //write down lines to switch to the settings page
                //...
                //...
                return true;
            case R.id.logout_button_on_toolbar:
                //write down lines to logout the user
                //...
                //...
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

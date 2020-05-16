package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs102.projet.GetInformations;
import com.cs102.projet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ChatActivity extends AppCompatActivity {

    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    private String projetName;

    private Button buttonSend;

    private EditText editTextMessageContent;
    private EditText editTextMessageTitle;

    private String currentUserMail;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting the clicked ProJet name
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");


        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //Connect views
        buttonSend = findViewById(R.id.buttonSend);
        editTextMessageContent = findViewById(R.id.editTextMessageContent);
        editTextMessageTitle = findViewById(R.id.editTextMessageTitle);


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentdate = Calendar.getInstance().getTime();
                Log.e("date", currentdate.toString());
                //these string will be sent to server
                final String messageTitle = editTextMessageTitle.getText().toString();
                String messageContent = editTextMessageContent.getText().toString();
                String messageSender = currentUserMail;
                Timestamp messageDate = new Timestamp(currentdate);

                //TODO : ADD date and time picker **** ADD MESSAGE SENDER

                //checks whether user filled all the required info or not
                if (messageTitle.equals("") || messageContent.equals(""))
                {
                    Toast.makeText(ChatActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else {
                    final Map<String, String> messageInfo = new HashMap<>();
                    messageInfo.put("message", messageContent);
                    messageInfo.put("coming_from", currentUserMail);


                    /*Query query = database.collection("Users").whereEqualTo("user_email", currentUserMail);
                    moveData(new GetInformations() {
                        @Override
                        public void useInfo(List<String> eventList) {
                            String userName = "";
                            for ( int p = 0; p < eventList.size(); p++){
                                userName = userName + eventList.get(p) + "\n";
                            }
                            Log.e("username:", userName);
                            messageInfo.put("coming_from", userName);
                        }
                    }, query);*/

                    //To add message and create message file with title on server.
                    database.collection("ProJets").document(projetName).collection("Chat")
                            .document(messageTitle).set(messageInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChatActivity.this, "Message successfully sent!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ChatActivity.this, "Error. Can't sent Message.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //To add date !!
                    final Map<String, Timestamp> messeageInfo2 = new HashMap<>();
                    messeageInfo2.put("time", messageDate);
                    final DocumentReference messageReference = database.collection("ProJets")
                            .document(projetName).collection("Chat").document(messageTitle);
                    messageReference.set(messeageInfo2, SetOptions.merge());


                    //Adding real name attribute to the user
                    /*database.collection("Users").document(currentUserMail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot)
                        {
                            currentUserName = documentSnapshot.getString("user_name");
                            assert currentUserName != null;
                            Log.e("User's real name is:", currentUserName);

                            Map<String, String> messageSender = new HashMap<>();
                            messageSender.put("coming_from", currentUserName);

                            //TODO There can be a mistake !! about message REference.
                            messageReference.set(messageSender, SetOptions.merge());
                            messageSender.clear();
                        }
                    });*/
                }
                /*Intent intentN = new Intent(ChatActivity.this, ProjectPageActivity.class);
                intentN.putExtra("projetName", projetName);
                startActivity(intentN);
                finish();*/
            }
        });
    }

    // The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    // To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    // What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    // While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(final GetInformations getInformations, Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventList = new ArrayList<>();

                            for(DocumentSnapshot doc : task.getResult()) {
                                String e = doc.get("task_name").toString();
                                eventList.add(e);
                            }
                            getInformations.useInfo(eventList);
                        } else {
                            Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                        }
                    }
                });
    }
}

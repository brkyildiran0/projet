package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cs102.projet.classes.Message;
import com.cs102.projet.adapters.MessageAdapter;
import com.cs102.projet.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProjetGroupChatActivity extends AppCompatActivity
{

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    String projetName;
    TextView groupChatname;

    private EditText editTextMessageContent;
    private ImageButton buttonSend;

    private String currentUserMail;
    private String currentUserName;

    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projet_group_chat);

        //Getting the clicked ProJet name
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        groupChatname = findViewById(R.id.textView_groupchat);
        groupChatname.setText(projetName + " Group Chat");

        setUpRecyclerView(projetName);

        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //Connect views
        buttonSend = findViewById(R.id.imageButtonSend_chat);
        editTextMessageContent = findViewById(R.id.editTextMessage_chat);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentdate = Calendar.getInstance().getTime();
                Log.e("date", currentdate.toString());
                //these string will be sent to server
                String messageContent = editTextMessageContent.getText().toString();
                editTextMessageContent.setText("");
                String messageSender = currentUserMail;
                Timestamp messageDate = new Timestamp(currentdate);

                //TODO : ADD date and time picker **** ADD MESSAGE SENDER

                //checks whether user filled all the required info or not
                if (messageContent.equals(""))
                {
                    Toast.makeText(ProjetGroupChatActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
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
                            .document(currentdate.toString()).set(messageInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProjetGroupChatActivity.this, "Message successfully sent!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ProjetGroupChatActivity.this, "Error. Can't sent Message.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //To add date !!
                    final Map<String, Timestamp> messeageInfo2 = new HashMap<>();
                    messeageInfo2.put("time", messageDate);
                    final DocumentReference messageReference = database.collection("ProJets")
                            .document(projetName).collection("Chat").document(currentdate.toString());
                    messageReference.set(messeageInfo2, SetOptions.merge());
                }
            }
        });

    }

    private void setUpRecyclerView(String projetName){
        Query query = database.collection("ProJets").document(projetName).collection("Chat")
                .orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        adapter = new MessageAdapter(options, projetName);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjetGroupChatActivity.this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

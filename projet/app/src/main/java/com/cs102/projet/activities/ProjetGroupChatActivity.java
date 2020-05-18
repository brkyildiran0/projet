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

import com.cs102.projet.MyNotificationClass;
import com.cs102.projet.classes.Message;
import com.cs102.projet.adapters.MessageAdapter;
import com.cs102.projet.R;
import com.cs102.projet.interfaces.GetInformations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    private MyNotificationClass myNotification;

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

        //Notification init
        myNotification = new MyNotificationClass();

        //setUpRecyclerView(projetName);
        Query query = database.collection("ProJets").document(projetName).collection("Chat")
                .orderBy("time", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        adapter = new MessageAdapter(options, projetName);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager xx = new LinearLayoutManager(ProjetGroupChatActivity.this);
        recyclerView.setLayoutManager(xx);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            int friendlyMessageCount = adapter.getItemCount();
            int lastVisiblePosition =
                    xx.findLastCompletelyVisibleItemPosition();
            // If the recycler view is initially being loaded or the
            // user is at the bottom of the list, scroll to the bottom
            // of the list to show the newly added message.
            if (lastVisiblePosition == -1 ||
                    (positionStart >= (friendlyMessageCount - 1) &&
                            lastVisiblePosition == (positionStart - 1))) {
                recyclerView.scrollToPosition(positionStart);
            }
        }
    });

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
                final String messageContent = editTextMessageContent.getText().toString();
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

                // To find projet members collection and get the e-mails.
                // moveData function is used for find e-mails
                // Function "useInfo" which is a part of" moveData" enables us to use these mails to send notification to all members.
                Query queryEmail = database.collection("ProJets").document(projetName).collection("Members");
                moveData(new GetInformations() {
                    @Override
                    public void useInfo(List<String> eventList) {
                        for(int h = 0; h < eventList.size(); h++){

                            if(!eventList.get(h).equals(currentUserMail)) {
                                myNotification.sendNotification(eventList.get(h).toString(), "New message at "
                                        + projetName +" \n" + messageContent);
                                myNotification.addMessageNotificationsToDatabase(eventList.get(h), "New message at "
                                        + projetName +" \n" + messageContent);
                            }
                        }
                    }
                }, queryEmail);
            }
        });

    }

    /*private void setUpRecyclerView(String projetName){
        Query query = database.collection("ProJets").document(projetName).collection("Chat")
                .orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        adapter = new MessageAdapter(options, projetName);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjetGroupChatActivity.this));
        recyclerView.setAdapter(adapter);
    }*/

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
                                String e = doc.getId().toString();
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

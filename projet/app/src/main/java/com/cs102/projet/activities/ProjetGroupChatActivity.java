package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cs102.projet.classes.MyNotificationClass;
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
    //Global Variables
    String projetName;
    TextView groupChatname;
    EditText editTextMessageContent;
    ImageButton buttonSend;
    String currentUserMail;
    String currentUserName;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    MessageAdapter adapter;
    MyNotificationClass myNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projet_group_chat);

        //Getting the clicked ProJet name
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        //Firebase init
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //Connect views
        buttonSend = findViewById(R.id.imageButtonSend_chat);
        editTextMessageContent = findViewById(R.id.editTextMessage_chat);

        //Moving the entire page up when text input occurs.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Setting the group's chat page name here
        String setterForChatPage = projetName + " Group Chat";
        groupChatname = findViewById(R.id.textView_groupchat);
        groupChatname.setText(setterForChatPage);

        //Moving the entire page up when text input occurs.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Notification init
        myNotification = new MyNotificationClass();

        //Query to get messages from the database and move screen upwards as new ones come.
        Query query = database.collection("ProJets").document(projetName).collection("Chat")
                .orderBy("time", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        //Initializing the adapter
        adapter = new MessageAdapter(options, projetName);

        //Declaring the recyclerView variables here.
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager xx = new LinearLayoutManager(ProjetGroupChatActivity.this);
        recyclerView.setLayoutManager(xx);
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() 
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) 
            {
                super.onItemRangeInserted(positionStart, itemCount);

                //These variables are needed to automatically scroll page to the bottom as new messages come.
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = xx.findLastCompletelyVisibleItemPosition();

                //If the recycler view is initially being loaded or the user is at the bottom of the list, scroll to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1)))
                {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        }
    );

        //Message send button onClick
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the current date.
                Date currentdate = Calendar.getInstance().getTime();

                //These strings will be sent to server or used in the process.
                final String messageContent = editTextMessageContent.getText().toString();
                editTextMessageContent.setText("");
                Timestamp messageDate = new Timestamp(currentdate);

                //Handling empty input
                if (messageContent.equals(""))
                {
                    Toast.makeText(ProjetGroupChatActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final Map<String, String> messageInfo = new HashMap<>();
                    messageInfo.put("message", messageContent);
                    messageInfo.put("coming_from", currentUserMail);

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

                    //To add the creation date.
                    final Map<String, Timestamp> messeageInfo2 = new HashMap<>();
                    messeageInfo2.put("time", messageDate);
                    final DocumentReference messageReference = database.collection("ProJets")
                            .document(projetName).collection("Chat").document(currentdate.toString());
                    messageReference.set(messeageInfo2, SetOptions.merge());
                }

                //To find ProJet members collection and get the e-mails.
                //moveData function is used to find e-mails (explained at the end of this page).
                //Function "useInfo" which is a part of" moveData" enables us to use these mails to send notification to all members.
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

    /**
     * Necessary method for real-time(recycleView) view of page to work
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Necessary method for real-time(recycleView) view of page to work
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    // The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    // To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    // What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    // While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(final GetInformations getInformations, Query query)
    {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    List<String> eventList = new ArrayList<>();

                    for(DocumentSnapshot doc : task.getResult())
                    {
                        String e = doc.getId().toString();
                        eventList.add(e);
                    }
                    getInformations.useInfo(eventList);
                }
                else
                {
                    Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                }
            }
        });
    }
}

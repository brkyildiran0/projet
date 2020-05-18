package com.cs102.projet.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.interfaces.GetInformations;
import com.cs102.projet.classes.Message;
import com.cs102.projet.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String projetName;

    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    // To fix the problems of view types when scrolling.
    //@Override
    /*public int getItemViewType(int position) {
        return position;
    }*/

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, String projetName) {
        super(options);
        this.projetName = projetName;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageHolder holder, int position, @NonNull final Message model) {
        holder.textView_message.setText(model.getMessage());

        //To know the message owner is me or not and colorization with this information.
        /*myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
        if(currentUser.getEmail().equals(model.getComing_from())) {
            holder.itemView.setBackgroundColor(Color.GREEN);
        }
        else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }*/


        //To get user_name from field "coming_from". (E mail >>>> user name)
        Query query = db.collection("Users").whereEqualTo("user_email", model.getComing_from()).limit(1);
        moveData(new GetInformations() {

            @Override
            // We use the informations coming from onCompleteListener in "useInfo" method.
            public void useInfo(List<String> eventList) {
                // To create a string for the user name.
                String userName = "";
                // Getting user name and prevent any error that cause application to stop!
                for (int p = 0; p < eventList.size(); p++) {
                    userName = userName + eventList.get(p);
                }


                if (userName == "" && userName == null) {
                    userName = "Error: Username";
                }

                // To set text where placed on Cardview.
                //TODO : Date should be converted to day!!
                holder.textView_messageInfo.setText("[" + userName + " " + model.getTime().toDate() + "]:");

            }
        }, query);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        return new MessageHolder(v);
    }

    class MessageHolder extends RecyclerView.ViewHolder{


        //TextView projetName;
        TextView textView_messageInfo;
        TextView textView_message;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            textView_messageInfo = itemView.findViewById(R.id.textView_messageInfo_chat);
            textView_message = itemView.findViewById(R.id.textView_message_chat);

        }
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
                                //Be careful about get() method. Modify this to whatever you need.
                                String e = doc.get("user_name").toString();
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

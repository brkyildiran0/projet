package com.cs102.projet.adapters;

import android.app.DownloadManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.classes.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationHolder> {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    String currentUserEmail;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull final Notification model) {

        holder.textViewMessage.setText(model.getMessage());
        holder.textViewTitle.setText(model.getTitle());

        //Connect to Firebase in order to delete notifications.
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
        currentUserEmail= currentUser.getEmail();

        //onClicklistener for delete button.
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To find exact notification, we look at the item's time, title and message AND find "the" notification.
                Query deleteQuery = database.collection("Users").document(currentUserEmail)
                        .collection("Notifications")
                        .whereEqualTo("time", model.getTime())
                        .whereEqualTo("title", model.getTitle())
                        .whereEqualTo("message", model.getMessage());
                updateNotification(deleteQuery);
            }
        });
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);

        return new NotificationHolder(v);
    }

    class NotificationHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewMessage;
        ImageButton deleteButton;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.notificationProjectName);
            textViewMessage = itemView.findViewById(R.id.notificationMessage);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public void updateNotification(Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for(DocumentSnapshot doc : task.getResult()) {
                                DocumentReference documentReference = doc.getReference();
                                documentReference.update("delete", true);
                            }
                        } else {
                            Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                        }
                    }
                });
    }
}

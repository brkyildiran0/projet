package com.cs102.projet.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.classes.Task;
import com.cs102.projet.interfaces.GetInformations;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompletedTasksAdapter extends FirestoreRecyclerAdapter<Task, CompletedTasksAdapter.CompletedTasksHolder> {

    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    String currentUserEmail;

    public CompletedTasksAdapter(@NonNull FirestoreRecyclerOptions<Task> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CompletedTasksHolder holder, int position, @NonNull Task model) {
        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
        currentUserEmail = currentUser.getEmail();

        holder.taskName.setText(model.getTask_name());
        String priority = model.getTask_priority();
        if( priority.equals("1")){
            holder.priority_show.setBackgroundColor(Color.GREEN);
        }
        else if( priority.equals("2")){
            holder.priority_show.setBackgroundColor(Color.YELLOW);
        }

        Query query = database.collection("Users").whereEqualTo("user_email", model.getTask_owner());
        moveData(new GetInformations() {
            @Override
            public void useInfo(List<String> eventList) {
                holder.userName.setText(eventList.get(0));
            }
        }, query);

    }

    @NonNull
    @Override
    public CompletedTasksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_task_item, parent, false);

        return new CompletedTasksHolder(v);
    }

    class CompletedTasksHolder extends RecyclerView.ViewHolder{
        TextView taskName;
        TextView userName;
        View priority_show;

        public CompletedTasksHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName_completed_tasks);
            userName = itemView.findViewById(R.id.username_completed_tasks);
            priority_show = itemView.findViewById(R.id.priority_completed_tasks);
        }
    }

    public void moveData(final GetInformations getInformations, Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventList = new ArrayList<>();

                            for(DocumentSnapshot doc : task.getResult()) {
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

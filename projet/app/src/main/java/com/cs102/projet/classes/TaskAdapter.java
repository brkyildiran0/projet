package com.cs102.projet.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskAdapter extends FirestoreRecyclerAdapter<Task, TaskAdapter.TaskHolder> {

    // To get ProJet name.
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String projetName;

    //To get user informations.
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    // ********* Extra String parameter **************
    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Task> options, String projetName) {
        super(options);

        // To get ProJet name. ******* Extra value ************
        this.projetName = projetName;

        // To get user informations.
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskHolder holder, final int position, @NonNull Task model) {
        holder.textView_task_name.setText(model.getTask_name());
        holder.textView_task_due_date.setText(model.getTask_due_date());
        holder.textView_task_description.setText(model.getTask_description());
        holder.buttonGetTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // To get name of the task clicked.
                String nameOfTheTask = getSnapshots().getSnapshot(position).getReference().getId();

                // To get user e-mail.
                String userEmail = currentUser.getEmail();

                // To create a document reference in order to change task owner.
                DocumentReference docref = db.collection("ProJets").document(projetName)
                        .collection("Tasks").document(nameOfTheTask);

                // To update the task owner with user e-mail.
                docref.update("task_owner", userEmail);
            }
        });
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);

        //**************** IMPORTANT PARAMETER ************** To move the projet name
        // from Task Adapter class to TaskHolder class. (The parameter is : this.projetName)
        return new TaskHolder(v, this.projetName);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        TextView textView_task_description;
        TextView textView_task_due_date;
        TextView textView_task_name;
        Button buttonGetTask;

        //************* The parameter "projetName" is used in order to take the projetname from Task Adapter class. ******************
        public TaskHolder(@NonNull View itemView, final String projetName) {
            super(itemView);
            textView_task_description = itemView.findViewById(R.id.textView_task_description);
            textView_task_due_date = itemView.findViewById(R.id.textView_task_due_date);
            textView_task_name = itemView.findViewById(R.id.textView_task_name);
            buttonGetTask = itemView.findViewById(R.id.buttonGetTask);

        }
    }
}

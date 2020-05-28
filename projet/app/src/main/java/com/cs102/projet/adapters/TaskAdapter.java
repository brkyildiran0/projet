package com.cs102.projet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.classes.Task;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class TaskAdapter extends FirestoreRecyclerAdapter<Task, TaskAdapter.TaskHolder>
{
    //Global Variables
    String projetName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    //Adapter Initializing
    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Task> options, String projetName)
    {
        super(options);

        this.projetName = projetName;
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskHolder holder, final int position, @NonNull final Task model)
    {
        //Getting the attributes of the ProJet and attaching them to the holder
        holder.textView_task_name.setText(model.getTask_name());
        holder.textView_task_due_date.setText(model.getTask_due_date());
        holder.textView_task_description.setText(model.getTask_description());
        holder.textView_task_due_hour.setText(model.getTask_due_hour());

        //Getting the priority of a task
        final String taskPriority = model.getTask_priority();

        //Handling for each case where a task is complete or not
        if(taskPriority.equals("1"))
        {
            holder.imageView_priority.setVisibility(View.GONE);
            holder.imageView_priority2.setVisibility(View.GONE);
            holder.imageView_priority3.setImageResource(R.drawable.ic_priority_high_green_40dp);
        }
        else if(taskPriority.equals("2"))
        {
            holder.imageView_priority.setVisibility(View.GONE);
            holder.imageView_priority2.setImageResource(R.drawable.ic_priority_high_yellow_40dp);
            holder.imageView_priority3.setImageResource(R.drawable.ic_priority_high_yellow_40dp);
        }

        holder.buttonGetTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //To get user e-mail.
                String userEmail = currentUser.getEmail();

                //To create a document reference in order to change task owner.
                DocumentReference theTask = db.collection("ProJets").document(projetName).collection("Tasks")
                        .document(model.getTask_name());

                //To update the task owner with user e-mail.
                theTask.update("task_owner", userEmail);

                //Adding the task to the User database as well(to list all tasks in Profile Page)
                Map<String, DocumentReference> taskReference = new HashMap<>();
                taskReference.put(model.getTask_name(), db.collection("ProJets").document(projetName).collection("Tasks").document(model.getTask_name() ));

                Map<String, String> taskString = new HashMap<>();
                taskString.put("task_name", model.getTask_name());
                taskString.put("task_due_date", model.getTask_due_date());
                taskString.put("task_priority", model.getTask_priority());
                taskString.put("projet_name", projetName);

                db.collection("Users").document(myFirebaseAuth.getCurrentUser().getEmail()).collection("Current Tasks").document(model.getTask_name()).set(taskReference);
                db.collection("Users").document(myFirebaseAuth.getCurrentUser().getEmail()).collection("Current Tasks").document(model.getTask_name()).set(taskString, SetOptions.merge());
            }
        });
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);

        //**************** IMPORTANT PARAMETER ************** To move the projet name
        // from Task Adapter class to TaskHolder class. (The parameter is : this.projetName)
        return new TaskHolder(v);
    }

    /**
     * Class for recycler view and view holder to work together
     * Processes the information for each task and assigns it to the TextView to be seen
     */
    class TaskHolder extends RecyclerView.ViewHolder
    {
        TextView textView_task_description;
        TextView textView_task_due_date;
        TextView textView_task_name;
        Button buttonGetTask;
        TextView textView_task_due_hour;
        ImageView imageView_priority;
        ImageView imageView_priority2;
        ImageView imageView_priority3;

        //************* The parameter "projetName" is used in order to take the projetname from Task Adapter class. ******************
        public TaskHolder(@NonNull View itemView)
        {
            super(itemView);

            textView_task_description = itemView.findViewById(R.id.textView_task_description);
            textView_task_due_date = itemView.findViewById(R.id.textView_task_due_date);
            textView_task_name = itemView.findViewById(R.id.textView_task_name);
            textView_task_due_hour = itemView.findViewById(R.id.textView_task_due_hour);

            buttonGetTask = itemView.findViewById(R.id.buttonGetTask);

            imageView_priority = itemView.findViewById(R.id.imageView_priority_task_item);
            imageView_priority2 = itemView.findViewById(R.id.imageView_priority_task_item2);
            imageView_priority3 = itemView.findViewById(R.id.imageView_priority_task_item3);
        }
    }
}

package com.cs102.projet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.classes.Task;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MyTasksAdapter extends FirestoreRecyclerAdapter<Task, MyTasksAdapter.MyTasksHolder> {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String projetName;
    Long currentAmountOfCompletedTasks;
    int assigner;
    Long currentAmountOfIncompletedTasks;
    int assignerTwo;

    public MyTasksAdapter(@NonNull FirestoreRecyclerOptions<Task> options, String projetName) {
        super(options);
        this.projetName = projetName;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyTasksHolder holder, int position, @NonNull final Task model) {
        holder.taskName.setText(model.getTask_name());
        holder.taskDesc.setText(model.getTask_description());
        holder.taskDueDate.setText(model.getTask_due_date());

        holder.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference theTask = database.collection("ProJets").document(projetName).collection("Tasks")
                        .document(model.getTask_name());
                theTask.update("task_status", true);

                //Incrementing total_completed_tasks attribute by 1 since the task is declared as completed above.
                //And decrementing total_incompleted_tasks attribute by 1.
                database.collection("ProJets").document(projetName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        currentAmountOfCompletedTasks = documentSnapshot.getLong("total_completed_tasks");
                        assigner = 0;
                        assigner = currentAmountOfCompletedTasks.intValue();
                        assigner++;
                        Integer databaseSender = assigner;

                        //Now adding the increased value to the ProJet root back again.
                        Map<String, Integer> increaser = new HashMap<>();
                        increaser.put("total_completed_tasks", databaseSender);
                        database.collection("ProJets").document(projetName).set(increaser, SetOptions.merge());

                        //Decrementing the total_incompleted_tasks value at ProJet database root by 1, since new task added.
                        currentAmountOfIncompletedTasks = documentSnapshot.getLong("total_uncompleted_tasks");
                        assignerTwo = 0;
                        assignerTwo = currentAmountOfIncompletedTasks.intValue();
                        assignerTwo--;
                        Integer databaseSenderTwo = assignerTwo;

                        //Now adding the increased value to the ProJet root back again.
                        Map<String, Integer> increaserTwo = new HashMap<>();
                        increaserTwo.put("total_uncompleted_tasks", databaseSenderTwo);
                        database.collection("ProJets").document(projetName).set(increaserTwo, SetOptions.merge());
                    }
                });
            }
        });

        holder.buttonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference theTask = database.collection("ProJets").document(projetName).collection("Tasks")
                        .document(model.getTask_name());
                theTask.update("task_owner", "");
            }
        });
    }

    @NonNull
    @Override
    public MyTasksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_task_item,parent,false);
        return new MyTasksHolder(v);
    }

    class MyTasksHolder extends RecyclerView.ViewHolder{
        TextView taskName;
        TextView taskDesc;
        TextView taskDueDate;

        ImageButton buttonFinish;
        ImageButton buttonLeave;
        public MyTasksHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.textView_task_name_my_task_item);
            taskDesc = itemView.findViewById(R.id.textView_task_desc_my_task_item);
            taskDueDate = itemView.findViewById(R.id.textView_task_due_date_my_task_item);
            buttonFinish = itemView.findViewById(R.id.buttonFinish_my_task_item);
            buttonLeave = itemView.findViewById(R.id.buttonLeave_my_Task_item);
        }
    }
}

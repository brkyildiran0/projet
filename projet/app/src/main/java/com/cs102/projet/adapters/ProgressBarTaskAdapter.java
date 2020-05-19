package com.cs102.projet.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.activities.CurrentTasksActivity;
import com.cs102.projet.activities.MembersPageActivity;
import com.cs102.projet.activities.ProjectPageActivity;
import com.cs102.projet.classes.ProgressBarTask;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProgressBarTaskAdapter extends FirestoreRecyclerAdapter<ProgressBarTask, ProgressBarTaskAdapter.ProgressBarTaskHolder> {

    public ProgressBarTaskAdapter(@NonNull FirestoreRecyclerOptions<ProgressBarTask> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ProgressBarTaskHolder holder, int position, @NonNull ProgressBarTask model)
    {
        int completed_task = model.getTotal_completed_tasks();
        int uncompleted_task = model.getTotal_uncompleted_tasks();
        int total_Task = completed_task + uncompleted_task;

        holder.completed_task.setText(String.valueOf(completed_task));
        holder.total_task.setText(String.valueOf(total_Task));
        holder.progressBar_task.setMax(total_Task);
        holder.progressBar_task.setProgress(completed_task);

        //TODO : burayı kaldır profile page e koyacağız.
        holder.progressBar_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentZ = new Intent(holder.itemView.getContext(), CurrentTasksActivity.class);
                //intentZ.putExtra("projetName", projetName);
                holder.itemView.getContext().startActivity(intentZ);
            }
        });
    }

    @NonNull
    @Override
    public ProgressBarTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_task_item, parent,false);
        return new ProgressBarTaskHolder(v);
    }

    class ProgressBarTaskHolder extends RecyclerView.ViewHolder
    {
        TextView completed_task;
        TextView total_task;
        ProgressBar progressBar_task;

        public ProgressBarTaskHolder(@NonNull View itemView) {
            super(itemView);
            completed_task = itemView.findViewById(R.id.textView_task_completed);
            total_task = itemView.findViewById(R.id.textView_task_total);
            progressBar_task = itemView.findViewById(R.id.progressBar_task);
        }
    }
}

package com.cs102.projet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.classes.ProgressBarTask;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProgressBarTaskAdapter extends FirestoreRecyclerAdapter<ProgressBarTask, ProgressBarTaskAdapter.ProgressBarTaskHolder> {

    public ProgressBarTaskAdapter(@NonNull FirestoreRecyclerOptions<ProgressBarTask> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProgressBarTaskHolder holder, int position, @NonNull ProgressBarTask model) {
        int completed_task = model.getTotal_completed_tasks();
        int uncompleted_task = model.getTotal_uncompleted_tasks();
        int total_Task = completed_task+ uncompleted_task;
        int calculated = (int)((Double.valueOf(String.valueOf(total_Task)))/
                (Double.valueOf(String.valueOf(completed_task))))*100;

        holder.completed_task.setText(String.valueOf(completed_task));
        holder.total_task.setText(String.valueOf(total_Task));
        holder.progressBar_task.setProgress(calculated);
    }

    @NonNull
    @Override
    public ProgressBarTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_task_item, parent,false);

        return new ProgressBarTaskHolder(v);
    }

    class ProgressBarTaskHolder extends RecyclerView.ViewHolder{
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

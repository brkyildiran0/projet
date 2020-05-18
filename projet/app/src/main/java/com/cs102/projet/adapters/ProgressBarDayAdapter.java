package com.cs102.projet.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs102.projet.R;
import com.cs102.projet.classes.ProgressBarDay;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Calendar;
import java.util.Date;

public class ProgressBarDayAdapter extends FirestoreRecyclerAdapter<ProgressBarDay, ProgressBarDayAdapter.ProgressBarDayHolder> {

    public ProgressBarDayAdapter(@NonNull FirestoreRecyclerOptions<ProgressBarDay> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProgressBarDayHolder holder, int position, @NonNull ProgressBarDay model) {
        Date currentdate = Calendar.getInstance().getTime();

        holder.remainingDay.setText(currentdate.toString());
    }

    @NonNull
    @Override
    public ProgressBarDayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_day_item, parent, false);

        return new ProgressBarDayHolder(v);
    }

    class ProgressBarDayHolder extends RecyclerView.ViewHolder{
        TextView remainingDay;
        ProgressBar progressBar_remain_day;
        public ProgressBarDayHolder(@NonNull View itemView) {
            super(itemView);
            remainingDay = itemView.findViewById(R.id.progressbar_remaining_day);
            progressBar_remain_day = itemView.findViewById(R.id.progressBar_day);
        }
    }
}

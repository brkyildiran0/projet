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
import com.cs102.projet.activities.ProjectPageActivity;
import com.cs102.projet.classes.ProJet;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProJetAdapter extends FirestoreRecyclerAdapter<ProJet, ProJetAdapter.ProJetHolder> {

    public ProJetAdapter(@NonNull FirestoreRecyclerOptions<ProJet> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProJetHolder holder, int position, @NonNull ProJet model) {
        holder.projet_name.setText(model.getProjet_name());
        holder.projet_due_date.setText(model.getProjet_due_date());
        holder.setprogress_text.setText("73");
        holder.progressBar.setProgress(73);
    }

    @NonNull
    @Override
    public ProJetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.projet_info_item, parent, false);

        return new ProJetHolder(v);
    }

    class ProJetHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView projet_due_date;
        TextView projet_name;
        TextView setprogress_text;
        ProgressBar progressBar;

        public ProJetHolder(@NonNull View itemView) {
            super(itemView);
            projet_name = itemView.findViewById(R.id.projetName_fr);
            projet_due_date = itemView.findViewById(R.id.setDate_fr);
            setprogress_text = itemView.findViewById(R.id.progressText_fr);
            progressBar = itemView.findViewById(R.id.progressBar_fr);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent gecIntent = new Intent(v.getContext(), ProjectPageActivity.class);
            gecIntent.putExtra("projetName", projet_name.getText().toString());
            v.getContext().startActivity(gecIntent);
        }
    }
}

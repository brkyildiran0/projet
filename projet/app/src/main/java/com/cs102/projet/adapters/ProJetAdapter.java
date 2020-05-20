package com.cs102.projet.adapters;

import android.content.Intent;
import android.util.Log;
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
import com.cs102.projet.interfaces.GetInformations;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProJetAdapter extends FirestoreRecyclerAdapter<ProJet, ProJetAdapter.ProJetHolder>
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Double completed_tasks = 0.0;
    Double uncompleted_tasks = 0.0;
    Double total_tasks = 0.0;
    GetInformations get;
    private int percentage = 0;

    public ProJetAdapter(@NonNull FirestoreRecyclerOptions<ProJet> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ProJetHolder holder, int position, @NonNull ProJet model)
    {
        Query query = db.collection("ProJets").whereEqualTo("projet_name", model.getProjet_name());

        moveData(new GetInformations()
        {
            @Override
            public void useInfo(List<String> eventList)
            {
                if (eventList.size() != 0)
                {
                    completed_tasks = Double.valueOf(eventList.get(0));
                    uncompleted_tasks = Double.valueOf(eventList.get(1));
                    total_tasks = completed_tasks + uncompleted_tasks;
                    percentage = (int) ((completed_tasks / total_tasks) * 100);
                }
                else
                {
                    percentage = (int) ((completed_tasks / total_tasks) * 100);
                }
                holder.setprogress_text.setText("%" + percentage);
                holder.progressBar.setProgress(percentage);
            }
        }, query);

        holder.projet_name.setText(model.getProjet_name());
        holder.projet_due_date.setText(model.getProjet_due_date());
    }

    // The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    // To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    // What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    // While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(GetInformations getInformations, Query query) {
        get = getInformations;
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventList = new ArrayList<>();

                            for(DocumentSnapshot doc : task.getResult()) {
                                Log.e("giris: ", "for a girildi");
                                String completed_tasks = String.valueOf((doc.getLong("total_completed_tasks")));
                                Log.e("aradayiz: ", completed_tasks);
                                String uncompleted_tasks = String.valueOf(doc.getLong("total_uncompleted_tasks"));
                                eventList.add(completed_tasks);
                                eventList.add(uncompleted_tasks);
                                Log.e("cikis: ", "for dan cikildi");
                            }
                            get.useInfo(eventList);
                        } else {
                            Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                        }
                    }
                });
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

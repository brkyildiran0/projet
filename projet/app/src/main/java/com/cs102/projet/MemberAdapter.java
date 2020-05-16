package com.cs102.projet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class MemberAdapter extends FirestoreRecyclerAdapter<Member, MemberAdapter.MemberHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String projetName;

    public MemberAdapter(@NonNull FirestoreRecyclerOptions<Member> options, String projetName) {
        super(options);
        this.projetName = projetName;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MemberHolder holder, int position, @NonNull Member model) {
        holder.textView_member_name.setText(model.getUser_name());

        // To get mail, we use document name.
        String mail = getSnapshots().getSnapshot(position).getReference().getId();
        holder.textView_member_email.setText(mail);

        // To get tasks that members have. This is limited to 3.
        Query query = db.collection("ProJets").document(projetName)
                .collection("Tasks").whereEqualTo("task_owner", mail).limit(3);

        moveData(new GetInformations() {

            @Override
            // We use the informations coming from onCompleteListener in "useInfo" method.
            public void useInfo(List<String> eventList) {
                // To create a string for the tasks..
                String tasks = "";
                int count = 1;
                // Getting tasks from list one by one.
                for ( int p = 0; p < eventList.size(); p++){
                    tasks = tasks + String.valueOf(count) + "- " + eventList.get(p) + "\n";
                    count++;
                }

                // To set text where placed on Cardview.
                if(tasks == ""){
                    tasks = "There is no task!";
                }

                holder.textView_member_tasks.setText(tasks);

            }
        }, query);
    }



    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent, false);
        return new MemberHolder(v);
    }

    class MemberHolder extends RecyclerView.ViewHolder{

        TextView textView_member_name;
        TextView textView_member_email;
        TextView textView_member_tasks;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);
            textView_member_name = itemView.findViewById(R.id.textView_member_name_members_page);
            textView_member_email = itemView.findViewById(R.id.textView_member_email_members_page);
            textView_member_tasks = itemView.findViewById(R.id.textView_member_tasks_members_page);
        }
    }

    // The OnCompleteListener method is an asynchronous method. So, we cannot move the informations outside of the method.
    // To do that, we create a new method that takes parameters an interface called "getInformations" and a query that we want to implement this method on.
    // What does this method do? It is a normal method until the line "getInformations.useInfo(eventlist)".
    // While this method is being used, the inner method is already completed. So, we can get the informations and we use them in "useInfo" method.
    public void moveData(final GetInformations getInformations, Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventList = new ArrayList<>();

                            for(DocumentSnapshot doc : task.getResult()) {
                                String e = doc.get("task_name").toString();
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

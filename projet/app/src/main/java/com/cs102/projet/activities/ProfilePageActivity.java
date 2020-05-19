package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cs102.projet.R;
import com.cs102.projet.fragments.FragmentCurrentTasks;
import com.cs102.projet.interfaces.GetInformations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    TextView userMail;
    TextView userName;

    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //View initialize
        userMail = findViewById(R.id.textMailAdress);
        userName = findViewById(R.id.textRealNameSurname);

        //Setting the Name & Email of the current user
        assert currentUser != null;
        userMail.setText(currentUser.getEmail());
        database.collection("Users").document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                userName.setText(documentSnapshot.getString("user_name"));
            }
        });


        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        currentUserEmail = currentUser.getEmail();

        Query query = database.collection("Users").document(currentUserEmail).collection("Current Tasks");
        moveData(new GetInformations() {
            @Override
            public void useInfo(List<String> eventList) {
                Log.e("list : ", eventList.toString());
                for(int h = 0; h < eventList.size(); h = h+4){
                    ft.add(R.id.container_current_tasks, new FragmentCurrentTasks(eventList.get(h),
                            eventList.get(h+1),eventList.get(h+2),Long.valueOf(eventList.get(h+3))));
                }
                ft.commit();
            }
        }, query);
    }

    //Method for the AppBar Buttons & Icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //TODO: edit the following comments as other pages gets created.
        switch (item.getItemId())
        {
            case R.id.profile_icon_on_toolbar:
                //should stay as empty
                return true;
            case R.id.notification_icon_on_toolbar:
                finish();
                Intent goToNotifications = new Intent(ProfilePageActivity.this, NotificationsActivity.class);
                startActivity(goToNotifications);
                return true;
            case R.id.help_button_on_toolbar:
                //write down lines to switch to the help page
                //...
                //...
                return true;
            case R.id.setting_button_on_toolbar:
                //write down lines to switch to the settings page
                //...
                //...
                return true;
            case R.id.logout_button_on_toolbar:
                //write down lines to logout the user
                //...
                //...
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void moveData(final GetInformations getInformations, Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventList = new ArrayList<>();

                            for(DocumentSnapshot doc : task.getResult()) {
                                String task_name = doc.getString("task_name");
                                String projet_name = doc.getString("projet_name");
                                String task_due_date = doc.getString("task_due_date");
                                String task_priority = doc.getString("task_priority");
                                eventList.add(task_name);
                                eventList.add(projet_name);
                                eventList.add(task_due_date);
                                eventList.add(task_priority);
                            }
                            getInformations.useInfo(eventList);
                        } else {
                            Log.e("QuerySnapshot Error!", "There is a problem while getting documents!");
                        }
                    }
                });
    }
}

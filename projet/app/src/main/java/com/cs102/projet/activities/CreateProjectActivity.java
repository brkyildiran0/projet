package com.cs102.projet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cs102.projet.NotificationsReceiver;
import com.cs102.projet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateProjectActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;
    private EditText editTextProjetName;
    private EditText editTextProjetDesc;
    private EditText editTextProjetDueDate;
    private EditText editTextProjetDueHour;
    String currentUserMail;
    String userRealName;
    String projetName;
    String projetDesc;
    String projetDueDate;
    String projetDueHour;

    private String day ;
    private String month ;
    private String year ;

    private String hour ;
    private String minute ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        //Firebase Initializing
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //connecting views to code
        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);
        editTextProjetName = findViewById(R.id.editTextProjetName);
        editTextProjetDesc = findViewById(R.id.editTextProjetDesc);
        editTextProjetDueDate = findViewById(R.id.editTextProjetDueDate);
        editTextProjetDueHour = findViewById(R.id.editTextProjetDueHour);

        // Using DatePicker in order to get valid dates
        // Date ClickListener..
        editTextProjetDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Using TimePicker in order to get valid times..
        // Hour ClickListener...
        editTextProjetDueHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //on click listener of Create New ProJet button
        buttonCreateNewProjet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //these string will be sent to server
                projetName = editTextProjetName.getText().toString();
                projetDesc = editTextProjetDesc.getText().toString();
                projetDueDate = editTextProjetDueDate.getText().toString();
                projetDueHour = editTextProjetDueHour.getText().toString();

                //Query to check whether a projet with the same name exists
                Query myQuery = database.collection("ProJets").whereEqualTo("projet_name", projetName);

                //Handling empty input with if-else
                if (projetName.equals("") || projetDesc.equals("") || projetDueDate.equals("") || projetDueHour.equals(""))
                {
                    Toast.makeText(CreateProjectActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    myQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            //IF there is no such projet with the desired name exists,
                            if (queryDocumentSnapshots.isEmpty())
                            {
                                //Creating a DocumentReference for current projet to make code cleaner
                                DocumentReference projetReference = database.collection("ProJets").document(projetName);

                                //Adding the essential parts of a projet and creating it at ProJets collection
                                Map<String, String> projetInfo = new HashMap<>();
                                projetInfo.put("projet_name", projetName);
                                projetInfo.put("projet_desc", projetDesc);
                                projetInfo.put("projet_due_date", projetDueDate);
                                projetInfo.put("projet_due_hour", projetDueHour);
                                projetReference.set(projetInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Toast.makeText(CreateProjectActivity.this, "ProJet successfully created!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                Toast.makeText(CreateProjectActivity.this, "Error. Can't create ProJet.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                //Adding the required boolean value to be able to finish a project in the future
                                Map<String, Boolean> isFinished = new HashMap<>();
                                isFinished.put("projet_is_complete", false);
                                projetReference.set(isFinished, SetOptions.merge());

                                //Creating users & tasks collection inside of the projet document and initializing them with currentUser's data
                                Map<String, DocumentReference> userInit = new HashMap<>();
                                userInit.put("user_reference", database.collection("Users").document(currentUserMail));
                                projetReference.collection("Members").document(currentUserMail).set(userInit, SetOptions.merge());

                                //Adding real name attribute to the user
                                database.collection("Users").document(currentUserMail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        userRealName = documentSnapshot.getString("user_name");
                                        assert userRealName != null;
                                        Log.d("User's real name is:", userRealName);

                                        Map<String, String> userInit2 = new HashMap<>();
                                        userInit2.put("user_name", userRealName);
                                        database.collection("ProJets").document(projetName).collection("Members").document(currentUserMail).set(userInit2, SetOptions.merge());
                                    }
                                });

                                //DocRef for creator of user, to improve code clarity
                                DocumentReference creatorUser = database.collection("Users").document(currentUserMail);

                                //Adding the projet reference and other properties of it to the creator of projet's current projets document
                                Map<String, DocumentReference> userProjetUpdate1 = new HashMap<>();
                                userProjetUpdate1.put("projet_reference", projetReference);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate1, SetOptions.merge());

                                Map<String, String> userProjetUpdate2 = new HashMap<>();
                                userProjetUpdate2.put("projet_name", projetName);
                                userProjetUpdate2.put("projet_desc", projetDesc);
                                userProjetUpdate2.put("projet_due_date", projetDueDate);
                                userProjetUpdate2.put("projet_due_hour", projetDueHour);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate2, SetOptions.merge());

                                //NOTIFICATION PART
                                // Getting task date and task name separately for the 2 hours left notification..
                                int indexForHour = projetDueHour.indexOf(':');
                                int indexFirstDate = projetDueDate.indexOf('/');
                                int indexSecondDate = projetDueDate.lastIndexOf('/');

                                day = projetDueDate.substring(0,indexFirstDate);
                                month = projetDueDate.substring(indexFirstDate + 1, indexSecondDate);
                                year = projetDueDate.substring(indexSecondDate + 1);

                                hour = projetDueHour.substring(0,indexForHour);
                                minute = projetDueHour.substring(indexForHour + 1);

                                int monthN = Integer.parseInt(month);
                                int dayN = Integer.parseInt(day);
                                int hourN = Integer.parseInt(hour);
                                int minuteN = Integer.parseInt(minute);

                                Log.e("month", month);
                                Log.e("day", day);
                                Log.e("hour", hour);
                                Log.e("minute", minute);

                                // Sending notification to the current user when 2 hours left for the task...
                                plannedNotification("2 days left for the projet " + projetName, monthN, dayN, hourN, minuteN );

                                //closing the creation page, and removing it from backstack
                                startActivity(new Intent(CreateProjectActivity.this, ProjetMainPageActivity.class));
                                finish();
                            }
                            //If there is another projet with  the desired name, adding an invisible char to the end of the desired projet name
                            else
                            {
                                //TODO handle this correctly.
                                Toast.makeText(CreateProjectActivity.this, "ProJet with the desired name already exists!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    // Method For Date Picker to get valid dates from users..
    public void showDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        editTextProjetDueDate.setText(date);
    }

    // Method For Time Picker to get valid times from users..
    public void showTimePickerDialog()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {

        editTextProjetDueHour.setText(hourOfDay + ":" +minute);
    }

    public void plannedNotification(String message, int month, int day, int hour, int minute){
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(CreateProjectActivity.this, ProjetMainPageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // For Oreo
            String channelId = "channelId";
            String channelName = "channelName";
            String channelDef = "channelDef";
            int channelPriority = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);

            if (channel == null){
                channel = new NotificationChannel(channelId, channelName, channelPriority);
                channel.setDescription(channelDef);

                notificationManager.createNotificationChannel(channel);
            }

            builder = new NotificationCompat.Builder(this, channelId);
            builder.setContentTitle("ProJet!!");
            builder.setContentText(message); //TODO get some real times for all tasks
            builder.setSmallIcon(R.drawable.app_logo);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);

        }
        else{
            // For Except Oreo

            builder = new NotificationCompat.Builder(this);


            builder.setContentTitle("ProJet!!");
            builder.setContentText(message); //TODO get some real times for all tasks
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.app_logo);
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setContentIntent(pendingIntent);
        }

        Intent broadcastIntent = new Intent(CreateProjectActivity.this, NotificationsReceiver.class);
        broadcastIntent.putExtra("object", builder.build());

        PendingIntent goBroadcast = PendingIntent.getBroadcast(this, 0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day - 2);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), goBroadcast);
    }
}
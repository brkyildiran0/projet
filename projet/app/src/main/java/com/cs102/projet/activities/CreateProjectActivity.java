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

import com.cs102.projet.recievers.NotificationsReceiver;
import com.cs102.projet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateProjectActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    //Global Variables
    int dayOfMonth;
    int monthOfYear;
    int year;
    String day;
    String month;
    String hour;
    String minute;
    String currentUserMail;
    String userRealName;
    String projetName;
    String projetDesc;
    String projetDueDate;
    String projetDueHour;
    EditText editTextProjetName;
    EditText editTextProjetDesc;
    EditText editTextProjetDueDate;
    EditText editTextProjetDueHour;
    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        //Initializing the current date
        dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        monthOfYear = Calendar.getInstance().get(Calendar.MONTH) + 1;
        year = Calendar.getInstance().get(Calendar.YEAR);

        //Firebase Initializing
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //Getting current logged in user's mail address
        assert currentUser != null;
        currentUserMail = currentUser.getEmail();

        //Connecting views to code
        Button buttonCreateNewProjet = findViewById(R.id.buttonCreateNewProjet);
        editTextProjetName = findViewById(R.id.editTextProjetName);
        editTextProjetDesc = findViewById(R.id.editTextProjetDesc);
        editTextProjetDueDate = findViewById(R.id.editTextProjetDueDate);
        editTextProjetDueHour = findViewById(R.id.editTextProjetDueHour);

        //On click listener for picking date
        editTextProjetDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //On click listener for picking time
        editTextProjetDueHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //On click listener for Create New ProJet button
        buttonCreateNewProjet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //These strings will be sent to server
                projetName = editTextProjetName.getText().toString();
                projetDesc = editTextProjetDesc.getText().toString();
                projetDueDate = editTextProjetDueDate.getText().toString();
                projetDueHour = editTextProjetDueHour.getText().toString();

                //Query to check whether a ProJet with the same name already exists
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
                            //If there is no such ProJet with the desired name already exists
                            if (queryDocumentSnapshots.isEmpty())
                            {
                                //Creating a DocumentReference for current ProJet to make code cleaner
                                DocumentReference projetReference = database.collection("ProJets").document(projetName);

                                //Adding the essential parts of a ProJet and creating it at ProJets collection
                                Map<String, String> projetInfo = new HashMap<>();
                                projetInfo.put("projet_name", projetName);
                                projetInfo.put("projet_desc", projetDesc);
                                projetInfo.put("projet_due_date", projetDueDate);
                                projetInfo.put("projet_due_hour", projetDueHour);
                                projetInfo.put("projet_created_date", dayOfMonth + "/" + monthOfYear + "/" + year);
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

                                //Adding the required completed/incomplete task int values beforehand to handle nullPointerException
                                Map<String, Integer> integerHashMap = new HashMap<>();
                                integerHashMap.put("total_completed_tasks", 0);
                                integerHashMap.put("total_uncompleted_tasks", 0);
                                projetReference.set(integerHashMap,SetOptions.merge());

                                //Creating users & tasks collection inside of the ProJet document and initializing them with currentUser's data
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

                                        Map<String, String> userInit2 = new HashMap<>();
                                        userInit2.put("user_name", userRealName);
                                        database.collection("ProJets").document(projetName).collection("Members").document(currentUserMail).set(userInit2, SetOptions.merge());
                                    }
                                });

                                //Reference to improve code clarity
                                DocumentReference creatorUser = database.collection("Users").document(currentUserMail);

                                //Adding the ProJet reference and other properties of it to the creator of ProJet's Current ProJets document
                                Map<String, DocumentReference> userProjetUpdate1 = new HashMap<>();
                                userProjetUpdate1.put("projet_reference", projetReference);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate1, SetOptions.merge());

                                Map<String, String> userProjetUpdate2 = new HashMap<>();
                                userProjetUpdate2.put("projet_name", projetName);
                                userProjetUpdate2.put("projet_desc", projetDesc);
                                userProjetUpdate2.put("projet_due_date", projetDueDate);
                                userProjetUpdate2.put("projet_due_hour", projetDueHour);
                                creatorUser.collection("Current ProJets").document(projetName).set(userProjetUpdate2, SetOptions.merge());

                                //***NOTIFICATION PART***
                                //Getting the task date and name separately to inform user when 2 hours left to notification
                                int indexForHour = projetDueHour.indexOf(':');
                                int indexFirstDate = projetDueDate.indexOf('/');
                                int indexSecondDate = projetDueDate.lastIndexOf('/');

                                //Assigning the needed Strings for date/time pickers to work properly.
                                day = projetDueDate.substring(0, indexFirstDate);
                                month = projetDueDate.substring(indexFirstDate + 1, indexSecondDate);
                                hour = projetDueHour.substring(0, indexForHour);
                                minute = projetDueHour.substring(indexForHour + 1);

                                //Creating another set on integers to safely pass the values to the method plannedNotification (integers below are to assure there won't be any error).
                                int monthN = Integer.parseInt(month);
                                int dayN = Integer.parseInt(day);
                                int hourN = Integer.parseInt(hour);
                                int minuteN = Integer.parseInt(minute);

                                //Sending the notification at planned time
                                plannedNotification("2 days left for the ProJet " + projetName, monthN, dayN, hourN, minuteN );

                                //Closing the creation page
                                startActivity(new Intent(CreateProjectActivity.this, ProjetMainPageActivity.class));
                                finish();
                            }
                            //Restricting the creation of a ProJet with an already taken name.
                            else
                            {
                                Toast.makeText(CreateProjectActivity.this, "ProJet with the desired name already exists!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * This method is called whenever due date dialog is clicked
     * Pops-up the dialog for user to pick the date
     */
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

    /**
     * Helper method for showDatePickerDialog() to work properly, and set the given input as a String.
     * @param view the pop-up view for user to input the date
     * @param year user input
     * @param month user input
     * @param dayOfMonth user input
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        //Because calendar starts at 0 for counting the months, incrementing month by 1
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        editTextProjetDueDate.setText(date);
    }

    /**
     * This method is called whenever due time dialog is clicked
     * Pops-up the dialog for user to pick the time
     */
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

    /**
     * Helper method for showTimePickerDialog() to work properly, and set the given input as a String.
     * @param view the pop-up view for user to input the time
     * @param hourOfDay user input
     * @param minute user input
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        String assigner = hourOfDay + ":" +minute;
        editTextProjetDueHour.setText(assigner);
    }

    /**
     * This method is responsible for plan & send notifications depending on the due date & time.
     * @param message is the message that is going to sent with the notification
     * @param month depends on user input
     * @param day depends on user input
     * @param hour depends on user input
     * @param minute depends on user input
     */
    public void plannedNotification(String message, int month, int day, int hour, int minute)
    {
        //Initializing NotificationCompat Builder
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent handling
        Intent intent = new Intent(CreateProjectActivity.this, ProjetMainPageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //For Oreo level devices, declaring needed values.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "channelId";
            String channelName = "channelName";
            String channelDef = "channelDef";
            int channelPriority = NotificationManager.IMPORTANCE_HIGH;

            //Creating the NotificationChannel
            assert notificationManager != null;
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);

            //If the channel is not null, creating & setting the Notification Channel.
            if (channel == null)
            {
                channel = new NotificationChannel(channelId, channelName, channelPriority);
                channel.setDescription(channelDef);

                notificationManager.createNotificationChannel(channel);
            }

            //Now assigning the created channel and other necessary features to builder initialized at the beginning of the method.
            builder = new NotificationCompat.Builder(this, channelId);
            builder.setContentTitle("ProJet!!");
            builder.setContentText(message); //TODO get some real times for all tasks
            builder.setSmallIcon(R.drawable.app_logo);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
        }
        //Below Oreo level devices
        else
        {
            //Deprecation does not effects the code, just a warning.
            builder = new NotificationCompat.Builder(this);

            builder.setContentTitle("ProJet!!");
            builder.setContentText(message); //TODO get some real times for all tasks
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.app_logo);
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setContentIntent(pendingIntent);
        }

        //Enabling the notification displaying at the device from now on
        Intent broadcastIntent = new Intent(CreateProjectActivity.this, NotificationsReceiver.class);
        broadcastIntent.putExtra("object", builder.build());

        PendingIntent goBroadcast = PendingIntent.getBroadcast(this, 0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //By AlarmManager, now device can display the sent notifications from the app.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Adjusting Java's Calendar class as needed to display the correct information.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day - 2);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        //Finally, setting the alarm(notification).
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), goBroadcast);
    }
}
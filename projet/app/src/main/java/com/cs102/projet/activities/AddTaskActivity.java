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
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cs102.projet.NotificationsReceiver;
import com.cs102.projet.R;
import com.cs102.projet.interfaces.GetInformations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    RadioGroup prioritiesGroup;
    String selectedPriorityInteger;
    private EditText taskName;
    private EditText taskDescription;
    private EditText editTextTaskDueHour;
    private EditText editTextTaskDueDate;
    Button addTask;
    Button done;
    private String projetName;

    private String day ;
    private String month ;
    private String year ;

    private String hour ;
    private String minute ;

    private String taskNameNotification;

    FirebaseFirestore database;
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    Long currentAmountOfUncompletedTasks;
    int assigner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //Getting the projet name from ProJet page
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        projetName = extras.getString("projetName");

        //Firebase initialize
        database = FirebaseFirestore.getInstance();
        myFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = myFirebaseAuth.getCurrentUser();

        //View initialize
        prioritiesGroup = findViewById(R.id.priorityRadioGroup);
        taskName = findViewById(R.id.editTextTaskName);
        taskDescription = findViewById(R.id.editTextDescription);
        editTextTaskDueHour  = findViewById(R.id.editTextTaskDueHour);
        editTextTaskDueDate = findViewById(R.id.editTextTaskDueDate);
        addTask = findViewById(R.id.buttonAddTask);
        done = findViewById(R.id.buttonDone);

        //message for notification


        // Using DatePicker in order to get valid dates
        // Date ClickListener..
        editTextTaskDueDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePickerDialog();
            }
        });

        // Using TimePicker in order to get valid times..
        // Hour ClickListener...
        editTextTaskDueHour.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showTimePickerDialog();
            }
        });

        //Radio Group input handling
        selectedPriorityInteger = "1";

        prioritiesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // find which radio button is selected
                if(checkedId == R.id.priorityOne) {
                    selectedPriorityInteger = "1";
                } else if(checkedId == R.id.priorityTwo) {
                    selectedPriorityInteger = "2";
                } else {
                    selectedPriorityInteger = "3";
                }
            }
        });

        //Add Task Button onClick()
        addTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //For checking whether another task with the same name exists
                Query myQuery = database.collection("ProJets").document(projetName).collection("Tasks").whereEqualTo("task_name", taskName.getText().toString());

                //Making sure all fields are filled in by if statement
                if (!taskName.getText().toString().equals("") && !taskDescription.getText().toString().equals("") && !editTextTaskDueHour .getText().toString().equals("") && !editTextTaskDueDate.getText().toString().equals(""))
                {
                    myQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            if (queryDocumentSnapshots.isEmpty())
                            {
                                //Now that we are certain such task does not exist, we can continue to initialize the task

                                //Adding the given task properties to the current ProJet's Firebase Database
                                Map<String, String> taskProperties = new HashMap<>();
                                taskProperties.put("task_name", taskName.getText().toString());
                                taskProperties.put("task_description", taskDescription.getText().toString());
                                taskProperties.put("task_due_hour", editTextTaskDueHour.getText().toString());
                                taskProperties.put("task_due_date", editTextTaskDueDate.getText().toString());
                                taskProperties.put("task_owner", "");
                                taskProperties.put("task_priority", selectedPriorityInteger);
                                database.collection("ProJets").document(projetName).collection("Tasks").document(taskName.getText().toString()).set(taskProperties);

                                //Adding the task task_status boolean value
                                Map<String, Boolean> isTaskComplete = new HashMap<>();
                                isTaskComplete.put("task_status", false);
                                database.collection("ProJets").document(projetName).collection("Tasks").document(taskName.getText().toString()).set(isTaskComplete, SetOptions.merge());

                                //Adding the task to the User database as well(to list all tasks in Profile Page)
                                Map<String, DocumentReference> taskReference = new HashMap<>();
                                taskReference.put(taskName.getText().toString(), database.collection("ProJets").document(projetName).collection("Tasks").document(taskName.getText().toString()));
                                database.collection("Users").document(myFirebaseAuth.getCurrentUser().getEmail()).collection("Current Tasks").document(taskName.getText().toString()).set(taskReference);

                                //Getting the current amount of uncompleted tasks at ProJet to increase it
                                database.collection("ProJets").document(projetName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        //Increasing the total_incompleted_tasks value at ProJet database root by 1, since new task added.
                                        currentAmountOfUncompletedTasks = documentSnapshot.getLong("total_uncompleted_tasks");
                                        assigner = 0;
                                        assigner = currentAmountOfUncompletedTasks.intValue();
                                        assigner++;
                                        Integer databaseSender = assigner;

                                        //Now adding the increased value to the ProJet root back again.
                                        Map<String, Integer> increaser = new HashMap<>();
                                        increaser.put("total_uncompleted_tasks", databaseSender);
                                        database.collection("ProJets").document(projetName).set(increaser, SetOptions.merge());
                                    }
                                });
                                // Getting task date and task name separately for the 2 hours left notification..
                                int indexForHour = editTextTaskDueHour.getText().toString().indexOf(':');
                                int indexFirstDate = editTextTaskDueDate.getText().toString().indexOf('/');
                                int indexSecondDate = editTextTaskDueDate.getText().toString().lastIndexOf('/');

                                day = editTextTaskDueDate.getText().toString().substring(0,indexFirstDate);
                                month = editTextTaskDueDate.getText().toString().substring(indexFirstDate + 1, indexSecondDate);
                                year = editTextTaskDueDate.getText().toString().substring(indexSecondDate + 1);

                                hour = editTextTaskDueHour.getText().toString().substring(0,indexForHour);
                                minute = editTextTaskDueHour.getText().toString().substring(indexForHour + 1);

                                taskNameNotification = taskName.getText().toString();

                                int monthN = Integer.parseInt(month);
                                int dayN = Integer.parseInt(day);
                                int hourN = Integer.parseInt(hour);
                                int minuteN = Integer.parseInt(minute);

                                // Sending notification to the current user when 2 hours left for the task...
                                plannedNotification("2 hour left for the task " + taskNameNotification, monthN, dayN, hourN, minuteN );

                                //Emptying the EditTexts and other variables again so that user can add other tasks again
                                taskName.setText("");
                                taskDescription.setText("");
                                editTextTaskDueHour .setText("");
                                editTextTaskDueDate.setText("");
                                prioritiesGroup.check(R.id.priorityOne);
                                selectedPriorityInteger = "1";
                                assigner = 0;

                                Toast.makeText(AddTaskActivity.this, "Task added!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(AddTaskActivity.this, taskName.getText().toString() + " already exists!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //If there is an empty statement at the task properties
                else
                {
                    Toast.makeText(AddTaskActivity.this, "Please fill in all gaps!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Done button onClick()
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

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
        // because calendar start at 0 for counting the months..
        month++;
        String date = dayOfMonth + "/" + month + "/" + year;
        editTextTaskDueDate.setText(date);
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
        editTextTaskDueHour.setText(hourOfDay + ":" +minute);
    }


    public void plannedNotification(String message, int month, int day, int hour, int minute){
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(AddTaskActivity.this, ProjetMainPageActivity.class);
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

        Intent broadcastIntent = new Intent(AddTaskActivity.this, NotificationsReceiver.class);
        broadcastIntent.putExtra("object", builder.build());

        PendingIntent goBroadcast = PendingIntent.getBroadcast(this, 0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Long delay = SystemClock.elapsedRealtime() + 10000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, month -1 );
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute - 2);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), goBroadcast);

//        notificationManager.notify(1, builder.build());
    }
}
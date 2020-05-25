package com.cs102.projet.activities;

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
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cs102.projet.recievers.NotificationsReceiver;
import com.cs102.projet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    //Global Variables
    RadioGroup prioritiesGroup;
    EditText taskName;
    EditText taskDescription;
    EditText editTextTaskDueHour;
    EditText editTextTaskDueDate;
    String projetName;
    String day;
    String month;
    String year;
    String hour;
    String minute;
    String selectedPriorityInteger;
    String taskNameNotification;
    Button addTask;
    Button done;
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

        //On click listener for picking date
        editTextTaskDueDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePickerDialog();
            }
        });

        //On click listener for picking time
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
                //Determine which radio button is selected
                if(checkedId == R.id.priorityOne)
                {
                    selectedPriorityInteger = "1";
                }
                else if(checkedId == R.id.priorityTwo)
                {
                    selectedPriorityInteger = "2";
                }
                else
                {
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
                //Creating a query to check whether another task with the same name exists
                Query myQuery = database.collection("ProJets").document(projetName).collection("Tasks").whereEqualTo("task_name", taskName.getText().toString());

                //Making sure all fields are filled in by if statement
                if (!taskName.getText().toString().equals("") && !taskDescription.getText().toString().equals("") && !editTextTaskDueHour .getText().toString().equals("") && !editTextTaskDueDate.getText().toString().equals(""))
                {
                    myQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {

                            //Now that we are certain such task does not exist, we can continue to initialize the task
                            if (queryDocumentSnapshots.isEmpty())
                            {
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

                                //Getting the current amount of uncompleted tasks at ProJet to increment it
                                database.collection("ProJets").document(projetName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        //Increasing the total_incompleted_tasks value at ProJet database root by 1, since new task added.
                                        currentAmountOfUncompletedTasks = documentSnapshot.getLong("total_uncompleted_tasks");
                                        assigner = 0;
                                        assert currentAmountOfUncompletedTasks != null;
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

                                //Assigning the needed Strings for date/time pickers to work properly.
                                day = editTextTaskDueDate.getText().toString().substring(0,indexFirstDate);
                                month = editTextTaskDueDate.getText().toString().substring(indexFirstDate + 1, indexSecondDate);
                                year = editTextTaskDueDate.getText().toString().substring(indexSecondDate + 1);
                                hour = editTextTaskDueHour.getText().toString().substring(0,indexForHour);
                                minute = editTextTaskDueHour.getText().toString().substring(indexForHour + 1);
                                taskNameNotification = taskName.getText().toString();

                                //Creating another set on integers to safely pass the values to the method plannedNotification (integers below are to assure there won't be any error).
                                int monthN = Integer.parseInt(month);
                                int dayN = Integer.parseInt(day);
                                int hourN = Integer.parseInt(hour);
                                int minuteN = Integer.parseInt(minute);

                                //Sending the notification at planned time
                                plannedNotification("2 hour left for the task " + taskNameNotification, monthN, dayN, hourN, minuteN );

                                //Emptying the EditTexts and other parts again so that user can add other tasks
                                taskName.setText("");
                                taskDescription.setText("");
                                editTextTaskDueHour .setText("");
                                editTextTaskDueDate.setText("");
                                prioritiesGroup.check(R.id.priorityOne);
                                selectedPriorityInteger = "1";
                                assigner = 0;

                                //Informing the user about the task add process is now complete.
                                Toast.makeText(AddTaskActivity.this, "Task added!", Toast.LENGTH_SHORT).show();
                            }
                            //If there is already a task with the desired name,
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

        //Done button onClick(), simply closes the add task page.
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
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
        editTextTaskDueDate.setText(date);
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
        editTextTaskDueHour.setText(assigner);
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
        Intent intent = new Intent(AddTaskActivity.this, ProjetMainPageActivity.class);
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
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.app_logo);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
        }
        //Below Oreo level devices...
        else
        {
            //Deprecation does not effects the code, just a warning.
            builder = new NotificationCompat.Builder(this);

            builder.setContentTitle("ProJet!!");
            builder.setContentText(message);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.app_logo);
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setContentIntent(pendingIntent);
        }

        //Enabling the notification displaying at the device from now on
        Intent broadcastIntent = new Intent(AddTaskActivity.this, NotificationsReceiver.class);
        broadcastIntent.putExtra("object", builder.build());

        PendingIntent goBroadcast = PendingIntent.getBroadcast(this, 0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //By AlarmManager, now device can display the sent notifications from the app.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Adjusting Java's Calendar class as needed to display the correct information.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, month -1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour - 2);
        calendar.set(Calendar.MINUTE, minute);

        //Finally, setting the alarm(notification).
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), goBroadcast);
    }
}
package com.cs102.projet;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyNotificationClass  {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    public MyNotificationClass(){
    }

    public void sendNotification(final String send_email, final String message){
        {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);


                        try {
                            String jsonResponse;

                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setUseCaches(false);
                            con.setDoOutput(true);
                            con.setDoInput(true);

                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            con.setRequestProperty("Authorization", "Basic MjUzYzQ0NjEtZTNkOC00NzA3LThkNTktMzMwNjIzMjU4ODZi");
                            con.setRequestMethod("POST");

                            String strJsonBody = "{"
                                    + "\"app_id\": \"42e66f60-0fa0-42b4-aad6-001620b0df7f\","

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_Id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \""+message+"\"}"
                                    + "}";


                            System.out.println("strJsonBody:\n" + strJsonBody);

                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                            con.setFixedLengthStreamingMode(sendBytes.length);

                            OutputStream outputStream = con.getOutputStream();
                            outputStream.write(sendBytes);

                            int httpResponse = con.getResponseCode();
                            System.out.println("httpResponse: " + httpResponse);

                            if (httpResponse >= HttpURLConnection.HTTP_OK
                                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            } else {
                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            }
                            System.out.println("jsonResponse:\n" + jsonResponse);

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    public void addNotificationsToDatabase(final String send_email, final String message){
        /*int month = Calendar.getInstance().get(Calendar.MONTH) +1;
        String calendar = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+ "/" + month + "/" + Calendar.getInstance().get(Calendar.YEAR) + " "
                + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);*/
        Date currentdate = Calendar.getInstance().getTime();
        DocumentReference notificationRef = database.collection("Users")
                .document(send_email).collection("Notifications").document(currentdate.toString());
        Map<String, String> notificationMap = new HashMap<>();
        notificationMap.put("title", "ProJet!!");
        notificationMap.put("message", message);
        notificationMap.put("time", currentdate.toString());
        notificationRef.set(notificationMap);

        Map<String, Boolean> deleteMap = new HashMap<>();
        deleteMap.put("delete", false);
        notificationRef.set(deleteMap, SetOptions.merge());
    }

    public void addMessageNotificationsToDatabase(final String send_email, final String message){
        /*int month = Calendar.getInstance().get(Calendar.MONTH) +1;
        String calendar = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+ "/" + month + "/" + Calendar.getInstance().get(Calendar.YEAR) + " "
                + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);*/
        Date currentdate = Calendar.getInstance().getTime();
        DocumentReference notificationRef = database.collection("Users")
                .document(send_email).collection("Notifications").document(currentdate.toString());
        Map<String, String> notificationMap = new HashMap<>();
        notificationMap.put("title", "ProJet!!");
        notificationMap.put("message", message);
        notificationMap.put("time", currentdate.toString());
        notificationRef.set(notificationMap);
    }


//    public void plannedNotification(){
//        NotificationCompat.Builder builder;
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent intent = new Intent(HomeActivity.this, SecondPageAcitivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            // For Oreo
//            String channelId = "channelId";
//            String channelName = "channelName";
//            String channelDef = "channelDef";
//            int channelPriority = NotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
//
//            if (channel == null){
//                channel = new NotificationChannel(channelId, channelName, channelPriority);
//                channel.setDescription(channelDef);
//
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            builder = new NotificationCompat.Builder(this, channelId);
//            builder.setContentTitle("ProJet!!");
//            builder.setContentText("You have a new Task..");
//            builder.setAutoCancel(true);
//            builder.setContentIntent(pendingIntent);
//
//        }
//        else{
//            // For Except Oreo
//
//            builder = new NotificationCompat.Builder();
//
//
//            builder.setContentTitle("ProJet!!");
//            builder.setContentText("You have a new Task..");
//            builder.setAutoCancel(true);
//            builder.setPriority(Notification.PRIORITY_HIGH);
//            builder.setContentIntent(pendingIntent);
//        }
//
//        Intent broadcastIntent = new Intent(this, NotificationsReceiver.class);
//        broadcastIntent.putExtra("object", builder.build());
//
//        PendingIntent goBroadcast = PendingIntent.getBroadcast(this, 0,
//                broadcastIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Long delay = SystemClock.elapsedRealtime() + 10000;
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.YEAR, 2020);
//        calendar.set(Calendar.MONTH, 5);
//        calendar.set(Calendar.DAY_OF_MONTH, 22);
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 30);
//
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), goBroadcast);
//
//        notificationManager.notify(1, builder.build());
//    }
}


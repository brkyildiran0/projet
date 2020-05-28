package com.cs102.projet.classes;

import android.os.AsyncTask;
import android.os.StrictMode;

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

public class MyNotificationClass
{
    //Properties
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth myFirebaseAuth;
    FirebaseUser currentUser;

    //Constructors
    public MyNotificationClass() { }

    //Methods

    /**
     * This method is responsible for sending the notifications to users via OneSignal system
     * @param send_email is the email of the user that is going to receive the notification
     * @param message is the String message sent in the notification
     */
    public void sendNotification(final String send_email, final String message)
    {
        {
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    //Getting the version level of the Android device
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;

                    //Above OREO level devices,
                    if (SDK_INT > 8)
                    {
                        //Pre-conditions of OneSignal
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        //Here, connection with OneSignal is built and handled as follows,
                        try
                        {
                            String jsonResponse;

                            //Connecting to OneSignal
                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setUseCaches(false);
                            con.setDoOutput(true);
                            con.setDoInput(true);

                            //Creating requests via HttpURLConnection object
                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            con.setRequestProperty("Authorization", "Basic MjUzYzQ0NjEtZTNkOC00NzA3LThkNTktMzMwNjIzMjU4ODZi");
                            con.setRequestMethod("POST");

                            //Creating the JSON body for the notification to be read by OneSignal
                            String strJsonBody = "{"
                                    + "\"app_id\": \"42e66f60-0fa0-42b4-aad6-001620b0df7f\","

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_Id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \""+message+"\"}"
                                    + "}";


                            System.out.println("strJsonBody:\n" + strJsonBody);

                            //Byte array to send the bytes
                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                            con.setFixedLengthStreamingMode(sendBytes.length);

                            //Creating an OutputStream object to send the array above
                            OutputStream outputStream = con.getOutputStream();
                            outputStream.write(sendBytes);

                            //Getting the httpResponse as an int
                            int httpResponse = con.getResponseCode();
                            System.out.println("httpResponse: " + httpResponse);

                            //Handling the cases according to the httpResponse int value
                            if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST)
                            {
                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            }
                            else
                            {
                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            }
                            System.out.println("jsonResponse:\n" + jsonResponse);

                        }
                        catch (Throwable t)
                        {
                            t.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * This method adds the notifications that has been created to the Firebase Database
     * @param send_email the email of the receiver of notification
     * @param message the String message of the notification
     */
    public void addNotificationsToDatabase(final String send_email, final String message)
    {
        //Getting the current date
        Date currentdate = Calendar.getInstance().getTime();

        //DocRef for code clarity
        DocumentReference notificationRef = database.collection("Users").document(send_email).collection("Notifications").document(currentdate.toString());

        //Adding the values to a HashMap to later send them to the database
        Map<String, String> notificationMap = new HashMap<>();
        notificationMap.put("title", "ProJet!!");
        notificationMap.put("message", message);
        notificationMap.put("time", currentdate.toString());
        notificationRef.set(notificationMap);

        //Adding the values to a HashMap to later send them to the database
        Map<String, Boolean> deleteMap = new HashMap<>();
        deleteMap.put("delete", false);
        notificationRef.set(deleteMap, SetOptions.merge());
    }

    /**
     * This method adds the MESSAGE notifications that has been created to the Firebase Database
     * @param send_email the email of the receiver of notification
     * @param message the String message of the notification
     */
    public void addMessageNotificationsToDatabase(final String send_email, final String message)
    {
        //Getting the current date
        Date currentdate = Calendar.getInstance().getTime();

        //DocRef for code clarity
        DocumentReference notificationRef = database.collection("Users").document(send_email).collection("Notifications").document(currentdate.toString());

        //Adding the values to a HashMap to later send them to the database
        Map<String, String> notificationMap = new HashMap<>();
        notificationMap.put("title", "ProJet!!");
        notificationMap.put("message", message);
        notificationMap.put("time", currentdate.toString());
        notificationRef.set(notificationMap);
    }
}


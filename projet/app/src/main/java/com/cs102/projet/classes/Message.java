package com.cs102.projet.classes;

import android.graphics.Color;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Message {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth myFirebaseAuth;
    private FirebaseUser currentUser;
    private String coming_from;
    private String message;
    private Timestamp time;
    private int color;

    public Message() {
    }

    public Message(String coming_from, String message, Timestamp time) {
        this.coming_from = coming_from;
        this.message = message;
        this.time = time;
        this.color = Color.GREEN;
    }

    public String getComing_from() {
        return coming_from;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTime() {
        return time;
    }

    public int getColor(){ return color; }
}

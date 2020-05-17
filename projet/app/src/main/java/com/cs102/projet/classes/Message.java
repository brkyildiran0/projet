package com.cs102.projet.classes;

import com.google.firebase.Timestamp;

public class Message {

    private String coming_from;
    private String message;
    private Timestamp time;

    public Message() {
    }

    public Message(String coming_from, String message, Timestamp time) {
        this.coming_from = coming_from;
        this.message = message;
        this.time = time;
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
}

package com.cs102.projet.classes;

import com.google.firebase.firestore.FirebaseFirestore;

public class Member
{
    //Properties
    private String user_name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Constructors
    public Member(){ }

    public Member(String user_name)
    {
        this.user_name = user_name;
    }

    public String getUser_name()
    {
        return user_name;
    }
}

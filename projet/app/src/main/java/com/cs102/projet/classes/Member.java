package com.cs102.projet.classes;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Member {
    private String user_name;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Member() {
    }

    public Member(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

}

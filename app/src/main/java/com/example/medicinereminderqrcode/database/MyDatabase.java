package com.example.medicinereminderqrcode.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyDatabase {

    private final DatabaseReference myDatabase;

    public MyDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myDatabase = firebaseDatabase.getReference();
    }

    public DatabaseReference getDatabase(){
        return myDatabase;
    }

    //clear database
    public void clear(Boolean b) {
        if(b){
            myDatabase.removeValue();
        }
    }

}

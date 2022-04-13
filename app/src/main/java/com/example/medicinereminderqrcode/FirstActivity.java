package com.example.medicinereminderqrcode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.medicinereminderqrcode.database.MyDatabase;

public class FirstActivity extends AppCompatActivity {

    Button button1,button2;
    MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        myDatabase = new MyDatabase();
        myDatabase.clear(false);
        setToolbar();
        setButton();
    }
    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setButton(){
        button1 = findViewById(R.id.user1_button);
        button2 = findViewById(R.id.user2_button);
        button1.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("username","user1");
            startActivity(intent);
        });
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("username","user2");
            startActivity(intent);
        });
    }
}
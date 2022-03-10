package com.example.appstart1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.NAME_STR);
        String message2 = intent.getStringExtra(MainActivity.BIRTH_STR);
        System.out.printf("DisplayMessage message :%s\n", message);
        System.out.printf("DisplayMessage message2:%s\n", message2);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        textView.setText(message);
        textView2.setText(message2);
    }
}
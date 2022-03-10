package com.example.appstart1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.EditText;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    public static final String NAME_STR ="com.example.appstart1.MESSAGE";
    public static final String BIRTH_STR ="com.example.appstart1.MESSAGE2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /* Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText editText2 = (EditText) findViewById(R.id.editTextTextPersonName1);

        String message = editText.getText().toString();
        String message2 = editText2.getText().toString();
        System.out.printf("main message :%s\n", message);
        System.out.printf("main message2:%s\n", message2);
        intent.putExtra(NAME_STR, message);
        intent.putExtra(BIRTH_STR, message2);
        startActivity(intent);
    }

    public void ScanBluetooth(View view) {
        Intent intent = new Intent(this, BleScannedListActivity.class);
        startActivity(intent);
    }

}
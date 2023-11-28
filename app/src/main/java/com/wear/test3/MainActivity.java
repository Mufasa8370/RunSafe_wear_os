package com.wear.test3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.wear.test3.run.ActivityStartRun;

public class MainActivity extends AppCompatActivity {
    ImageButton button_run, button_settings, button_training,button_history;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_history = findViewById(R.id.button_history);
        button_run = findViewById(R.id.button_run);
        button_settings = findViewById(R.id.button_settings);
        button_training = findViewById(R.id.button_training);

        button_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityStartRun.class);
                startActivity(intent);
            }
        });
    }
}
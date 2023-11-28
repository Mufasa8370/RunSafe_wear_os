package com.wear.test3.run;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.wear.test3.R;

public class ActivityStartRun extends AppCompatActivity {
    ImageButton start_run;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);
        start_run = findViewById(R.id.start_run);

        start_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityStartRun.this, ActivityRun.class);
                startActivity(intent);
            }
        });

    }
}
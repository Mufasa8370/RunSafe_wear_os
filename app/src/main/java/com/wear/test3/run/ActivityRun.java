package com.wear.test3.run;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wear.test3.R;

public class ActivityRun extends AppCompatActivity implements DistanceCalculationService.DistanceUpdateListener {

    Chronometer chronometer;
    TextView distanceTextView;

    ImageButton pause_run, stop_run;
    long timeWhenStopped = 0;

    boolean enPAuse;
    private DistanceCalculationService distanceService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DistanceCalculationService.LocalBinder binder = (DistanceCalculationService.LocalBinder) service;
            distanceService = binder.getService();
            distanceService.setDistanceUpdateListener(ActivityRun.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            distanceService = null;
            isBound = false;
        }
    };

    private Handler handler = new Handler();
    private static final long UPDATE_INTERVAL = 1000; // 1 seconde

    private Runnable updateDistanceTask = new Runnable() {
        @Override
        public void run() {
            if (distanceService != null) {
                // Appeler une méthode dans le service pour obtenir la distance mise à jour
                float distance = distanceService.getDistanceCovered();
                // Mettre à jour le TextView avec la distance
                updateDistanceTextView(distance);
            }

            // Programmer la prochaine exécution après une seconde
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        pause_run = findViewById(R.id.pause_run);
        chronometer = findViewById(R.id.chrono_run);
        distanceTextView = findViewById(R.id.distance_run); // Ajout de cette ligne

        // Lier le service
        Intent intent = new Intent(this, DistanceCalculationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        //Lance le Chrono
        lunchChrono();
        pause_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Met en pause le chrono si il est pas en pause
                if(!enPAuse){
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    pause_run.setImageResource(R.drawable.play_y);
                    enPAuse = true;
                }
                //redémarre le chrono
                else{
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                    pause_run.setImageResource(R.drawable.pause_y);
                    enPAuse = false;
                }
            }
        });
    }

    @Override
    public void onDistanceUpdate(float distance) {
        // Appelé lorsque la distance est mise à jour
        // Mettez à jour votre interface utilisateur avec la distance
        // distance est la nouvelle valeur de la distance parcourue
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBound) {
            // Arrêter la tâche de mise à jour périodique de la distance
            handler.removeCallbacks(updateDistanceTask);
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    public void lunchChrono(){
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();

        // Démarrer la tâche de mise à jour périodique de la distance
        handler.postDelayed(updateDistanceTask, UPDATE_INTERVAL);
    }

    private void updateDistanceTextView(float distance) {
        // Mettre à jour le TextView avec la distance
        distanceTextView.setText(String.format("%.2f", distance) + " m");
    }
}
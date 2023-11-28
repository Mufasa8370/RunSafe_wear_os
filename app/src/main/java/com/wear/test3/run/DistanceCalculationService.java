package com.wear.test3.run;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class DistanceCalculationService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] lastAccelerometerData;
    private float distanceCovered;

    // Interface pour la communication avec l'activité
    public interface DistanceUpdateListener {
        void onDistanceUpdate(float distance);
    }

    private DistanceUpdateListener distanceUpdateListener;

    // Binder donné aux clients
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        DistanceCalculationService getService() {
            // Retourne cette instance de LocalService pour que les clients puissent appeler des méthodes publiques
            return DistanceCalculationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        lastAccelerometerData = new float[3];
        distanceCovered = 0.0f;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Retourne le canal de communication au service.
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Calcule la distance en utilisant les données de l'accéléromètre
            calculateDistance(event.values[0], event.values[1], event.values[2]);
        }
    }

    private void calculateDistance(float x, float y, float z) {
        // Estimation simple de la distance en utilisant la norme des données de l'accéléromètre
        float deltaX = x - lastAccelerometerData[0];
        float deltaY = y - lastAccelerometerData[1];
        float deltaZ = z - lastAccelerometerData[2];

        float deltaDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        distanceCovered += deltaDistance;

        // Met à jour les dernières données de l'accéléromètre
        lastAccelerometerData[0] = x;
        lastAccelerometerData[1] = y;
        lastAccelerometerData[2] = z;

        // Utilisez la valeur 'distanceCovered' comme nécessaire

        // Notifie l'activité de la mise à jour de la distance
        notifyDistanceUpdate(distanceCovered);
    }

    // Méthode pour définir le listener d'interface depuis l'activité
    public void setDistanceUpdateListener(DistanceUpdateListener listener) {
        this.distanceUpdateListener = listener;
    }

    // Méthode pour obtenir la distance parcourue
    public float getDistanceCovered() {
        return distanceCovered;
    }

    private void notifyDistanceUpdate(float distance) {
        if (distanceUpdateListener != null) {
            distanceUpdateListener.onDistanceUpdate(distance);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé dans cet exemple
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
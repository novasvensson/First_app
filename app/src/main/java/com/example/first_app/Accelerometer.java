package com.example.first_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    TextView xVal, yVal, zVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        xVal = (TextView) findViewById(R.id.xVal);
        yVal = (TextView) findViewById(R.id.yVal);
        zVal = (TextView) findViewById(R.id.zVal);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(Accelerometer.this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        xVal.setText("x-axiz: " + sensorEvent.values[0]);
        yVal.setText("y-axiz: " + sensorEvent.values[1]);
        zVal.setText("z-axiz: " + sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
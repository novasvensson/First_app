package com.example.first_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private ImageView normalWater;
    private ImageView splashWater;
    TextView xVal, yVal, zVal, funText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        xVal = (TextView) findViewById(R.id.xVal);
        yVal = (TextView) findViewById(R.id.yVal);
        zVal = (TextView) findViewById(R.id.zVal);
        funText = (TextView) findViewById(R.id.funText);

        splashWater = (ImageView) findViewById(R.id.imageView);
        normalWater = (ImageView) findViewById(R.id.imageView2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(Accelerometer.this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        xVal.setText("x-axiz: " + sensorEvent.values[0]);
        yVal.setText("y-axiz: " + sensorEvent.values[1]);
        zVal.setText("z-axiz: " + sensorEvent.values[2]);

        if(sensorEvent.values[1] > 8 && sensorEvent.values[1] < 11){
            funText.setText("Bra balans, du står upp");
            splashWater.setImageAlpha(255);
            normalWater.setImageAlpha(0);
        } else {
            funText.setText("Ställ dig upp!");
            splashWater.setImageAlpha(0);
            normalWater.setImageAlpha(255);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
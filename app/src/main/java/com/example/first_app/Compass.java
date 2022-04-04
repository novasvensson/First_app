package com.example.first_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magSensor;
    private Sensor accSensor;
    private Sensor rotationV;

    boolean haveSensor = false;
    boolean haveSensor2 = false;

    private ImageView compassImage;
    private ImageView cheese;
    TextView textView;

    int azimuth;

    float[] lastAcc = new float[3];
    float[] lastMag = new float[3];
    private float[] orientation = new float[3];
    private float[] rotMatrix = new float[9];

    private boolean lastAccSet = false;
    private boolean lastMagSet = false;


    // executing at the App startup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassImage = (ImageView) findViewById(R.id.compass_img);
        cheese = (ImageView) findViewById(R.id.imageView3);
        textView = (TextView) findViewById(R.id.textViewCompass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        start();
    }

    public void start() {
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)){
                noSensorAlert();
            } else {
                accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_UI);
            }
        } else {
            rotationV = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = sensorManager.registerListener(this, rotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void noSensorAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("The device does not support the compass")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop(){
        if(haveSensor && haveSensor2) {
            sensorManager.unregisterListener(this, accSensor);
            sensorManager.unregisterListener(this, magSensor);
        } else {
            if(haveSensor) {
                sensorManager.unregisterListener(this, rotationV);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    SensorEventListener sensorEventListenerAccelerometer = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotMatrix, sensorEvent.values);
                azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotMatrix, orientation)[0]) + 360) % 360;
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(sensorEvent.values, 0, lastAcc, 0, sensorEvent.values.length);
                lastAccSet = true;
            } else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(sensorEvent.values, 0, lastMag, 0, sensorEvent.values.length);
                lastMagSet = true;
            }

            if(lastAccSet && lastMagSet) {
                SensorManager.getRotationMatrix(rotMatrix, null, lastAcc, lastMag);
                SensorManager.getOrientation(rotMatrix, orientation);
                azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotMatrix, orientation)[0]) + 360) % 360;
            }

            azimuth = Math.round(azimuth);
            compassImage.setRotation(-azimuth);

            String where = "NW";
            float i = 255;

            if(azimuth >= 350 || azimuth <=10) {
                where = "N";
                i = 255;
            }
            if(azimuth < 350 && azimuth > 280) {
                where = "NW";
                i = 255;
            }
            if(azimuth <= 280 && azimuth > 260) {
                where = "W";
                i = 255;
            }
            if(azimuth <= 260 && azimuth > 190) {
                where = "SW";
                i = 255;
            }
            if(azimuth <= 190 && azimuth > 170) {
                where = "S";
                i = 255;
            }
            if(azimuth <= 170 && azimuth > 100) {
                where = "SE";
                i = 0;
            }
            if(azimuth <= 100 && azimuth > 80) {
                where = "E";
                i = 0;
            }
            if(azimuth <= 80 && azimuth > 10) {
                where = "NE";
                i = 0;
            }

            String text = azimuth + "° " + where;
            textView.setText(text);
            cheese.setImageAlpha((int) i);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // executed when a sensor change its state
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    // executed when the sensor change accuracy
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
package com.example.first_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {

    ImageView compassImage;
    ImageView cheese;
    TextView textView;
    Vibrator vibrator;

    private SensorManager sensorManager;
    private Sensor magSensor;
    private Sensor accSensor;
    private Sensor rotationV;

    boolean haveSensor = false;
    boolean haveSensor2 = false;

    int azimuth;

    private float[] lastAcc = new float[3];
    private float[] lastMag = new float[3];
    float[] orientation = new float[3];
    float[] rotMatrix = new float[9];

    private boolean lastAccSet = false;
    private boolean lastMagSet = false;

    // static final float LOWPASS_FACTOR = 0.25f;// Used for low pass filtering


    // executing at the App startup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassImage = (ImageView) findViewById(R.id.compass_img);
        cheese = (ImageView) findViewById(R.id.imageView3);
        textView = (TextView) findViewById(R.id.textViewCompass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

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

    // executed when a sensor change its state
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotMatrix, sensorEvent.values);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotMatrix, orientation)[0]) + 360) % 360;
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, lastAcc, 0, sensorEvent.values.length);
            // System.arraycopy(lowPass(event.values, mLastAccelerometer), 0, mLastAccelerometer, 0, event.values.length);
            lastAccSet = true;
        } else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, lastMag, 0, sensorEvent.values.length);
            // System.arraycopy(lowPass(event.values, mLastMagnetometer), 0, mLastMagnetometer, 0, event.values.length);
            lastMagSet = true;
        }

        if(lastAccSet && lastMagSet) {
            SensorManager.getRotationMatrix(rotMatrix, null, lastAcc, lastMag);
            SensorManager.getOrientation(rotMatrix, orientation);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotMatrix, orientation)[0]) + 360) % 360;
        }

        azimuth = Math.round(azimuth);
        compassImage.setRotation(-azimuth);

        String where = "NV";
        float i = 0;

        if(azimuth >= 350 || azimuth <=10) {
            where = "N";
            i = 0;
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }
        if(azimuth < 350 && azimuth > 280) {
            where = "NV";
            i = 0;
        }
        if(azimuth <= 280 && azimuth > 260) {
            where = "V";
            i = 0;
        }
        if(azimuth <= 260 && azimuth > 190) {
            where = "SV";
            i = 0;
        }
        if(azimuth <= 190 && azimuth > 170) {
            where = "S";
            i = 0;
        }
        if(azimuth <= 170 && azimuth > 100) {
            where = "SO";
            i = 255;
        }
        if(azimuth <= 100 && azimuth > 80) {
            where = "Ö";
            i = 255;
        }
        if(azimuth <= 80 && azimuth > 10) {
            where = "NO";
            i = 255;
        }

        textView.setText(azimuth + "° " + where);
        cheese.setImageAlpha((int) i);
    }

    // executed when the sensor change accuracy
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
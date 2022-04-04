package com.example.first_app;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Compass extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magCompassSensor;
    private Sensor accCompassSensor;

    private ImageView compassImage;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float correctAzimuth = 0f;


    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassImage = (ImageView) findViewById(R.id.compass_img);

        // TextView that will display the degree
        textView = (TextView) findViewById(R.id.textViewCompass);
        textView2 = (TextView) findViewById(R.id.textView2);

        // initialize your android device sensor capabilities
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magCompassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accCompassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magCompassSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accCompassSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        final float alpha = 0.97f;
        synchronized (this) {
            if(sensorEvent.sensor.getType() ==  Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1-alpha) * sensorEvent.values[0];
                mGravity[1] = alpha * mGravity[1] + (1-alpha) * sensorEvent.values[1];
                mGravity[2] = alpha * mGravity[2] + (1-alpha) * sensorEvent.values[2];
            }
            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGravity[0] = alpha * mGeomagnetic[0] + (1-alpha) * sensorEvent.values[0];
                mGravity[1] = alpha * mGeomagnetic[1] + (1-alpha) * sensorEvent.values[1];
                mGravity[2] = alpha * mGeomagnetic[2] + (1-alpha) * sensorEvent.values[1];
            }
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if(success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;

                Animation animation = new RotateAnimation(-correctAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                correctAzimuth = azimuth;

                animation.setDuration(500);
                animation.setRepeatCount(0);
                animation.setFillAfter(true);

                compassImage.setAnimation(animation);

                textView.setText((int) correctAzimuth);
                textView2.setText((int) azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
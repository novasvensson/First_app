package com.example.first_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Accelerometer button*/
    public void startAccelerometer(View view) {
        Intent intent = new Intent(this, Accelerometer.class);
        startActivity(intent);
    }

    /** Called when the user taps the Accelerometer button*/
    public void startCompass(View view) {
        Intent intent = new Intent(this, Compass.class);
        startActivity(intent);
    }
}
package com.example.flashlightusingjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public final int MIN_TIME_BETWEEN_SHAKE = 1000; // in millisec
    private long lastShakeTime = 0;
    private TextView textView;
    private static final String TAG = "FlashLight";
    private Float shakeThreshold = 50.0f;
    private boolean isFlashLightOn = false;

    public void torchToggle(String command) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            if (cameraManager != null) {
                cameraId = cameraManager.getCameraIdList()[0];
            }

            if(cameraManager != null) {
                if (command.equals("on")) {
                    cameraManager.setTorchMode(cameraId, true);
                } else {
                    cameraManager.setTorchMode(cameraId, false);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        utility = new Utility(this);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acc = sensorEvent.values[0];
                    float y_acc = sensorEvent.values[1];
                    float z_acc = sensorEvent.values[2];
                    double acceleration = Math.sqrt(Math.pow(x_acc,2) + Math.pow(y_acc,2) + Math.pow(z_acc,2));
                    long currTime = System.currentTimeMillis();

                    if(acceleration > shakeThreshold && (currTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKE) {
                        textView.setText("Yes, Shaking ");
                        Log.d(TAG, "FlashLight:: toggling FlashLight ");
                        if(!isFlashLightOn) { // turn ON if it was ON
                            try {
                                torchToggle("on");
                                isFlashLightOn = true;
                                Log.d(TAG, "FlashLight::Turning ON flashlight");
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        } else { // turn OFF if it was ON
                            try {
                                torchToggle("off");
                                isFlashLightOn = false;
                                Log.d(TAG, "FlashLight::Turning OFF flashlight");
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        lastShakeTime = currTime;
                    } else {
                        textView.setText("No, NOT Shaking ");
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }
}
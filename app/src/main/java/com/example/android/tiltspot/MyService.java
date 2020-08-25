package com.example.android.tiltspot;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service implements SensorEventListener {
    public MyService() {
    }

    public SensorManager mSensorManager;
    public float[] mRotmeterData = new float[3];
    public Sensor mSensorRotmeter;


    public int onStartCommand(Intent intent, int flags, int startId) {




        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ROTATION_VECTOR:
                mRotmeterData = sensorEvent.values.clone();
                break;

            default:
                return;
        }

        float[] rotationMatrix = new float[9];
//        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,null, mAccelerometerData, mMagnetometerData );
        SensorManager.getRotationMatrixFromVector(rotationMatrix, mRotmeterData );

        float orientationValues[] = new float[3];
//        if (rotationOK) {
        SensorManager.getOrientation(rotationMatrix, orientationValues);
//        }
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];


    }

    @Override
    public void onStart(Intent intent, int startid) {
        if (mSensorRotmeter != null) {
            mSensorManager.registerListener(this, mSensorRotmeter,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

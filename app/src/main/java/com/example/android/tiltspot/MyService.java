package com.example.android.tiltspot;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyService extends IntentService implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorRotmeter;


    private float[] mRotmeterData = new float[3];


    private List<float[]> mRotHist = new ArrayList<float[]>();
    private int mRotHistIndex;
    private int mHistoryMaxLength = 40;
    float[] mRotationMatrix = new float[9];

    private static final float VALUE_DRIFT = 0.05f;





    public static float azimuth;
    public static float roll;
    public static float pitch;

    public MyService() {
        super("Hello");
    }


    @Override
    public void onCreate() {

        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);


        mSensorRotmeter = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR );


        if (mSensorRotmeter != null) {
            mSensorManager.registerListener(this, mSensorRotmeter,
                    SensorManager.SENSOR_DELAY_GAME);
        }


    }





    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
        SensorManager.getOrientation(rotationMatrix, orientationValues);



        this.azimuth = orientationValues[0];
        this.pitch = orientationValues[1];
        this.roll = orientationValues[2];

        MainActivity.mTextSensorAzimuth.setText(getResources().getString(
                R.string.value_format, azimuth*90/1.57));
        MainActivity.mTextSensorPitch.setText(getResources().getString(
                R.string.value_format, pitch*90/1.57));
        MainActivity.mTextSensorRoll.setText(getResources().getString(
                R.string.value_format, roll*90/1.57));



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onStop() {

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }


    protected void ondestroy()
    {
        mSensorManager.unregisterListener(this);

    }




}

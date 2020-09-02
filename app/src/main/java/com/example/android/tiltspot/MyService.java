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
import android.widget.Button;

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

    static Button listView;


    public static float azimuth;
    public float azimuthTemp;

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



        this.azimuthTemp = (float)(orientationValues[0]);
        if (this.azimuthTemp < 0) {
            this.azimuthTemp += 2*Math.PI;
        }
        this.azimuth = (float) (this.azimuthTemp );

        this.pitch = (float) (orientationValues[1]);
        this.roll = (float) (orientationValues[2]);


        MainActivity.mTextSensorAzimuth.setText("A:"+getResources().getString(R.string.value_format, this.azimuth*90/1.57)+", "
                +"P:"+getResources().getString(R.string.value_format, this.pitch*90/1.57)+", "
                +"R:"+getResources().getString(R.string.value_format, this.roll*90/1.57));
//        MainActivity.mTextSensorPitch.setText(getResources().getString(
//                R.string.value_format, pitch*90/1.57));
//        MainActivity.mTextSensorRoll.setText(getResources().getString(
//                R.string.value_format, roll*90/1.57));



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onStop() {

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {




        return START_STICKY_COMPATIBILITY;
    }

    protected void ondestroy()
    {

        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }




}

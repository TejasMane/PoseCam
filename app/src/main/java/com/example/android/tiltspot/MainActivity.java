/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.tiltspot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ir.mahdi.mzip.zip.ZipArchive;







public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private String currentPhotoPath;
    private ProgressBar spinner;

    private String path;




    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;
    private Sensor mSensorRotmeter;

    // TextViews to display current sensor values.
    public static TextView mTextSensorAzimuth;
    public static TextView mTextSensorPitch;
    public static TextView mTextSensorRoll;

    private float[] mRotmeterData = new float[3];


    private List<float[]> mRotHist = new ArrayList<float[]>();
    private int mRotHistIndex;
    // Change the value so that the azimuth is stable and fit your requirement
    private int mHistoryMaxLength = 40;
    float[] mRotationMatrix = new float[9];
    // the direction of the back camera, only valid if the device is tilted up by
// at least 25 degrees.


    // Very small values for the accelerometer (on all three axes) should
    // be interpreted as 0. This value is the amount of acceptable
    // non-zero drift.
    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lock the orientation to portrait (for now)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTextSensorAzimuth = (TextView) findViewById(R.id.value_azimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.value_pitch);
        mTextSensorRoll = (TextView) findViewById(R.id.value_roll);

        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);

        mSensorRotmeter = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR );



        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);


        final Camera[] camera = new Camera[1];







        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                spinner.setVisibility(View.VISIBLE);


                new Thread(new Runnable() {
                    public void run() {

                        ZipArchive zipArchive = new ZipArchive();
                        zipArchive.zip(Environment.getExternalStorageDirectory().getPath()  + "/Android/data/com.example.android.tiltspot/files/Pictures/",Environment.getExternalStorageDirectory().getPath()  + "/PoseCamFiles.zip","");
                        String folderPath = Environment.getExternalStorageDirectory().getPath()  + "/";
                        String zipFileName = "PoseCamFiles.zip";
                        File zipFile = new File (folderPath + zipFileName);
                        Uri zipURI = FileProvider.getUriForFile(MainActivity.this, "com.example.android.tiltspot.fileprovider", zipFile );


                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, zipURI);

                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sendIntent.setType("application/zip");
                        startActivity(Intent.createChooser(sendIntent, "Share..."));
                        spinner.setVisibility(View.INVISIBLE);
                    }
                }).start();


//
            }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                String azi = (String) mTextSensorAzimuth.getText();
//                int aziI = (int) Double.parseDouble(azi);
//                azi = Integer.toString(aziI);
//
//                String Pitch = (String) mTextSensorPitch.getText();
//                int PitchI = (int) Double.parseDouble(Pitch);
//                Pitch = Integer.toString(PitchI);
//
//                String Roll = (String) mTextSensorRoll.getText();
//                int RollI = (int) Double.parseDouble(Roll);
//                Roll = Integer.toString(RollI);
//
//                String fileName = "angles_"+azi+"_"+Pitch+"_"+Roll;
//                File StorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                try {
////                    File ImageFile = File.createTempFile(fileName, ".png",StorageDirectory);
//
//                    File image = new File(StorageDirectory, fileName + ".png");
//                    currentPhotoPath = image.getAbsolutePath();
//
//
//
//                    Uri Imageuri = FileProvider.getUriForFile(MainActivity.this, "com.example.android.tiltspot.fileprovider", image);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra("android.intent.extra.quickCapture",true);
//
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT,Imageuri );
//                    setResult(RESULT_OK, intent);
//
//                    startActivityForResult(intent, 1);
//
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                new Thread(new Runnable() {
                    public void run() {
                        openCamActivity();

                    }
                }).start();


            }
        });






        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new Thread(new Runnable() {
                    public void run() {
                        openActivity2();

                    }
                }).start();






            }
        });



    }


    public void openActivity2()
    {
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);


    }

    public void openCamActivity()
    {
        Intent intent = new Intent(this, CamActivity.class);
        startActivity(intent);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK)
        {
//            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//            ImageView imageView = findViewById(R.id.imageView);
//            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }


        if (mSensorRotmeter != null) {
            mSensorManager.registerListener(this, mSensorRotmeter,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }





    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
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
        mTextSensorAzimuth.setText(getResources().getString(
                R.string.value_format, azimuth*90/1.57));
        mTextSensorPitch.setText(getResources().getString(
                R.string.value_format, pitch*90/1.57));
        mTextSensorRoll.setText(getResources().getString(
                R.string.value_format, roll*90/1.57));






    }


    private void clearRotHist()
    {
        mRotHist.clear();
        mRotHistIndex = 0;
    }

    private void setRotHist()
    {
        float[] hist = mRotationMatrix.clone();
        if (mRotHist.size() == mHistoryMaxLength)
        {
            mRotHist.remove(mRotHistIndex);
        }
        mRotHist.add(mRotHistIndex++, hist);
        mRotHistIndex %= mHistoryMaxLength;
    }

    private float findFacing()
    {
        float[] averageRotHist = average(mRotHist);
        return (float) Math.atan2(-averageRotHist[2], -averageRotHist[5]);
    }

    public float[] average(List<float[]> values)
    {
        float[] result = new float[9];
        for (float[] value : values)
        {
            for (int i = 0; i < 9; i++)
            {
                result[i] += value[i];
            }
        }

        for (int i = 0; i < 9; i++)
        {
            result[i] = result[i] / values.size();
        }

        return result;
    }












    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
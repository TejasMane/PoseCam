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







public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath;
    private ProgressBar spinner;

    private String path;





    // TextViews to display current sensor values.
    public static TextView mTextSensorAzimuth;
    public static TextView mTextSensorPitch;
    public static TextView mTextSensorRoll;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lock the orientation to portrait (for now)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTextSensorAzimuth = (TextView) findViewById(R.id.value_azimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.value_pitch);
        mTextSensorRoll = (TextView) findViewById(R.id.value_roll);



        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);


        final Camera[] camera = new Camera[1];



        startService(new Intent(this, MyService.class));




        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                spinner.setVisibility(View.VISIBLE);


                new Thread(new Runnable() {
                    public void run() {

                        ZipArchive zipArchive = new ZipArchive();
                        zipArchive.zip(Environment.getExternalStorageDirectory().getPath()  + "/Pictures/PoseCam/",Environment.getExternalStorageDirectory().getPath()  + "/PoseCamFiles.zip","");
                        String folderPath = Environment.getExternalStorageDirectory().getPath()  + "/";
                        String zipFileName = "PoseCamFiles.zip";
                        File zipFile = new File (folderPath + zipFileName);
                        Uri zipURI = FileProvider.getUriForFile(MainActivity.this, "com.example.android.tiltspot.fileprovider", zipFile );


                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, zipURI);

                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sendIntent.setType("application/zip");
                        startActivityForResult(Intent.createChooser(sendIntent, "Share..."), 512);
                        spinner.setVisibility(View.INVISIBLE);



                    }
                }).start();


//
            }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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

        if(requestCode == 512) {
            try
            {
                File file = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/PoseCamFiles.zip");
                if(file.exists())
                    file.delete();
            }
            catch (Exception e)
            {
            }

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
//        if (mSensorAccelerometer != null) {
//            mSensorManager.registerListener(this, mSensorAccelerometer,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        if (mSensorMagnetometer != null) {
//            mSensorManager.registerListener(this, mSensorMagnetometer,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
//
//
//        if (mSensorRotmeter != null) {
//            mSensorManager.registerListener(this, mSensorRotmeter,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }





    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        // Unregister all sensor listeners in this callback so they don't
//        // continue to use resources when the app is stopped.
//        mSensorManager.unregisterListener(this);
//    }

//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//
//        float azimuth = MyService.azimuth;
//        float pitch = MyService.pitch;
//        float roll = MyService.roll;
//
//        mTextSensorAzimuth.setText(getResources().getString(
//                R.string.value_format, azimuth*90/1.57));
//        mTextSensorPitch.setText(getResources().getString(
//                R.string.value_format, pitch*90/1.57));
//        mTextSensorRoll.setText(getResources().getString(
//                R.string.value_format, roll*90/1.57));
//
//
//
//
//
//
//    }










    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//    }
}
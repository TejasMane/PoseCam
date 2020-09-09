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
package com.example.android.tiltspot

import android.Manifest.permission
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.android.tiltspot.MainActivity
import ir.mahdi.mzip.zip.ZipArchive
import java.io.File

class MainActivity : AppCompatActivity() {
    private val currentPhotoPath: String? = null
    private var spinner: ProgressBar? = null
    private val path: String? = null
    private var isInPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTextSensorAzimuth = findViewById<View>(R.id.textView) as TextView
        if (!isInPermission) {
            isInPermission = true
            ActivityCompat.requestPermissions(this, PERMS_TAKE_PICTURE,
                    RESULT_PERMS_INITIAL)
        }


        // Lock the orientation to portrait (for now)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


//        mTextSensorAzimuth = (TextView) findViewById(R.id.value_azimuth);
//        mTextSensorPitch = (TextView) findViewById(R.id.value_pitch);
//        mTextSensorRoll = (TextView) findViewById(R.id.value_roll);
        spinner = findViewById<View>(R.id.progressBar) as ProgressBar
        spinner!!.visibility = View.GONE
        val camera = arrayOfNulls<Camera>(1)
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PoseCam")
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        startService(Intent(this, MyService::class.java))
        findViewById<View>(R.id.button2).setOnClickListener {
            spinner!!.visibility = View.VISIBLE
            Thread {
                val zipArchive = ZipArchive()
                ZipArchive.zip(Environment.getExternalStorageDirectory().path + "/Pictures/PoseCam/", Environment.getExternalStorageDirectory().path + "/PoseCamFiles.zip", "")
                val folderPath = Environment.getExternalStorageDirectory().path + "/"
                val zipFileName = "PoseCamFiles.zip"
                val zipFile = File(folderPath + zipFileName)
                val zipURI = FileProvider.getUriForFile(this@MainActivity, "com.example.android.tiltspot.fileprovider", zipFile)
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_STREAM, zipURI)
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                sendIntent.type = "application/zip"
                startActivityForResult(Intent.createChooser(sendIntent, "Share..."), 512)
                spinner!!.visibility = View.INVISIBLE
            }.start()


//
        }
        findViewById<View>(R.id.button).setOnClickListener {
            //                new Thread(new Runnable() {
//                    public void run() {
            openCamActivity()
            //
//                    }
//                }).start();
        }
        findViewById<View>(R.id.button3).setOnClickListener {
            //                new Thread(new Runnable() {
//                    public void run() {
            openActivity2()

//                    }
//                }).start();
        }
    }

    fun openActivity2() {
        val intent = Intent(this, Activity2::class.java)
        startActivity(intent)
    }

    fun openCamActivity() {
        val intent = Intent(this, CamActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 512) {
            try {
                val file = File(Environment.getExternalStorageDirectory().path
                        + "/PoseCamFiles.zip")
                if (file.exists()) file.delete()
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */
    override fun onStart() {
        super.onStart()

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
    } //    @Override
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
    companion object {
        @JvmField
        var mTextSensorAzimuth: TextView? = null
        private val PERMS_TAKE_PICTURE = arrayOf(
                permission.CAMERA,
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE
        )
        private const val RESULT_PERMS_INITIAL = 1339
    }
}
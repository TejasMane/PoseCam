package com.example.android.tiltspot;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import ir.mahdi.mzip.zip.ZipArchive;


public class CamActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private CameraPreview mCameraPreview;


//    private SensorManager mSensorManager;
//    private Sensor mSensorAccelerometer;
//    private Sensor mSensorMagnetometer;
//    private Sensor mSensorRotmeter;

//    private float[] mRotmeterData = new float[3];

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_layout);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);


//        mSensorRotmeter = mSensorManager.getDefaultSensor(
//                Sensor.TYPE_ROTATION_VECTOR );
//
//        if (mSensorRotmeter != null) {
//            mSensorManager.registerListener(this, mSensorRotmeter,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }




        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread t = new Thread(new Runnable() {
                    public void run() {

                        mCamera.takePicture(null, null, mPicture);
                    }
                });

                t.start();
//                mCamera.takePicture(null, null, mPicture);
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                mCamera.stopPreview();
//                mCamera.startPreview();

//                recreate();
            }
        });
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
//            Camera.Parameters params = camera.getParameters();
//            List<String> flashModes = params.getSupportedFlashModes();
//
//            if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
//                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//            }
//            camera.setParameters(params);


        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }

            mCamera.startPreview();
        }
    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PoseCam");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PoseCam", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;


        int aziI = (int)  (MyService.azimuth * 90/1.57);
        String azi = Integer.toString(aziI);

        int PitchI =  (int)  (MyService.pitch * 90/1.57);
        String Pitch = Integer.toString(PitchI);

        int RollI = (int)  (MyService.roll * 90/1.57);
        String Roll = Integer.toString(RollI);

        String fileName = "angles_"+azi+"_"+Pitch+"_"+Roll;


        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                 + fileName + ".png");



        return mediaFile;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
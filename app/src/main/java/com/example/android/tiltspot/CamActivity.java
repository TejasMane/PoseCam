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
import android.hardware.camera2.CameraDevice;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import ir.mahdi.mzip.zip.ZipArchive;


public class CamActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_FRONT;


//    private float[] mRotmeterData = new float[3];

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.cam_layout);


        mCamera = getCameraInstance();


        mCameraPreview = new CameraPreview(this, mCamera);
        RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);


        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Thread t = new Thread(new Runnable() {
//                    public void run() {

                        mCamera.takePicture(new Camera.ShutterCallback() { @Override public void onShutter() { } }, null, mPicture);
//
//                    }
//                });

//                t.start();
//                try {
//                    t.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
        });

        Button flipCamButton = (Button) findViewById(R.id.flipCam);

        flipCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.stopPreview();
                mCamera.release();

                if(camId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    camId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    camId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                mCamera = Camera.open(camId);

                Camera.Parameters parameters = mCamera.getParameters();
                List<String> focusModes = parameters.getSupportedFocusModes();

//                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//                    parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//                }

                List<String> flashModes = parameters.getSupportedFlashModes();

//                if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//                }

                if(camId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    parameters.setRotation(90);
                }
                else {
                    parameters.setRotation(270);
                }


                mCamera.setParameters(parameters);

                mCameraPreview = new CameraPreview(CamActivity.this, mCamera);
                RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
                preview.addView(mCameraPreview);

            }
        });





        Button BackButton = (Button) findViewById(R.id.button4);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCamera(true);

                Intent intent = new Intent(CamActivity.this, MainActivity.class);
                startActivity(intent);

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


            
//            camera = Camera.open();

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        camera = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                    }
                }
            }


            Camera.Parameters parameters = camera.getParameters();


            if(camId == Camera.CameraInfo.CAMERA_FACING_BACK){
                parameters.setRotation(90);
            }
            else {
                parameters.setRotation(270);
            }


            List<String> focusModes = parameters.getSupportedFocusModes();

            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            List<String> flashModes = parameters.getSupportedFlashModes();

//            if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//            }

            camera.setParameters(parameters);


        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }


    private Camera getCameraInstance2() {
        Camera camera = null;
        try {



//            camera = Camera.open();

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        camera = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                    }
                }
            }


            Camera.Parameters parameters = camera.getParameters();


            if(camId == Camera.CameraInfo.CAMERA_FACING_BACK){
                parameters.setRotation(90);
            }
            else {
                parameters.setRotation(270);
            }


            camera.setParameters(parameters);


            camera.setParameters(parameters);


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

        // Create a media file name
        String timeStamp = new SimpleDateFormat("dd_HHmmss")
                .format(new Date());
        File mediaFile;


        int aziI = (int)  (MyService.azimuth * 90/1.57);
        String azi = Integer.toString(aziI);

        int PitchI =  (int)  (MyService.pitch * 90/1.57);
        String Pitch = Integer.toString(PitchI);

        int RollI = (int)  (MyService.roll * 90/1.57);
        String Roll = Integer.toString(RollI);

        String fileName = "PC_"+azi+"_"+Pitch+"_"+Roll+"_"+Integer.toString(camId);


        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                 + fileName + "_"+timeStamp+ ".png");



        return mediaFile;
    }




    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        releaseCamera(true);

        mCamera = getCameraInstance();


        mCameraPreview = new CameraPreview(this, mCamera);
        RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        releaseCamera(true);

        mCamera = getCameraInstance();


        mCameraPreview = new CameraPreview(this, mCamera);
        RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);


    }


    private void releaseCamera(boolean remove) {
        if (mCamera != null) {
            if (remove)
                mCameraPreview.getHolder().removeCallback(mCameraPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera(true);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
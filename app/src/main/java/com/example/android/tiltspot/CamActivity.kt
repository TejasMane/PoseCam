package com.example.android.tiltspot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.cam_layout.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit


class CamActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    var lF = CameraSelector.DEFAULT_BACK_CAMERA
    var fM: Int = ImageCapture.FLASH_MODE_AUTO




    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cam_layout)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }




        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener {
            takePhoto()

        }

        button4.setOnClickListener { backButtonAction() }

        flipCam.setOnClickListener { flipCamera() }
        flashButton.setOnClickListener { flashButtonAction() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }



    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun backButtonAction(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }


    private fun flashButtonAction(){


        when (fM) {
            ImageCapture.FLASH_MODE_OFF -> {
                fM = ImageCapture.FLASH_MODE_ON
                val msg = "Flash Mode = ON "
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

            ImageCapture.FLASH_MODE_ON -> {
                fM = ImageCapture.FLASH_MODE_AUTO
                val msg = "Flash Mode = Auto "
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
            ImageCapture.FLASH_MODE_AUTO -> {

                fM = ImageCapture.FLASH_MODE_OFF
                val msg = "Flash Mode = OFF "
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        }



//
//
//
//        if (fM.equals(ImageCapture.FLASH_MODE_AUTO))
//        {
//            fM = ImageCapture.FLASH_MODE_ON
//            val msg = "Flash Mode = ON "
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            Log.d(TAG, msg)
//        }
//        else if (fM.equals(ImageCapture.FLASH_MODE_ON))
//        {
//            fM== ImageCapture.FLASH_MODE_OFF
//            val msg = "Flash Mode = OFF "
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            Log.d(TAG, msg)
//        }
//        else if (fM.equals(ImageCapture.FLASH_MODE_OFF))
//        {
//            fM== ImageCapture.FLASH_MODE_AUTO
//
//            val msg = "Flash Mode = Auto "
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        }


        startCamera()


    }


    private fun flipCamera(){

        if (lF== CameraSelector.DEFAULT_BACK_CAMERA)
        {
            lF = CameraSelector.DEFAULT_FRONT_CAMERA
        }
        else
        {
            lF = CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()

    }



    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image

        val mediaStorageDir: File = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PoseCam")


        val timeStamp = SimpleDateFormat("dd_HHmmss")
                .format(Date())
        val photoFile: File


        val aziI = (MyService.azimuth * 90 / 1.57).toInt()
        val azi = Integer.toString(aziI)

        val PitchI = (MyService.pitch * 90 / 1.57).toInt()
        val Pitch = Integer.toString(PitchI)

        val RollI = (MyService.roll * 90 / 1.57).toInt()
        val Roll = Integer.toString(RollI)

        val isBack = if ((lF== CameraSelector.DEFAULT_BACK_CAMERA)) 1 else 0

        val fileName = "PC_" + azi + "_" + Pitch + "_" + Roll + "_" + Integer.toString(isBack)


        photoFile = File(mediaStorageDir.getPath().toString() + File.separator
                + fileName + "_" + timeStamp + ".png")





        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $fileName"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            }
        })
    }





    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    }

            imageCapture = ImageCapture.Builder().setFlashMode(fM).setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        })
                    }

            // Select back camera as a default
            val cameraSelector = lF

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                var camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }



}
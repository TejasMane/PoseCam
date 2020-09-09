package com.example.android.tiltspot

import android.app.IntentService
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.widget.Button
import java.util.*

class MyService : IntentService("Hello"), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mSensorRotmeter: Sensor? = null
    private var mRotmeterData = FloatArray(3)
    private val mRotHist: List<FloatArray> = ArrayList()
    private val mRotHistIndex = 0
    private val mHistoryMaxLength = 40
    var mRotationMatrix = FloatArray(9)
    var azimuthTemp = 0f
    override fun onCreate() {
        super.onCreate()
        mSensorManager = getSystemService(
                SENSOR_SERVICE) as SensorManager
        mSensorRotmeter = mSensorManager!!.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR)
        if (mSensorRotmeter != null) {
            mSensorManager!!.registerListener(this, mSensorRotmeter,
                    SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onHandleIntent(intent: Intent?) {}
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val sensorType = sensorEvent.sensor.type
        mRotmeterData = when (sensorType) {
            Sensor.TYPE_ROTATION_VECTOR -> sensorEvent.values.clone()
            else -> return
        }
        val rotationMatrix = FloatArray(9)
        //        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,null, mAccelerometerData, mMagnetometerData );
        SensorManager.getRotationMatrixFromVector(rotationMatrix, mRotmeterData)
        val orientationValues = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationValues)
        azimuthTemp = orientationValues[0]
        if (azimuthTemp < 0) {
            azimuthTemp += 2 * Math.PI.toFloat()
        }
        azimuth = azimuthTemp
        pitch = orientationValues[1]
        roll = orientationValues[2]
        MainActivity.mTextSensorAzimuth!!.text = ("A:" + resources.getString(R.string.value_format, azimuth * 90 / 1.57) + ", "
                + "P:" + resources.getString(R.string.value_format, pitch * 90 / 1.57) + ", "
                + "R:" + resources.getString(R.string.value_format, roll * 90 / 1.57))
        //        MainActivity.mTextSensorPitch.setText(getResources().getString(
//                R.string.value_format, pitch*90/1.57));
//        MainActivity.mTextSensorRoll.setText(getResources().getString(
//                R.string.value_format, roll*90/1.57));
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    protected fun onStop() {

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager!!.unregisterListener(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY_COMPATIBILITY
    }

    protected fun ondestroy() {
        mSensorManager!!.unregisterListener(this)
        super.onDestroy()
    }

    companion object {
        private const val VALUE_DRIFT = 0.05f
        var listView: Button? = null
        var azimuth = 0f
        var roll = 0f
        var pitch = 0f
    }
}
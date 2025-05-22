package com.example.androidsensordatafetcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class SensorService : Service() {
    private lateinit var sensorManager: SensorManager

     private fun startForeground() {
         val fileName = "${System.currentTimeMillis()}-sensor.csv"
         val sensorFile = FileOutputStream(
             File(
                 Environment.getExternalStoragePublicDirectory(
             Environment.DIRECTORY_DOWNLOADS), fileName)
         )
        val writer = BufferedWriter(OutputStreamWriter(sensorFile))
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        registerSensors(sensorManager, writer)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sensor Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recording Sensor Data")
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                100,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            ServiceCompat.startForeground(this, 100, notification, 0)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun registerSensors(sensorManager: SensorManager, writer: BufferedWriter) {
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        for (sensor in sensors) {
            sensorManager.registerListener(object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

                override fun onSensorChanged(event: SensorEvent) {
                    writer.write(event.sensor.name)
                    writer.write(",")
//                    event.timestamp returns uptime, not world time
//                    use event.timestamp for increased accuracy
                    writer.write(event.timestamp.toString())
                    writer.write(",")
                    writer.write(event.values.joinToString())
                    writer.newLine()
                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    companion object {
        const val CHANNEL_ID = "sensor_service_channel"
    }
}
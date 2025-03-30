package com.example.androidsensordatafetcher
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.androidsensordatafetcher.ui.theme.AndroidSensorDataFetcherTheme
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, SensorService::class.java)
        startService(intent)

        enableEdgeToEdge()
        setContent {
            AndroidSensorDataFetcherTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(
                        onClick = { },
                    ) {
                        Text(text = "oh hai", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

class SensorService : Service() {

    private lateinit var sensorManager: SensorManager

    private val file = File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS), "sensorData.csv")
    private val fileWriter by lazy { FileWriter(file) }
    private val bufferedWriter by lazy { BufferedWriter(fileWriter) }

    private fun startForeground() {
        val channel = NotificationChannel(
            "SENSOR_CHANNEL_ID",
            "SensorChannel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "SENSOR_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My App")
            .setContentText("Running in background")
            .build()
        ServiceCompat.startForeground(this, 100, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
    }

    override fun onCreate() {
        super.onCreate()
        createFile()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        registerSensors(sensorManager)
        startForeground()
    }

    // required, no idea what to do with it yet
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun registerSensors(sensorManager: SensorManager) {
        val sensors = listOf(
            sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
            sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY),
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        )

        for (sensor in sensors) {
            sensorManager.registerListener(object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

                override fun onSensorChanged(event: SensorEvent) {
                    writeDataToFile(event)
                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun writeDataToFile(sensorEvent: SensorEvent) {
        bufferedWriter.write(sensorEvent.sensor.name)
        bufferedWriter.write(",")
        bufferedWriter.write(System.currentTimeMillis().toString())
        bufferedWriter.write(",")
        bufferedWriter.write(sensorEvent.values.joinToString())
        bufferedWriter.newLine()
    }

    private fun createFile() {
        if (!file.exists()) {
            try {
                val outputStream = FileOutputStream(file)
                outputStream.write("Hello, World!".toByteArray())
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            val outputStream = FileOutputStream(file)
            outputStream.write("Hello, World II!".toByteArray())
            outputStream.close()
        }
    }

}
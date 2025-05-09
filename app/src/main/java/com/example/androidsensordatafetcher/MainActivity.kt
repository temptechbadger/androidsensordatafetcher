package com.example.androidsensordatafetcher
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.androidsensordatafetcher.ui.theme.AndroidSensorDataFetcherTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ), 0
        )

        val sensorServiceIntent = Intent(this, SensorService::class.java)
        val locationServiceIntent = Intent(this, LocationService::class.java)
        sensorServiceIntent.putExtra("Sensor Service", 45)
        locationServiceIntent.putExtra("Location Service", 46)
        startForegroundService(sensorServiceIntent)
        startForegroundService(locationServiceIntent)

        enableEdgeToEdge()
        setContent {
            AndroidSensorDataFetcherTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(text = "to finish the collection of data, use the system menu and close the service", fontSize = 24.sp)
                }
            }
        }
    }
}


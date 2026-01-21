package com.example.glimpse.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.glimpse.SharedViewModel
import com.example.glimpse.utility.OrientationListener
import kotlin.math.roundToInt

/*
FR11 - Compass.Widget
FR12 - Compass.Orientation
Composable compass widget. Provides the current compass direction the rear of the phone is facing.
If the rear is pointing to the sky (alternative proposed orientation) the compass direction is adjusted
to be that of the top edge of the phone in a landscape orientation with the top (charging port) to the right
 */

@Composable
fun CompassWidget(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    var compassDirection by remember { mutableStateOf("") }
    var rotation by remember { mutableIntStateOf(0) }

    LifecycleResumeEffect(Unit) {
        // Orientation listener
        val orientationListener = OrientationListener(context) { rotation = it }
        orientationListener.startListening()

        // Listener for device vector changes (rotation)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    // Get sensor details
                    val rotationMatrix = FloatArray(9)
                    val orientation = FloatArray(3)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)

                    val azimuthF = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    val pitchF = Math.toDegrees(orientation[1].toDouble()).toFloat()
                    val rollF = Math.toDegrees(orientation[2].toDouble()).toFloat()

                    // Convert to integer degrees
                    val azimuth = ((azimuthF + 360) % 360).roundToInt()
                    val pitch = ((pitchF + 360) % 360).roundToInt()
                    val roll = ((rollF + 360) % 360).roundToInt()

                    compassDirection = CompassManager.getCompassDirection(azimuth, pitch, roll, rotation)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val sensorManager = context.getSystemService(SensorManager::class.java)
        sensorManager.registerListener(
            listener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_UI
        )

        onPauseOrDispose {
            orientationListener.stopListening()
            sensorManager.unregisterListener(listener)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = compassDirection,
            color = sharedViewModel.hudForegroundColor,
            fontSize = 48.sp,
            fontFamily = sharedViewModel.selectedFont.fontResource,
        )
    }
}


package com.example.glimpse.camera

import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.glimpse.utility.OrientationListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
FR13 - Barcode.Scanning
FR21 - Recognition.Widget
Camera analyzer composable. Starts camera and image analysis.
 */
@Composable
fun CameraAnalyzer(
    analyzer: CameraFeedAnalyzer<*>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }


    // Initialize CameraX
    LifecycleResumeEffect(Unit) {
        // Listen for orientation changes
        val orientationListener = OrientationListener(context) { rotation ->
            analyzer.rotation = rotation
        }
        orientationListener.startListening()

        // Initialize CameraX
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProvider = ProcessCameraProvider.getInstance(context).get()

        // Set up the image analysis
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setTargetRotation(Surface.ROTATION_0) // Ensures proper behavior regardless of screen orientation
            .build()
            .also {
                it.setAnalyzer(cameraExecutor!!, analyzer)
            }

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            imageAnalysis
        )

        onPauseOrDispose {
            orientationListener.stopListening()

            // Shut off CameraX
            cameraProvider?.unbindAll()
            cameraExecutor?.shutdown()
        }
    }
}
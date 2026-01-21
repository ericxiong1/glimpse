package com.example.glimpse.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.glimpse.SharedViewModel
import com.example.glimpse.utility.surfaceRotationToInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
FR13 - Barcode.Scanning
FR21 - Recognition.Widget
Camera feed analyzer. Converts frames to bitmaps, rotates them so the ground is down
and then processes them according to the given use case.
 */
abstract class CameraFeedAnalyzer<T>(
    protected val context: Context,
    protected val sharedViewModel: SharedViewModel
) : ImageAnalysis.Analyzer {
    private var isProcessing = false
    var lastSuccessTime = 0L
    open val successTimeout = 1000L
    var rotation = ORIENTATION_UNKNOWN

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (isProcessing) {
            image.close()
            return
        }
        isProcessing = true

        val frameBitmap = Bitmap.createBitmap(
            image.width, image.height, Bitmap.Config.ARGB_8888
        )
        frameBitmap.copyPixelsFromBuffer(image.planes[0].buffer)

        val imageTransform = Matrix().apply {
            var rotationAngle = if (rotation == ORIENTATION_UNKNOWN) {
                // Mirror image in the "flat" orientation
                postScale(-1f, 1f, frameBitmap.width / 2f, frameBitmap.height / 2f)
                // Apply fixed offset
                -90f
            } else {
                -surfaceRotationToInteger(rotation).toFloat()
            }
            if (sharedViewModel.rotateCamera)
                rotationAngle += 180f
            postRotate(rotationAngle + image.imageInfo.rotationDegrees.toFloat())
        }

        val transformedBitmap = Bitmap.createBitmap(
            frameBitmap, 0, 0, frameBitmap.width, frameBitmap.height, imageTransform, false
        )

        CoroutineScope(Dispatchers.Default).launch {
            processImage(transformedBitmap) {
                isProcessing = false
            }
        }
        image.close()
    }

    protected abstract fun processImage(bitmap: Bitmap, onFinished: () -> Unit = {})
}
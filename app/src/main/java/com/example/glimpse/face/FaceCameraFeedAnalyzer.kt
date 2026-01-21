package com.example.glimpse.face

import android.content.Context
import android.graphics.Bitmap
import com.example.glimpse.SharedViewModel
import com.example.glimpse.camera.CameraFeedAnalyzer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*
FR21 - Recognition.Widget
Camera feed analyzer. Converts frames to bitmaps, rotates them so the ground is down
and then sends them for facial recognition
 */
class FaceCameraFeedAnalyzer(
    context: Context,
    sharedViewModel: SharedViewModel,
    val onFaceRecognized: (FacialRecognition.FaceRecognitionResult) -> Unit
) : CameraFeedAnalyzer<FacialRecognition.FaceRecognitionResult>(context, sharedViewModel) {

    public override fun processImage(bitmap: Bitmap, onFinished: () -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = FacialRecognition.getInstance(context).findFace(bitmap)
            if (result.detected && result.person != null) {
                lastSuccessTime = System.currentTimeMillis()
                onFaceRecognized(result)
            } else if (System.currentTimeMillis() - lastSuccessTime > successTimeout) {
                onFaceRecognized(result)
            }
            onFinished()
        }
    }
}


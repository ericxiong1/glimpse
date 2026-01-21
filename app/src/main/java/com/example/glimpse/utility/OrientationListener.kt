package com.example.glimpse.utility

import android.content.Context
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.core.UseCase
import androidx.lifecycle.LifecycleObserver


/*
FR11 - Compass.Widget
FR21 - Recognition.Widget

Listens for changes in orientation and notifies the owner when the orientation is changed
with the updated orientation
 */
class OrientationListener(
    private val context: Context,
    private val onRotationChanged: (Int) -> Unit
) :
    LifecycleObserver {
    private var mOrientationEventListener: OrientationEventListener? = null

    init {
        mOrientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                // Snap the orientation to the nearest surface rotation
                if (orientation != ORIENTATION_UNKNOWN)
                    onRotationChanged(UseCase.snapToSurfaceRotation(orientation))
                else
                    onRotationChanged(ORIENTATION_UNKNOWN)
            }
        }
    }

    fun startListening() {
        mOrientationEventListener?.enable()
    }

    fun stopListening() {
        mOrientationEventListener?.disable()
    }
}

// Public helper function to convert a surface rotation to an integer
fun surfaceRotationToInteger(rotation: Int): Int {
    return when (rotation) {
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }
}
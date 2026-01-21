package com.example.glimpse.compass

import com.example.glimpse.utility.surfaceRotationToInteger

class CompassManager {
    /* FR12 Compass.Orientation
    The following code segment directly addresses FR12 by adjusting the compass direction
    when the phone is in the secondary orientation */
    companion object {
        fun getCompassDirection(
            azimuth: Int,
            pitch: Int,
            roll: Int,
            surfaceRotation: Int
        ): String {
            val orientationAdjustment =
                if ((pitch in 0..22 || pitch in 338..360) && roll in 135..225) {
                    // Phone screen facing down, secondary orientation
                    // compass direction is to the "top" of the hud in standard landscape orientation
                    270
                } else {
                    // Account for phone orientation
                    surfaceRotationToInteger(surfaceRotation)
                }

            val direction = (azimuth + orientationAdjustment + 360) % 360

            return when (direction) {
                in 0..22, in 338..360 -> "N"
                in 23..67 -> "NE"
                in 68..112 -> "E"
                in 113..157 -> "SE"
                in 158..202 -> "S"
                in 203..247 -> "SW"
                in 248..292 -> "W"
                else -> "NW"
            }
        }
    }
}
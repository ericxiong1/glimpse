package com.example.glimpse.compass

import android.view.Surface
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CompassTest {
    @Test
    fun compassStandard() {
        val direction = CompassManager.getCompassDirection(210, 0, 0, Surface.ROTATION_90)
        assertEquals("NW", direction)
    }

    @Test
    fun compassAlternate() {
        val direction = CompassManager.getCompassDirection(0, 0, 180, 0)
        assertEquals("W", direction)
    }

    @Test
    fun compassOverflow() {
        CompassManager()
        val direction = CompassManager.getCompassDirection(270, 180, 0, Surface.ROTATION_270)
        assertEquals("S", direction)
    }

    @Test
    fun compassDirections() {
        var direction = CompassManager.getCompassDirection(0, 0, 0, Surface.ROTATION_0)
        assertEquals("N", direction)

        direction = CompassManager.getCompassDirection(360, 0, 0, Surface.ROTATION_0)
        assertEquals("N", direction)

        direction = CompassManager.getCompassDirection(24, 0, 0, Surface.ROTATION_0)
        assertEquals("NE", direction)

        direction = CompassManager.getCompassDirection(100, 0, 0, Surface.ROTATION_0)
        assertEquals("E", direction)

        direction = CompassManager.getCompassDirection(118, 0, 0, Surface.ROTATION_0)
        assertEquals("SE", direction)

        direction = CompassManager.getCompassDirection(208, 0, 0, Surface.ROTATION_0)
        assertEquals("SW", direction)
    }
}
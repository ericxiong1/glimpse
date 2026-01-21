package com.example.glimpse.voice

import androidx.compose.runtime.mutableStateOf
import com.example.glimpse.SharedViewModel
import com.example.glimpse.WidgetType
import com.example.glimpse.customization.WidgetMenuItem
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VoiceActionTest {
    private lateinit var sharedViewModel: SharedViewModel
    private val hudOn = mutableStateOf(false)

    @Before
    fun setup() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0

        sharedViewModel = mockk(relaxed = true)
        every { sharedViewModel.widgetMenuItems } returns listOf(
            WidgetMenuItem(WidgetType.Barcode, true),
            WidgetMenuItem(WidgetType.Chat, true),
            WidgetMenuItem(WidgetType.Compass, true),
            WidgetMenuItem(WidgetType.Face, true),
            WidgetMenuItem(WidgetType.Navigate, true),
            WidgetMenuItem(WidgetType.Weather, true),
            WidgetMenuItem(WidgetType.Settings, true),
        )
        hudOn.value = false
        VoiceAction()
    }

    @After
    fun teardown() {
        unmockkStatic(android.util.Log::class)
    }

    // changeWidget
    @Test
    fun changeWidgetBarcode() {
        val words = listOf("Barcode", "Barcodes", "Scan", "Scanner", "Barcode Scanner")

        for (word in words) {
            val result = VoiceAction.action(
                "changeWidget",
                mapOf("widget" to word),
                sharedViewModel,
                hudOn
            )
            assertEquals(WidgetType.Barcode, result)
        }
    }

    @Test
    fun changeWidgetChat() {
        val words = listOf("Chat", "Gemini", "A I")

        for (word in words) {
            val result = VoiceAction.action(
                "changeWidget",
                mapOf("widget" to word),
                sharedViewModel,
                hudOn
            )
            assertEquals(WidgetType.Chat, result)
        }
    }

    @Test
    fun changeWidgetCompass() {
        val words = listOf("Compass", "Heading", "Bearing")

        for (word in words) {
            val result = VoiceAction.action(
                "changeWidget",
                mapOf("widget" to word),
                sharedViewModel,
                hudOn
            )
            assertEquals(WidgetType.Compass, result)
        }
    }

    @Test
    fun changeWidgetFace() {
        val words = listOf("Face", "Facial Recognition", "face recognition", "recognition")

        for (word in words) {
            val result = VoiceAction.action(
                "changeWidget",
                mapOf("widget" to word),
                sharedViewModel,
                hudOn
            )
            assertEquals(WidgetType.Face, result)
        }
    }

    @Test
    fun changeWidgetNavigate() {
        val words = listOf("Navigate", "Nav", "Navigation", "Directions", "maps")

        for (word in words) {
            val result = VoiceAction.action(
                "changeWidget",
                mapOf("widget" to word),
                sharedViewModel,
                hudOn
            )
            assertEquals(WidgetType.Navigate, result)
        }
    }

    @Test
    fun changeWidgetWeather() {
        val result =
            VoiceAction.action("changeWidget", mapOf("widget" to "Weather"), sharedViewModel, hudOn)
        assertEquals(WidgetType.Weather, result)
    }

    @Test
    fun changeWidgetSettings() {
        val words = listOf("Settings", "Gear")

        for (word in words) {
            val result = VoiceAction.action(
                "changeWidget",
                mapOf("widget" to word),
                sharedViewModel,
                hudOn
            )
            assertEquals(WidgetType.Settings, result)
        }
    }

    @Test
    fun changeWidgetUnknown() {
        val result =
            VoiceAction.action("changeWidget", mapOf("widget" to "Music"), sharedViewModel, hudOn)
        assertNull(result)
    }

    @Test
    fun changeWidgetInvisible() {
        every { sharedViewModel.widgetMenuItems } returns listOf(
            WidgetMenuItem(
                WidgetType.Chat,
                false
            )
        )
        val result =
            VoiceAction.action("changeWidget", mapOf("widget" to "Chat"), sharedViewModel, hudOn)
        assertNull(result)
    }

    // dnd
    @Test
    fun dndOnCallsUpdateTrue() {
        VoiceAction.action("dnd", mapOf("state" to "on"), sharedViewModel, hudOn)
        verify { sharedViewModel.updateDnd(true) }
    }

    @Test
    fun dndOffCallsUpdateFalse() {
        VoiceAction.action("dnd", mapOf("state" to "off"), sharedViewModel, hudOn)
        verify { sharedViewModel.updateDnd(false) }
    }

    @Test
    fun dndUnknownStateDoesNothing() {
        VoiceAction.action("dnd", mapOf("state" to "maybe"), sharedViewModel, hudOn)
        verify(exactly = 0) { sharedViewModel.updateDnd(any()) }
    }

    // setting
    @Test
    fun settingBrightnessPercent() {
        VoiceAction.action(
            "setting",
            mapOf("setting" to "brightness", "level" to "50%"),
            sharedViewModel,
            hudOn
        )
        verify { sharedViewModel.updateBrightness(0.5f) }
    }

    @Test
    fun settingVolumeNumber() {
        VoiceAction.action(
            "setting",
            mapOf("setting" to "brightness", "level" to "22"),
            sharedViewModel,
            hudOn
        )
        verify { sharedViewModel.updateBrightness(0.22f) }
    }

    @Test
    fun settingVolumeWithMax() {
        val words = listOf("one hundred", "maximum", "max")

        for (word in words) {
            VoiceAction.action(
                "setting",
                mapOf("setting" to "volume", "settingLevel" to word),
                sharedViewModel,
                hudOn
            )
            verify { sharedViewModel.updateVolume(1.0f) }
        }
    }

    @Test
    fun settingVolumeInvalidLevel() {
        VoiceAction.action(
            "setting",
            mapOf("setting" to "volume", "settingLevel" to "bad"),
            sharedViewModel,
            hudOn
        )
        verify(exactly=0) { sharedViewModel.updateVolume(1.0f) }
    }

    @Test
    fun settingBrightnessWithMin() {
        val words = listOf("minimum", "min")

        for (word in words) {
            VoiceAction.action(
                "setting",
                mapOf("setting" to "volume", "settingLevel" to word),
                sharedViewModel,
                hudOn
            )
            verify { sharedViewModel.updateVolume(0f) }
        }
    }

    @Test
    fun settingUnknown() {
        VoiceAction.action(
            "setting",
            mapOf("setting" to "color", "level" to "20%"),
            sharedViewModel,
            hudOn
        )
        verify(exactly = 0) { sharedViewModel.updateBrightness(any()) }
        verify(exactly = 0) { sharedViewModel.updateVolume(any()) }
    }

    @Test
    fun settingInvalidLevel() {
        VoiceAction.action(
            "setting",
            mapOf("setting" to "brightness", "level" to "hello"),
            sharedViewModel,
            hudOn
        )
        verify(exactly = 0) { sharedViewModel.updateBrightness(any()) }
        verify(exactly = 0) { sharedViewModel.updateVolume(any()) }
    }

    @Test
    fun settingNoLevel() {
        VoiceAction.action("setting", mapOf("setting" to "volume"), sharedViewModel, hudOn)
        verify(exactly = 0) { sharedViewModel.updateVolume(any()) }
    }

    @Test
    fun settingInvalid() {
        VoiceAction.action(
            "setting",
            mapOf("setting" to "brightness", "level" to "high"),
            sharedViewModel,
            hudOn
        )
        verify(exactly = 0) { sharedViewModel.updateBrightness(any()) }
    }

    // hud
    @Test
    fun hudOnSetsTrue() {
        hudOn.value = false
        VoiceAction.action("hud", mapOf("state" to "on"), sharedViewModel, hudOn)
        assertTrue(hudOn.value)
    }

    @Test
    fun hudOffSetsFalse() {
        hudOn.value = true
        VoiceAction.action("hud", mapOf("state" to "off"), sharedViewModel, hudOn)
        assertFalse(hudOn.value)
    }

    @Test
    fun hudUnknown() {
        hudOn.value = true
        VoiceAction.action("hud", mapOf("state" to "maybe"), sharedViewModel, hudOn)
        assertTrue(hudOn.value)

        hudOn.value = false
        VoiceAction.action("hud", mapOf("state" to "maybe"), sharedViewModel, hudOn)
        assertFalse(hudOn.value)
    }

    @Test
    fun unknown() {
        val result = VoiceAction.action("other", mapOf(), sharedViewModel, hudOn)
        assertNull(result)
    }
}

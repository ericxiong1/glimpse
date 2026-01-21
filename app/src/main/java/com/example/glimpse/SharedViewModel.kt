package com.example.glimpse

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.AndroidViewModel
import com.example.glimpse.customization.AppFont
import com.example.glimpse.customization.WidgetMenuItem
import com.example.glimpse.customization.WidgetPosition
import com.example.glimpse.voice.WakeWordListener
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion

/*
FR1 - Manipulate.HUD
FR2 - Change.Font
FR3 - Change.Colours
FR4 - Change.Positions
FR5 - Change.Brightness
FR24 - Change.Volume
FR9 - Notification.DoNotDisturb
Manages user settings using app SharedPreferences for persistence
 */

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    var wakeWordListener: WakeWordListener? = null

    var hudBackgroundColor by mutableStateOf(loadColor())
        private set

    var iconMode by mutableStateOf(loadIconMode())
        private set

    var hudFontWeight by mutableStateOf(loadFontWeight())
        private set

    var hudMenuOpacity by mutableStateOf(loadMenuOpacity())
        private set

    var hudForegroundColor by mutableStateOf(loadForegroundColor())
        private set

    var scaleX by mutableStateOf(loadScaleX())
        private set

    var scaleY by mutableStateOf(loadScaleY())
        private set

    var mirrorX by mutableStateOf(loadMirrorX())
        private set

    var mirrorY by mutableStateOf(loadMirrorY())
        private set

    var offsetX by mutableStateOf(loadOffsetX())
        private set

    var offsetY by mutableStateOf(loadOffsetY())
        private set

    var rotationX by mutableStateOf(loadRotationX())
        private set

    var rotationY by mutableStateOf(loadRotationY())
        private set

    var rotationZ by mutableStateOf(loadRotationZ())
        private set

    var rotateCamera by mutableStateOf(loadRotateCamera())
        private set

    var selectedPosition by mutableStateOf(WidgetPosition.Top)

    var selectedFont by mutableStateOf<AppFont>(loadSelectedFont())
        private set

    // Widget assignments for each position
    var widgetAssignments by mutableStateOf<Map<WidgetPosition, WidgetType?>>(
        mapOf(
            WidgetPosition.Top to null,
            WidgetPosition.MiddleLeft to null,
            WidgetPosition.MiddleRight to null,
            WidgetPosition.Bottom to null,
        )
    )

    // Toggle for single-widget vs. multi-widget mode
    var isSingleWidgetMode by mutableStateOf(true)

    var widgetMenuItems by mutableStateOf(
        WidgetType.allWidgetTypes.map { WidgetMenuItem(it, true) }
    )

    // Save all preferences, including widget assignments and mode
    fun savePreferences() {
        sharedPreferences.edit().apply {
            // Save existing preferences
            putInt("hudBackgroundColor", hudBackgroundColor.toArgb())
            putBoolean("iconMode", iconMode)
            putInt("hudFontWeight", hudFontWeight.weight)
            putFloat("hudMenuOpacity", hudMenuOpacity)
            putInt("hudForegroundColor", hudForegroundColor.toArgb())
            putFloat("scaleX", scaleX)
            putFloat("scaleY", scaleY)
            putBoolean("mirrorX", mirrorX)
            putBoolean("mirrorY", mirrorY)
            putFloat("offsetX", offsetX)
            putFloat("offsetY", offsetY)
            putFloat("rotationX", rotationX)
            putFloat("rotationY", rotationY)
            putFloat("rotationZ", rotationZ)
            putBoolean("rotateCamera", rotateCamera)
            putString("selectedFont", selectedFont.fontName)

            // Save widget assignments
            widgetAssignments.forEach { (position, widget) ->
                putString(position.name, widget?.toString())
            }

            // Save single-widget mode preference
            putBoolean("isSingleWidgetMode", isSingleWidgetMode)

            // Save selected position
            putString("selectedPosition", selectedPosition.name)

            // Save widget visibility states
            widgetMenuItems.forEach { item ->
                putBoolean("widget_${item.widget}", item.isVisible)
            }

            apply()
        }
    }

    // Load all preferences, including widget assignments, mode, and widgetMenuItems
    private fun loadPreferences() {
        // Load existing preferences
        hudBackgroundColor = loadColor()
        iconMode = loadIconMode()
        hudFontWeight = loadFontWeight()
        hudMenuOpacity = loadMenuOpacity()
        hudForegroundColor = loadForegroundColor()
        scaleX = loadScaleX()
        scaleY = loadScaleY()
        mirrorX = loadMirrorX()
        mirrorY = loadMirrorY()
        offsetX = loadOffsetX()
        offsetY = loadOffsetY()
        rotationX = loadRotationX()
        rotationY = loadRotationY()
        rotationZ = loadRotationZ()
        rotateCamera = loadRotateCamera()
        selectedFont = loadSelectedFont()

        // Load widget assignments
        val assignments = mutableMapOf<WidgetPosition, WidgetType?>()
        WidgetPosition.entries.forEach { position ->
            val widgetName = sharedPreferences.getString(position.name, null)
            assignments[position] = WidgetType.allWidgetTypes.find { it.toString() == widgetName }
        }
        widgetAssignments = assignments

        // Load single-widget mode preference
        isSingleWidgetMode = sharedPreferences.getBoolean("isSingleWidgetMode", true)

        // Load selected position
        val savedPosition = sharedPreferences.getString("selectedPosition", null)
        if (savedPosition != null) {
            selectedPosition = WidgetPosition.valueOf(savedPosition)
        }

        // Load widget visibility states
        widgetMenuItems = WidgetType.allWidgetTypes.map { widget ->
            WidgetMenuItem(widget, loadWidgetVisibility(widget))
        }
    }

    // Helper method to load widget visibility
    private fun loadWidgetVisibility(widget: WidgetType): Boolean {
        return sharedPreferences.getBoolean("widget_${widget}", true) // Default to visible
    }

    // Initialize preferences when the ViewModel is created
    init {
        loadPreferences()
    }

    var volume by mutableFloatStateOf(loadVolume())
        private set

    var brightness by mutableFloatStateOf(loadBrightness())
        private set

    var dnd by mutableStateOf(loadDnd())
        private set

    fun updateForegroundColor(color: Color) {
        hudForegroundColor = color
    }

    fun updateMenuOpacity(opacity: Float) {
        hudMenuOpacity = opacity
    }

    fun updateHudFontWeight(weight: FontWeight) {
        hudFontWeight = weight
    }

    fun updateHudBackgroundColor(color: Color) {
        hudBackgroundColor = color
    }

    fun updateIconMode(mode: Boolean) {
        iconMode = mode
    }

    fun updateScaleX(value: Float) {
        scaleX = value
    }

    fun updateScaleY(value: Float) {
        scaleY = value
    }

    fun updateMirrorX(value: Boolean) {
        mirrorX = value
    }

    fun updateMirrorY(value: Boolean) {
        mirrorY = value
    }

    fun updateOffsetX(value: Float) {
        offsetX = value
    }

    fun updateOffsetY(value: Float) {
        offsetY = value
    }

    fun updateRotationX(value: Float) {
        rotationX = value
    }

    fun updateRotationY(value: Float) {
        rotationY = value
    }

    fun updateRotationZ(value: Float) {
        rotationZ = value
    }

    fun updateSelectedFont(font: AppFont) {
        selectedFont = font
    }

    fun updateRotateCamera(value: Boolean) {
        rotateCamera = value
    }

    fun updateVolume(value: Float) {
        volume = value
        sharedPreferences.edit().apply {
            putFloat("volume", volume)
            apply()
        }

    }

    fun updateBrightness(value: Float) {
        brightness = value
        sharedPreferences.edit().apply {
            putFloat("brightness", brightness)
            apply()
        }
    }

    fun updateDnd(value: Boolean) {
        dnd = value
        sharedPreferences.edit().apply {
            putBoolean("dnd", dnd)
            apply()
        }
    }

    private fun loadColor(): Color {
        val colorInt = sharedPreferences.getInt("hudBackgroundColor", Color.Black.toArgb())
        return Color(colorInt)
    }

    private fun loadFontWeight(): FontWeight {
        val weight = sharedPreferences.getInt("hudFontWeight", FontWeight.Normal.weight)
        return FontWeight(weight)
    }

    private fun loadMenuOpacity(): Float {
        return sharedPreferences.getFloat("hudMenuOpacity", 1f)
    }

    private fun loadForegroundColor(): Color {
        val colorInt = sharedPreferences.getInt("hudForegroundColor", Color.Green.toArgb())
        return Color(colorInt)
    }

    private fun loadIconMode(): Boolean {
        return sharedPreferences.getBoolean("iconMode", true)
    }

    private fun loadScaleX(): Float {
        return sharedPreferences.getFloat("scaleX", 1f)
    }

    private fun loadScaleY(): Float {
        return sharedPreferences.getFloat("scaleY", 1f)
    }

    private fun loadMirrorX(): Boolean {
        return sharedPreferences.getBoolean("mirrorX", false)
    }

    private fun loadMirrorY(): Boolean {
        return sharedPreferences.getBoolean("mirrorY", false)
    }

    private fun loadOffsetX(): Float {
        return sharedPreferences.getFloat("offsetX", 0f)
    }

    private fun loadOffsetY(): Float {
        return sharedPreferences.getFloat("offsetY", 0f)
    }

    private fun loadRotationX(): Float {
        return sharedPreferences.getFloat("rotationX", 0f)
    }

    private fun loadRotationY(): Float {
        return sharedPreferences.getFloat("rotationY", 0f)
    }

    private fun loadRotationZ(): Float {
        return sharedPreferences.getFloat("rotationZ", 0f)
    }

    private fun loadRotateCamera(): Boolean {
        return sharedPreferences.getBoolean("rotateCamera", false)
    }

    private fun loadVolume(): Float {
        return sharedPreferences.getFloat("volume", 0.5f)
    }

    private fun loadBrightness(): Float {
        return sharedPreferences.getFloat("brightness", 0.5f)
    }

    private fun loadDnd(): Boolean {
        return sharedPreferences.getBoolean("dnd", false)
    }

    private fun loadSelectedFont(): AppFont {
        val fontName = sharedPreferences.getString("selectedFont", null)
        return if (fontName != null) {
            AppFont.allFonts.firstOrNull { it.fontName == fontName } ?: AppFont.Default
        } else {
            AppFont.Default
        }
    }

    var destination: PlaceAutocompleteSuggestion? by mutableStateOf(null)
}
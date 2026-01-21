package com.example.glimpse.voice

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.glimpse.SharedViewModel
import com.example.glimpse.WidgetType

/*
FR7 - Control.Voice
Actions for user voice commands
 */

class VoiceAction {
    companion object {
        fun action(
            intent: String,
            slots: Map<String, String>,
            sharedViewModel: SharedViewModel,
            hudOn: MutableState<Boolean>
        ): WidgetType? {
            fun getVisibleWidget(widgetType: WidgetType): WidgetType? {
                if (sharedViewModel.widgetMenuItems.any { it.widget == widgetType && it.isVisible }) {
                    return widgetType
                }
                return null
            }
            when (intent) {
                "changeWidget" -> {
                    when (slots["widget"]) {
                        "Barcode", "Barcodes", "Scan", "Scanner", "Barcode Scanner" -> return getVisibleWidget(
                            WidgetType.Barcode
                        )
                        "Chat", "Gemini", "A I" -> return getVisibleWidget(WidgetType.Chat)
                        "Compass", "Heading", "Bearing" -> return getVisibleWidget(WidgetType.Compass)
                        "Face", "Facial Recognition", "face recognition", "recognition" -> return getVisibleWidget(
                            WidgetType.Face
                        )
                        "Navigate", "Nav", "Navigation", "Directions", "maps" -> return getVisibleWidget(WidgetType.Navigate)
                        "Weather" -> return getVisibleWidget(WidgetType.Weather)
                        "Settings", "Gear" -> return getVisibleWidget(WidgetType.Settings)
                        else -> Log.d("Voice", "Unknown widget: ${slots["widget"]}")
                    }
                }

                "dnd" -> {
                    when (slots["state"]) {
                        "on" -> sharedViewModel.updateDnd(true)
                        "off" -> sharedViewModel.updateDnd(false)
                        else -> Log.d("Voice", "Unknown state: ${slots["state"]}")
                    }
                }

                "setting" -> {
                    when (slots["setting"]) {
                        "brightness" -> {
                            val level = getLevel(slots)
                            if (level != null)
                                sharedViewModel.updateBrightness(level / 100)
                        }
                        "volume" -> {
                            val level = getLevel(slots)
                            if (level != null)
                                sharedViewModel.updateVolume(level / 100)
                        }
                        else -> Log.d("Voice", "Unknown setting: ${slots["setting"]}")
                    }
                }

                // FR25 - Off.Widget
                // Users can use voice commands to toggle the HUD on and off
                "hud" -> {
                    when (slots["state"]) {
                        "on" -> hudOn.value = true
                        "off" -> hudOn.value = false
                        else -> Log.d("Voice", "Unknown state: ${slots["state"]}")
                    }
                }
            }
            return null
        }

        private fun getLevel(slots: Map<String, String>): Float? {
            if (slots.containsKey("level")) {
                return try {
                    slots["level"]?.replace("%", "")?.toFloat()
                } catch (e: NumberFormatException) {
                    null
                }
            }
            return when (slots["settingLevel"]) {
                "minimum", "min" -> 0f
                "one hundred", "maximum", "max" -> 100f
                else -> null
            }
        }
    }
}
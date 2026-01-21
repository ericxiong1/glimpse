package com.example.glimpse.weather

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.glimpse.SharedViewModel
import com.example.glimpse.customization.AppFont
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherWidgetTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        sharedViewModel = mockk()

        // Add every blocks for the properties that are being used.
        every { sharedViewModel.hudForegroundColor } returns Color.Green
        every { sharedViewModel.selectedFont } returns AppFont.Default
    }

    @Test
    fun testWeatherWidgetDataAvailable() {
        val editor = sharedPreferences.edit()
        editor.putString("temperature", "25.0")
        editor.putString("description", "overcast clouds")
        editor.putString("name", "Toronto")
        editor.putString("feels_like", "23.0")
        editor.apply()

        composeTestRule.setContent {
            WeatherWidget(sharedViewModel = sharedViewModel)
        }

        composeTestRule.onNodeWithText("25°C Toronto").assertExists()
        composeTestRule.onNodeWithText("Conditions: overcast clouds | Feels like 23°C").assertExists()
    }

    @Test
    fun testWeatherWidgetDataUnavailable() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        composeTestRule.setContent {
            WeatherWidget(sharedViewModel = sharedViewModel)
        }

        composeTestRule.onNodeWithText("Weather unavailable. Please ensure location services are enabled and you are connected to a network.").assertExists()
    }

    @Test
    fun testSharedPreferenceUpdate() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        composeTestRule.setContent {
            WeatherWidget(sharedViewModel = sharedViewModel)
        }
        composeTestRule.onNodeWithText("Weather unavailable. Please ensure location services are enabled and you are connected to a network.").assertExists()

        val editor2 = sharedPreferences.edit()
        editor2.putString("temperature", "25.0")
        editor2.putString("description", "overcast clouds")
        editor2.putString("name", "Toronto")
        editor2.putString("feels_like", "23.0")
        editor2.apply()

        composeTestRule.onNodeWithText("25°C Toronto").assertExists()
        composeTestRule.onNodeWithText("Conditions: overcast clouds | Feels like 23°C").assertExists()

    }
}
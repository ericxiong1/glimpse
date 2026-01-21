package com.example.glimpse.settings

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.glimpse.EmptyActivity
import com.example.glimpse.SharedViewModel
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsWidgetTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<EmptyActivity>()

    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setup() {
        sharedViewModel = spyk(SharedViewModel(composeTestRule.activity.application))
        composeTestRule.activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .edit().clear().apply()
        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        composeTestRule.setContent {
            SettingsWidget(sharedViewModel)
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun labels() {
        composeTestRule.onNodeWithText("Volume").assertIsDisplayed()
        composeTestRule.onNodeWithText("Brightness").assertIsDisplayed()
        composeTestRule.onNodeWithText("Do Not Disturb").assertIsDisplayed()
    }

    @Test
    fun volumeSliderIncrease() {
        val start = sharedViewModel.volume
        composeTestRule.onNodeWithTag("volumeSlider")
            .performTouchInput { swipeRight() }
        assert(sharedViewModel.volume > start)
    }

    @Test
    fun brightnessSliderDecrease() {
        val start = sharedViewModel.brightness
        composeTestRule.onNodeWithTag("brightnessSlider")
            .performTouchInput { swipeLeft() }
        assert(sharedViewModel.brightness < start)
    }

    @Test
    fun dndToggleFlipsState() {
        val start = sharedViewModel.dnd
        composeTestRule.onNodeWithTag("dndSwitch").performClick()
        assert(sharedViewModel.dnd != start)
    }
}
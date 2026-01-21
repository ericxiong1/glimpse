package com.example.glimpse.customization

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.glimpse.SharedViewModel
import com.example.glimpse.WidgetType
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomizationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: NavController
    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setup() {
        navController = mockk(relaxed = true)
        sharedViewModel = mockk(relaxed = true) {
            // Mock visible widgets
            every { widgetMenuItems } returns listOf(
                WidgetMenuItem(WidgetType.Barcode, true),
                WidgetMenuItem(WidgetType.Weather, true),
                WidgetMenuItem(WidgetType.Settings, true)
            )
            every { isSingleWidgetMode } returns true
            every { selectedPosition } returns WidgetPosition.Top
            every { scaleX } returns 1f
            every { scaleY } returns 1f
            every { mirrorX } returns false
            every { mirrorY } returns false
            every { offsetX } returns 0f
            every { offsetY } returns 0f
            every { rotationX } returns 0f
            every { rotationY } returns 0f
            every { rotationZ } returns 0f
            every { rotateCamera } returns false
            every { hudBackgroundColor } returns Color.Black
            every { hudForegroundColor } returns Color.Green
            every { hudFontWeight } returns FontWeight.Normal
            every { hudMenuOpacity } returns 1f
            every { iconMode } returns true
            every { selectedFont } returns AppFont.Default
        }

        composeTestRule.setContent {
            CustomizationScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
    }

    @Test
    fun testForegroundColorAdjustment() {
        composeTestRule.onNodeWithText("HUD Foreground Color").assertIsDisplayed()

        // Adjust red slider
        composeTestRule.onNodeWithTag("ForegroundRed").performTouchInput { swipeRight() }
        verify { sharedViewModel.updateForegroundColor(any()) }
    }

    @Test
    fun testBackgroundColorAdjustment() {
        composeTestRule.onNodeWithText("HUD Background Color").assertIsDisplayed()

        // Adjust Red slider
        composeTestRule.onNodeWithTag("BackgroundRed").performTouchInput { swipeRight() }
        verify { sharedViewModel.updateHudBackgroundColor(any()) }
    }

    @Test
    fun testFontSelection() {
        // Verify font selection button exists
        composeTestRule.onNodeWithText("System Default").performScrollTo().assertIsDisplayed()

        // Click to open dropdown and change font to Monospace
        composeTestRule.onNodeWithText("System Default").performClick()

        composeTestRule.onNodeWithText("Monospace").assertIsDisplayed()

        composeTestRule.onNodeWithText("Monospace").performClick()

        verify { sharedViewModel.updateSelectedFont(any()) }
    }

    @Test
    fun testResetToDefaults() {
        // Verify reset button exists
        composeTestRule.onNodeWithText("Reset to Defaults").assertIsDisplayed()

        // Perform reset
        composeTestRule.onNodeWithText("Reset to Defaults").performClick()

        // Verify all default values are set
        verify { sharedViewModel.updateHudBackgroundColor(Color.Black) }
        verify { sharedViewModel.updateForegroundColor(Color.Green) }
        verify { sharedViewModel.updateHudFontWeight(FontWeight.Normal) }
        verify { sharedViewModel.updateIconMode(true) }
        verify { sharedViewModel.updateMenuOpacity(1f) }
        verify { sharedViewModel.updateSelectedFont(AppFont.Default) }
    }

    @Test
    fun testSavePreferences() {
        // Verify save button exists
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()

        // Perform save
        composeTestRule.onNodeWithText("Save").performClick()

        // Verify navigation and save
        verify { sharedViewModel.savePreferences() }
        verify { navController.popBackStack() }
    }
}
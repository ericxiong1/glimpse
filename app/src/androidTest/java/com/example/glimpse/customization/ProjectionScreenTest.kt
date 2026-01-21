package com.example.glimpse.customization

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class ProjectionScreenTest {

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
            ProjectionScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
    }

    @Test
    fun testScaleModification() {
        // Modify X scale
        composeTestRule.onNodeWithText("Scale").assertIsDisplayed()
        composeTestRule.onNodeWithText("X: 1.00").performTouchInput { swipeRight() }

        // Verify ViewModel update
        verify { sharedViewModel.updateScaleX(any()) }
    }

    @Test
    fun testMirrorToggle() {
        composeTestRule.onNodeWithTag("MirrorXSwitch").performClick()
        verify { sharedViewModel.updateMirrorX(any()) }

        composeTestRule.onNodeWithTag("MirrorYSwitch").performClick()
        verify { sharedViewModel.updateMirrorX(any()) }
    }

    @Test
    fun testOffsetAdjustment() {
        // Adjust X offset
        composeTestRule.onNodeWithTag("OffsetXSlider").performTouchInput { swipeRight() }

        // Verify ViewModel update
        verify { sharedViewModel.updateOffsetX(any()) }
    }

    @Test
    fun testRotationAdjustment() {
        // Adjust X rotation
        composeTestRule.onNodeWithTag("RotationXSlider").performTouchInput { swipeRight() }

        // Verify ViewModel update
        verify { sharedViewModel.updateRotationX(any()) }
    }

    @Test
    fun testCameraFlipToggle() {
        // Toggle camera flip
        composeTestRule.onNodeWithTag("FlipCameraSwitch").performClick()

        // Verify ViewModel update
        verify { sharedViewModel.updateRotateCamera(any()) }
    }

    @Test
    fun testResetToDefaults() {
        // Verify reset button exists
        composeTestRule.onNodeWithText("Reset to Defaults").assertIsDisplayed()

        // Perform reset
        composeTestRule.onNodeWithText("Reset to Defaults").performClick()

        // Verify all default values are set
        verify { sharedViewModel.updateScaleX(1f) }
        verify { sharedViewModel.updateScaleY(1f) }
        verify { sharedViewModel.updateMirrorX(false) }
        verify { sharedViewModel.updateMirrorY(false) }
        verify { sharedViewModel.updateOffsetX(0f) }
        verify { sharedViewModel.updateOffsetY(0f) }
        verify { sharedViewModel.updateRotationX(0f) }
        verify { sharedViewModel.updateRotationY(0f) }
        verify { sharedViewModel.updateRotationZ(0f) }
        verify { sharedViewModel.updateRotateCamera(false) }
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
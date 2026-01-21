package com.example.glimpse.notification

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.glimpse.EmptyActivity
import com.example.glimpse.SharedViewModel
import io.mockk.spyk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationItemTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<EmptyActivity>()

    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setup() {
        sharedViewModel = spyk(SharedViewModel(composeTestRule.activity.application))
        composeTestRule.activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .edit().clear().apply()
        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    @Test
    fun notificationVisible() {
        sharedViewModel.updateDnd(false)
        composeTestRule.setContent {
            NotificationItem(
                appName = "Discord",
                title = "New Message",
                text = "Hello there",
                notificationCount = 1,
                sharedViewModel = sharedViewModel
            )
        }

        composeTestRule.onNodeWithText("New Message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello there").assertIsDisplayed()
        composeTestRule.onNodeWithText("Discord").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Close").assertIsDisplayed()
    }

    @Test
    fun dndOn() {
        sharedViewModel.updateDnd(true)
        composeTestRule.setContent {
            NotificationItem(
                appName = "Discord",
                title = "New Message",
                text = "Hello there",
                notificationCount = 1,
                sharedViewModel = sharedViewModel
            )
        }

        composeTestRule.onNodeWithText("New Message").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Hello there").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Discord").assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription("Close").assertIsNotDisplayed()
    }

    @Test
    fun dismissClick() {
        sharedViewModel.updateDnd(false)
        composeTestRule.setContent {
            NotificationItem(
                appName = "Discord",
                title = "New Message",
                text = "Hello there",
                notificationCount = 1,
                sharedViewModel = sharedViewModel
            )
        }

        composeTestRule.onNodeWithText("New Message").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Close").performClick()
        composeTestRule.onNodeWithText("New Message").assertIsNotDisplayed()
    }
    @Test
    fun dismissSwipe() {
        sharedViewModel.updateDnd(false)
        composeTestRule.setContent {
            NotificationItem(
                appName = "Discord",
                title = "New Message",
                text = "Hello there",
                notificationCount = 1,
                sharedViewModel = sharedViewModel
            )
        }

        composeTestRule.onNodeWithText("New Message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello there").performTouchInput { swipeDown() }
        composeTestRule.onNodeWithText("New Message").assertIsNotDisplayed()
    }
}
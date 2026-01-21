package com.example.glimpse.chat

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.glimpse.EmptyActivity
import com.example.glimpse.SharedViewModel
import com.example.glimpse.voice.PorcupineWakeWordListener
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatWidgetTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<EmptyActivity>()

    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setup() {
        sharedViewModel = spyk(SharedViewModel(composeTestRule.activity.application))
        composeTestRule.activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            .edit().clear().apply()
        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        mockkConstructor(ChatManager::class)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun voiceClick() {
        mockkConstructor(PorcupineWakeWordListener::class)
        every { anyConstructed<PorcupineWakeWordListener>().startListening() } just Runs
        val finishedSlot = slot<(ArrayList<String>?) -> Unit>()
        every {
            anyConstructed<ChatManager>().startSpeechToText(
                any(),
                any(),
                any(),
                capture(finishedSlot)
            )
        } just Runs
        coEvery { anyConstructed<ChatManager>().sendToGemini(any()) } returns "Test response"
        composeTestRule.setContent {
            ChatWidget(sharedViewModel)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().performClick()
        composeTestRule.onNodeWithText("...").assertExists()
        finishedSlot.captured.invoke(arrayListOf("Hello"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test response").assertExists()
    }

    @Test
    fun voiceClickNoResult() {
        mockkConstructor(PorcupineWakeWordListener::class)
        every { anyConstructed<PorcupineWakeWordListener>().startListening() } just Runs
        val finishedSlot = slot<(ArrayList<String>?) -> Unit>()
        every {
            anyConstructed<ChatManager>().startSpeechToText(
                any(),
                any(),
                any(),
                capture(finishedSlot)
            )
        } just Runs
        coVerify(exactly = 0) { anyConstructed<ChatManager>().sendToGemini(any()) }
        coVerify(exactly = 0) { anyConstructed<ChatManager>().sendToGemini(any(), any()) }
        composeTestRule.setContent {
            ChatWidget(sharedViewModel)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().performClick()
        composeTestRule.onNodeWithText("...").assertExists()
        finishedSlot.captured.invoke(null)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chatText").assertTextEquals("")
    }
}
package com.example.glimpse.chat

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.glimpse.SharedViewModel
import com.example.glimpse.voice.WakeWordListener
import org.junit.Assert.*
import org.junit.Test
import com.google.ai.client.generativeai.*
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.asImageOrNull
import com.google.ai.client.generativeai.type.asTextOrNull
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import kotlin.test.assertEquals

class ChatTest {
    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun containsCameraWord_returnsTrueForCameraWords() {
        val inputs =
            listOf("Take a picture", "Capture this", "Use the camera", "What's in this image")
        for (input in inputs) {
            assertTrue(ChatManager.containsCameraWord(input))
        }
    }

    @Test
    fun containsCameraWord_returnsFalseForNonCameraWords() {
        val input = "Tell me a fun fact"
        assertFalse(ChatManager.containsCameraWord(input))
    }

    @Test
    fun standardChat() = runTest {
        mockkConstructor(GenerativeModel::class)
        val mockModel = mockk<Chat>()
        every { anyConstructed<GenerativeModel>().startChat() } returns mockModel
        val mockResponse = mockk<GenerateContentResponse>()
        val expectedContent = "Testing"
        coEvery {
            mockModel.sendMessage(match<Content> { content ->
                content.parts.any { it.asTextOrNull() == expectedContent }
            }
            )
        } returns mockResponse
        val response = "Hello!"
        every { mockResponse.text } returns response

        assertEquals(response, ChatManager().sendToGemini(expectedContent))
    }

    @Test
    fun gemini2Down() = runTest {
        mockkConstructor(GenerativeModel::class)
        val mockResponse = mockk<GenerateContentResponse>()
        val expectedContent = "Testing"
        coEvery {
            anyConstructed<GenerativeModel>().generateContent(match<Content> { content ->
                content.parts.any { it.asTextOrNull() == expectedContent }
            }
            )
        } returns mockResponse
        val response = "Hello!"
        every { mockResponse.text } returns response

        assertEquals(response, ChatManager().sendToGemini("Testing"))
    }

    @Test
    fun geminiError() = runTest {
        mockkConstructor(GenerativeModel::class)
        coEvery {anyConstructed<GenerativeModel>().generateContent(ofType(Content::class))} throws Exception()

        assertEquals("Sorry, please ask something else", ChatManager().sendToGemini("Testing"))
    }

    @Test
    fun imageText() = runTest {
        mockkConstructor(GenerativeModel::class)
        val mockModel = mockk<Chat>()
        every { anyConstructed<GenerativeModel>().startChat() } returns mockModel
        val mockResponse = mockk<GenerateContentResponse>()
        val expectedContent = "Testing"
        val expectedImage = mockk<Bitmap>()
        coEvery {
            mockModel.sendMessage(match<Content> { content ->
                content.parts.any { it.asTextOrNull() == expectedContent }
                content.parts.any { it.asImageOrNull() == expectedImage}
            }
            )
        } returns mockResponse
        val response = "Hello!"
        every { mockResponse.text } returns response

        assertEquals(response, ChatManager().sendToGemini(expectedContent, expectedImage))
    }

    @Test
    fun imageNoText() = runTest {
        mockkConstructor(GenerativeModel::class)
        val mockModel = mockk<Chat>()
        every { anyConstructed<GenerativeModel>().startChat() } returns mockModel
        val mockResponse = mockk<GenerateContentResponse>()
        val expectedImage = mockk<Bitmap>()
        coEvery {
            mockModel.sendMessage(match<Content> { content ->
                content.parts.any { it.asTextOrNull() == "Describe what I am looking at" }
                content.parts.any { it.asImageOrNull() == expectedImage}
            }
            )
        } returns mockResponse
        val response = "Hello!"
        every { mockResponse.text } returns response

        assertEquals(response, ChatManager().sendToGemini(image=expectedImage))
    }

    private val mockRecognizer = mockk<SpeechRecognizer>(relaxed = true)
    private val mockViewModel = mockk<SharedViewModel>(relaxed = true)
    private val mockWakeListener = mockk<WakeWordListener>(relaxed = true)

    @Test
    fun speechToTextOnResults() {
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) } returns mockk(relaxed = true)
        val expectedResults = arrayListOf("Here's some results")
        val mockBundle = mockk<Bundle> {
            every { getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) } returns expectedResults
        }
        var finishedResult: ArrayList<String>? = null
        val chatManager = ChatManager()
        chatManager.startSpeechToText(
            speechRecognizer = mockRecognizer,
            sharedViewModel = mockViewModel,
            chatWordListener = mockWakeListener
        ) { finishedResult = it }

        val listenerSlot = slot<RecognitionListener>()
        verify { mockRecognizer.setRecognitionListener(capture(listenerSlot)) }
        verify { mockRecognizer.startListening(any()) }

        listenerSlot.captured.onResults(mockBundle)

        assertEquals(expectedResults, finishedResult)
    }

    @Test
    fun speechToTextOnEndOfSpeech() {
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) } returns mockk(relaxed = true)

        val chatManager = ChatManager()
        chatManager.startSpeechToText(
            speechRecognizer = mockRecognizer,
            sharedViewModel = mockViewModel,
            chatWordListener = mockWakeListener
        ) { }

        val listenerSlot = slot<RecognitionListener>()
        verify { mockRecognizer.setRecognitionListener(capture(listenerSlot)) }
        verify { mockWakeListener.stopListening() }
        verify { mockViewModel.wakeWordListener?.stopListening() }

        listenerSlot.captured.onEndOfSpeech()
        verify { mockWakeListener.startListening() }
        verify { mockViewModel.wakeWordListener?.startListening() }
    }

    @Test
    fun speechToTextNoRecognizer() {
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) } returns mockk(relaxed = true)


        ChatManager().startSpeechToText(
            speechRecognizer = null,
            sharedViewModel = mockViewModel,
            chatWordListener = mockWakeListener
        ) { }
        verify (exactly=0){ mockRecognizer.setRecognitionListener(any()) }
    }

    @Test
    fun speechToTextNoChatListener() {
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) } returns mockk(relaxed = true)
        every { mockViewModel.wakeWordListener } returns null
        ChatManager().startSpeechToText(
            speechRecognizer = mockRecognizer,
            sharedViewModel = mockViewModel,
            chatWordListener = null
        ) { }

        val listenerSlot = slot<RecognitionListener>()
        verify { mockRecognizer.setRecognitionListener(capture(listenerSlot)) }
        listenerSlot.captured.onEndOfSpeech()
        listenerSlot.captured.onError(1)
    }

    @Test
    fun speechToTextOnError() {
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) } returns mockk(relaxed = true)

        var finishedResult: ArrayList<String>? = arrayListOf("Here's some results")
        val chatManager = ChatManager()
        chatManager.startSpeechToText(
            speechRecognizer = mockRecognizer,
            sharedViewModel = mockViewModel,
            chatWordListener = mockWakeListener
        ) { finishedResult = it }

        val listenerSlot = slot<RecognitionListener>()
        verify { mockRecognizer.setRecognitionListener(capture(listenerSlot)) }
        verify { mockWakeListener.stopListening() }
        verify { mockViewModel.wakeWordListener?.stopListening() }

        listenerSlot.captured.onError(1)
        listenerSlot.captured.onReadyForSpeech(mockk())
        listenerSlot.captured.onBeginningOfSpeech()
        listenerSlot.captured.onRmsChanged(1f)
        listenerSlot.captured.onBufferReceived(byteArrayOf())
        listenerSlot.captured.onPartialResults(mockk())
        listenerSlot.captured.onEvent(1, mockk())

        verify { mockWakeListener.startListening() }
        verify { mockViewModel.wakeWordListener?.startListening() }
        assertNull(finishedResult)
    }
}
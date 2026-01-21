package com.example.glimpse.chat

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.glimpse.BuildConfig
import com.example.glimpse.SharedViewModel
import com.example.glimpse.voice.WakeWordListener
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig


class ChatManager {
    companion object {
        // List of words to trigger taking picture
        private val cameraWords = listOf("photo", "image", "picture", "capture", "camera")
        fun containsCameraWord(text: String): Boolean {
            return cameraWords.any { text.contains(it, ignoreCase = true) }
        }
    }

    private val config = generationConfig {
        temperature = 0.1f // More deterministic for demo
        maxOutputTokens = 200
    }
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_KEY,
        generationConfig = config,
        systemInstruction = content { text("Keep all of your responses short and concise. Do not say the word Gemini") }
    )
    private val chat by lazy { generativeModel.startChat() }

    // Function to send the speech to Gemini and update the response
    suspend fun sendToGemini(speech: String? = null, image: Bitmap? = null): String {
        val inputContent = content {
            if (image != null) {
                image(image)
                if (speech == null)
                    text("Describe what I am looking at")
            }
            if (speech != null)
                text(speech)
        }
        Log.d("Gemini", "Sending: $speech")

        try {
            val response = chat.sendMessage(inputContent)
            return "${response.text}"
        } catch (e: Exception) {
            // Fallback in case gemini-2.0-flash is down
            try {
                val backupModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = BuildConfig.GEMINI_KEY,
                    generationConfig = config,
                    systemInstruction = content { text("Keep all of your responses short and concise. Do not say the word Gemini") }
                )
                val backupResponse = backupModel.generateContent(inputContent)
                return "${backupResponse.text}"
            } catch (e: Exception) {
                return "Sorry, please ask something else"
            }
        }
    }

    fun startSpeechToText(
        speechRecognizer: SpeechRecognizer?,
        sharedViewModel: SharedViewModel,
        chatWordListener: WakeWordListener?,
        finished: (ArrayList<String>?) -> Unit
    ) {
        if (speechRecognizer == null)
            return
        // Picovoice interferes with speech recognition, so stop listening
        chatWordListener?.stopListening()
        sharedViewModel.wakeWordListener?.stopListening()

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {
                // Restart wake word listening
                sharedViewModel.wakeWordListener?.startListening()
                chatWordListener?.startListening()
            }

            override fun onError(i: Int) {
                // Restart wake word listening
                Log.d("WakeWord", "Error $i")
                sharedViewModel.wakeWordListener?.startListening()
                chatWordListener?.startListening()
                finished(null)
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
            override fun onResults(bundle: Bundle) {
                finished(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))
            }
        })

        speechRecognizer.startListening(speechRecognizerIntent)
    }
}
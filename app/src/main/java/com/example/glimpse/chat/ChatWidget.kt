package com.example.glimpse.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.glimpse.SharedViewModel
import com.example.glimpse.chat.ChatManager.Companion.containsCameraWord
import com.example.glimpse.utility.OrientationListener
import com.example.glimpse.voice.PorcupineWakeWordListener
import com.example.glimpse.voice.WakeWordListener
import kotlinx.coroutines.launch
import java.util.Locale

/*
FR19 - Chat.Widget
FR23 - Chat.TextToSpeech
Chat widget. The user can chat with gemini by left clicking or using the wake word "gemini".
Gemini's response will be displayed to the user and read aloud. The user can also include a photo
by either saying one of the "cameraWords" or right clicking
 */

@Composable
fun ChatWidget(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var geminiResponse by remember { mutableStateOf("") }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsInitialized by remember { mutableStateOf(false) }
    var chatWordListener by remember { mutableStateOf<WakeWordListener?>(null) }
    var wakeWordDetected by remember { mutableStateOf(false) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var speak by remember { mutableStateOf(false) }
    var orientation by remember { mutableIntStateOf(ORIENTATION_UNKNOWN) }
    val chatManager = remember { ChatManager() }

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        if (orientation == ORIENTATION_UNKNOWN) {
            // Mirror bitmap
            val imageTransform =
                Matrix().apply { postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) }
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, imageTransform, false
            )
        }
        return bitmap
    }

    fun takePhoto(finished: (Bitmap?) -> Unit) {
        imageCapture?.takePicture(ContextCompat.getMainExecutor(context), object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                finished(imageProxyToBitmap(image))
            }
        })
    }

    fun startPhotoMessage() {
        takePhoto { image ->
            scope.launch {
                geminiResponse = chatManager.sendToGemini(image = image)
                speak = true
            }
        }
    }

    fun startVoiceMessage() {
        geminiResponse = "..."
        chatManager.startSpeechToText(speechRecognizer, sharedViewModel, chatWordListener) { result ->
            if (result != null) {
                val userSpeech = result[0]
                if (containsCameraWord(userSpeech)) {
                    takePhoto { image ->
                        scope.launch {
                            geminiResponse =
                                chatManager.sendToGemini(speech = userSpeech, image = image)
                            speak = true
                        }
                    }
                } else {
                    scope.launch {
                        geminiResponse = chatManager.sendToGemini(speech = userSpeech)
                        speak = true
                    }
                }
            } else {
                geminiResponse = ""
            }
        }
    }

    LaunchedEffect(wakeWordDetected) {
        if (wakeWordDetected) {
            startVoiceMessage()
            wakeWordDetected = false
        }
    }

    // FR23 - Chat.TextToSpeech
    // Chat text to speech call
    if (isTtsInitialized && speak) {
        geminiResponse = geminiResponse.replace("\n", "")
        Log.d("Gemini", "Speaking: $geminiResponse")
        speak = false
        tts?.speak(geminiResponse, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    LifecycleResumeEffect(Unit) {
        val orientationListener = OrientationListener(context) { orientation = it }
        orientationListener.startListening()

        // STT and TTS
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.CANADA
                isTtsInitialized = true
            }
        }

        // Setup camera
        if (cameraProvider == null) {
            imageCapture =
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(context.resources.configuration.orientation)
                    .build()
            cameraProvider = ProcessCameraProvider.getInstance(context).get()
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                imageCapture
            )
        }

        // Setup hotword detection
        if (chatWordListener == null) {
            chatWordListener =
                PorcupineWakeWordListener(
                    context,
                    "Gemini.ppn",
                    onWakeWordDetected = {
                        wakeWordDetected = true
                    }
                )
        }
        chatWordListener?.startListening()

        onPauseOrDispose {
            chatWordListener?.stopListening()
            speechRecognizer?.destroy()
            chatWordListener?.stopListening()
            cameraProvider?.unbindAll()
            tts?.stop()
            tts?.shutdown()
            orientationListener.stopListening()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 16.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            if (event.buttons.isSecondaryPressed) {
                                // Right click
                                startPhotoMessage()
                            } else {
                                // Left click
                                startVoiceMessage()
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 96.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = geminiResponse,
                style = TextStyle(
                    color = sharedViewModel.hudForegroundColor,
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = sharedViewModel.selectedFont.fontResource,
                ),
                modifier = Modifier.testTag("chatText")
            )
        }
    }
}

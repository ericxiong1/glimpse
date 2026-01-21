package com.example.glimpse.voice

import com.example.glimpse.BuildConfig
import ai.picovoice.picovoice.PicovoiceException
import ai.picovoice.picovoice.PicovoiceInferenceCallback
import ai.picovoice.picovoice.PicovoiceManager
import ai.picovoice.picovoice.PicovoiceWakeWordCallback
import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import ai.picovoice.rhino.RhinoInference
import android.content.Context
import android.util.Log

/*
FR7 - Control.Voice
Listener for user voice commands
 */

abstract class WakeWordListener(
    val context: Context,
    val keywordPath: String,
    val contextPath: String = "",
    val onIntentRecognized: (String, Map<String, String>) -> Unit = {_, _ ->},
    val onWakeWordDetected: () -> Unit = {}
) {

    abstract fun startListening()

    abstract fun stopListening()
}

class PicovoiceWakeWordListener(
    context: Context,
    keywordPath: String,
    contextPath: String = "",
    onIntentRecognized: (String, Map<String, String>) -> Unit = {_, _ ->},
    onWakeWordDetected: () -> Unit = {}
) : WakeWordListener(context, keywordPath, contextPath, onIntentRecognized, onWakeWordDetected) {
    private var picovoiceManager: PicovoiceManager? = null
    override fun startListening() {
       if (picovoiceManager == null) {
           try {
               val wakeWordCallback = PicovoiceWakeWordCallback {
                   Log.d("Picovoice", "Wake word detected!")
                   onWakeWordDetected()
               }
               val builder = PicovoiceManager.Builder()
                   .setAccessKey(BuildConfig.PICOVOICE_ACCESS_KEY)
                   .setKeywordPath(keywordPath)
                   .setWakeWordCallback(wakeWordCallback)

               if (contextPath != "") {
                   val inferenceCallback = PicovoiceInferenceCallback { inference: RhinoInference ->
                       if (inference.isUnderstood) {
                           Log.d(
                               "Picovoice",
                               "Intent: ${inference.intent}, Slots: ${inference.slots}"
                           )
                           onIntentRecognized(inference.intent, inference.slots)
                       } else {
                           Log.d("Picovoice", "Unrecognized command.")
                       }
                   }

                   builder
                       .setContextPath(contextPath)
                       .setInferenceCallback(inferenceCallback)
               }

               picovoiceManager = builder.build(context)
               picovoiceManager?.start()
               Log.d("Picovoice", "Listening for wake word...")
           } catch (e: PicovoiceException) {
               Log.e("Picovoice", "Failed to initialize Picovoice: ${e.message}")
           }
       }
    }

    override fun stopListening() {
        picovoiceManager?.stop()
        picovoiceManager?.delete()
        picovoiceManager = null
    }
}

class PorcupineWakeWordListener(
    context: Context,
    keywordPath: String,
    onWakeWordDetected: () -> Unit = {}
) : WakeWordListener(context, keywordPath, "", {_,_->}, onWakeWordDetected) {
    private var porcupineManager: PorcupineManager? = null
    override fun startListening() {
        if (porcupineManager == null) {
            try {
                val wakeWordCallback = PorcupineManagerCallback {
                    Log.d("Porcupine", "Wake word detected!")
                    onWakeWordDetected()
                }
                val builder = PorcupineManager.Builder()
                    .setAccessKey(BuildConfig.PICOVOICE_ACCESS_KEY)
                    .setKeywordPath(keywordPath)

                porcupineManager = builder.build(context, wakeWordCallback)
                porcupineManager?.start()
                Log.d("Porcupine", "Listening for wake word...")
            } catch (e: PorcupineException) {
                Log.e("Porcupine", "Failed to initialize Porcupine: ${e.message}")
            }
        }
    }

    override fun stopListening() {
        porcupineManager?.stop()
        porcupineManager?.delete()
        porcupineManager = null
    }
}


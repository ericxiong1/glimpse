package com.example.glimpse.face

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.nio.ByteBuffer
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.max

/*
FR20 - Recognition.Upload
FR21 - Recognition.Widget
Class to get face embeddings using FaceNet tflite model.
Implementation derived from https://github.com/shubham0204/OnDevice-Face-Recognition-Android/blob/main/app/src/main/java/com/ml/shubham0204/facenet_android/domain/embeddings/FaceNet.kt
 */

class FaceNet private constructor(context: Context) {
    private val imgSize = 160 // Input image size for FaceNet model
    private val embeddingDim = 512 // Output embedding size
    private val imageTensorProcessor =
        ImageProcessor.Builder().add(ResizeOp(imgSize, imgSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(StandardizeOp()).build()
    private var interpreter: Interpreter

    var detector: FaceDetector? = null

    init {
        val interpreterOptions = Interpreter.Options().apply {
            // Add the GPU Delegate if supported.
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                addDelegate(GpuDelegate(CompatibilityList().bestOptionsForThisDevice))
            } else {
                numThreads = max(4, Runtime.getRuntime().availableProcessors() / 2)
            }
            useXNNPACK = true
            useNNAPI = true
        }
        interpreter =
            Interpreter(FileUtil.loadMappedFile(context, "facenet_512.tflite"), interpreterOptions)
        interpreter.allocateTensors()

        // Check if the architecture is x86_64, mediapipe is not supported for these devices
        val architecture = Build.SUPPORTED_ABIS.firstOrNull() ?: ""
        if (!architecture.contains("x86_64", ignoreCase = true))
            detector = createFaceDetector(context, "blaze_face_short_range.tflite", RunningMode.IMAGE)
    }

    private fun createFaceDetector(context: Context, modelName: String, runningMode: RunningMode): FaceDetector {
        val baseOptions = BaseOptions.builder().setModelAssetPath(modelName).build()
        return FaceDetector.createFromOptions(
            context,
            FaceDetector.FaceDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(runningMode)
                .build()
        )
    }

    // Gets a face embedding using FaceNet
    suspend fun getFaceEmbedding(image: Bitmap): FloatArray = withContext(Dispatchers.Default) {
        val imageBuffer = imageTensorProcessor.process(TensorImage.fromBitmap(image)).buffer
        return@withContext runFaceNet(imageBuffer)
    }

    // Run the FaceNet model
    private fun runFaceNet(inputs: ByteBuffer): FloatArray {
        val faceNetModelOutputs = Array(1) { FloatArray(embeddingDim) }
        interpreter.run(inputs, faceNetModelOutputs)
        return faceNetModelOutputs[0]
    }

    // Op to perform standardization
    private class StandardizeOp : TensorOperator {
        override fun apply(p0: TensorBuffer?): TensorBuffer {
            val pixels = p0!!.floatArray
            val mean = pixels.average().toFloat()
            var std = sqrt(pixels.map { pi -> (pi - mean).pow(2) }.sum() / pixels.size.toFloat())
            std = max(std, 1f / sqrt(pixels.size.toFloat()))
            for (i in pixels.indices) {
                pixels[i] = (pixels[i] - mean) / std
            }
            return TensorBufferFloat.createFixedSize(p0.shape, DataType.FLOAT32).apply {
                loadArray(pixels)
            }

        }
    }

    // Singleton instance
    companion object {
        @Volatile
        private var instance: FaceNet? = null

        fun getInstance(context: Context): FaceNet {
            return instance ?: synchronized(this) {
                instance ?: FaceNet(context.applicationContext).also { instance = it }
            }
        }
    }
}
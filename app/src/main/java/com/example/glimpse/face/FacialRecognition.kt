package com.example.glimpse.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

/*
FR20 - Recognition.Upload
FR21 - Recognition.Widget
Class to provide Facial detection + recognition operations using FaceNet and ML Kit.
 */

class FacialRecognition(context: Context) {
    private val faceNet = FaceNet.getInstance(context)
    private val x86_64Detector = FaceDetection.getClient()

    data class EmbeddingResult(
        val embedding: FloatArray?,
        val multipleFaces: Boolean
    )

    data class FaceRecognitionResult(
        val person: Person?,
        val detected: Boolean
    )

    suspend fun processBitmap(bitmap: Bitmap): EmbeddingResult = withContext(Dispatchers.Default) {
        val faceDim: Rect
        val size: Int

        // Check if the architecture is x86_64, mediapipe is not supported for these devices
        // Workaround of using mlkit in this case (significantly slower)
        val architecture = Build.SUPPORTED_ABIS.firstOrNull() ?: ""
        if (architecture.contains("x86_64", ignoreCase = true)) {
            val faces = x86_64Detector.process(InputImage.fromBitmap(bitmap, 0)).await()
            if (faces.isEmpty()) {
                // No face detected
                return@withContext EmbeddingResult(null, false)
            }
            // Find the largest face based on bounding box are
            faceDim =
                faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }!!.boundingBox
            size = faces.size
        } else {
            val faces =
                faceNet.detector!!.detect(BitmapImageBuilder(bitmap).build()).detections()
            if (faces.isEmpty()) {
                // No Face detected
                return@withContext EmbeddingResult(null, false)
            }
            // Find the largest face based on bounding box are
            val faceDimF =
                faces.maxByOrNull { it.boundingBox().width() * it.boundingBox().height() }!!
                    .boundingBox()
            faceDim = Rect(
                faceDimF.left.toInt(),
                faceDimF.top.toInt(),
                faceDimF.right.toInt(),
                faceDimF.bottom.toInt()
            )
            size = faces.size
        }

        if (faceDim.left < 0 || faceDim.top < 0 || faceDim.right > bitmap.width || faceDim.bottom > bitmap.height) {
            // Face dim out of image bounds
            return@withContext EmbeddingResult(null, size > 1)
        }

        // Extract the face from the bitmap
        val face = Bitmap.createBitmap(
            bitmap,
            faceDim.left,
            faceDim.top,
            faceDim.width(),
            faceDim.height()
        )

        // Use FaceNet to get the embedding
        val embedding = faceNet.getFaceEmbedding(face)
        return@withContext EmbeddingResult(embedding, size > 1)
    }

    suspend fun findFace(frame: Bitmap): FaceRecognitionResult = withContext(Dispatchers.IO) {
        // Convert face in frame to embedding
        val result = processBitmap(frame)
        val detectedEmbedding =
            result.embedding ?: return@withContext FaceRecognitionResult(null, false)

        // If cosine similarity < threshold then there is no match
        val threshold = 0.6f
        val faceBox = ObjectBox.store.boxFor(Face::class)


        val face = if (Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()) {
            // 64-bit: Use nearest neighbor search
            faceBox
                .query(Face_.faceEmbedding.nearestNeighbors(detectedEmbedding, 10))
                .build()
                .findWithScores()
                .filter { it.score < (1 - threshold) } // Score is 1 - cosine similarity
                .minByOrNull { it.score }
                ?.get()
        } else {
            // 32-bit: Use manual vector search as nearest neighbor search is not supported
            faceBox.query().build().find()
                .map { it to cosineSimilarity(it.faceEmbedding, detectedEmbedding) }
                .filter { it.second > threshold }
                .sortedBy { it.second }
                .maxByOrNull { it.second }
                ?.first
        } ?: return@withContext FaceRecognitionResult(null, true)

        // Return the person detected
        return@withContext FaceRecognitionResult(face.person.target, true)
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        // Calculate cosine similarity between two vectors
        val dotProduct = a.indices.fold(0f) { acc, i -> acc + (a[i] * b[i]) }
        val normA = sqrt(a.fold(0f) { acc, x -> acc + (x * x) })
        val normB = sqrt(b.fold(0f) { acc, x -> acc + (x * x) })
        return if (normA == 0f || normB == 0f) 0f else dotProduct / (normA * normB)
    }

    // Singleton instance
    companion object {
        @Volatile
        private var instance: FacialRecognition? = null

        fun getInstance(context: Context): FacialRecognition {
            return instance ?: synchronized(this) {
                instance ?: FacialRecognition(context.applicationContext).also { instance = it }
            }
        }
    }
}
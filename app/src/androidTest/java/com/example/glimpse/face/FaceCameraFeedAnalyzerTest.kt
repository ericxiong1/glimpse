package com.example.glimpse.face

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.glimpse.R
import com.example.glimpse.SharedViewModel
import io.mockk.mockk
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaceCameraFeedAnalyzerTest {
    private lateinit var context: Context
    private lateinit var analyzer: FaceCameraFeedAnalyzer
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var faceBox: Box<Face>
    private lateinit var personBox: Box<Person>

    @Before
    fun setup() = runTest {
        context = ApplicationProvider.getApplicationContext()
        sharedViewModel = mockk(relaxed = true)

        faceBox = ObjectBox.store.boxFor(Face::class)
        personBox = ObjectBox.store.boxFor(Person::class)
        faceBox.removeAll()
        personBox.removeAll()

        // Setup database
        val testImage = BitmapFactory.decodeResource(context.resources, R.drawable.face)
        val embeddingResult = FacialRecognition.getInstance(context).processBitmap(testImage)
        requireNotNull(embeddingResult.embedding) { "No face detected in test image" }
        val face = Face(photoPath = "test_path", faceEmbedding = embeddingResult.embedding!!)
        faceBox.put(face)
        val person = Person(name = "Test Person", information = "Test Info")
        person.faces.add(face)
        personBox.put(person)
    }

    @Test
    fun testProcessImageFace() = runTest {
        val testImage = BitmapFactory.decodeResource(context.resources, R.drawable.face)

        // Mock callback to capture the result
        val deferredResult = CompletableDeferred<FacialRecognition.FaceRecognitionResult?>()
        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel) { recognitionResult ->
            deferredResult.complete(recognitionResult)
        }

        // Process image
        analyzer.processImage(testImage) {}

        val result = deferredResult.await()
        assertNotNull(result)
        assertTrue(result!!.detected)
        assertNotNull(result.person)
        assertEquals("Test Person", result.person!!.name)
    }

    @Test
    fun testProcessImageNoFace() = runTest {
        val testImage = BitmapFactory.decodeResource(context.resources, R.drawable.noface)

        // Mock callback to capture the result
        val deferredResult = CompletableDeferred<FacialRecognition.FaceRecognitionResult?>()
        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel) { recognitionResult ->
            deferredResult.complete(recognitionResult)
        }

        // Process image
        analyzer.processImage(testImage) {}

        val result = deferredResult.await()
        assertNotNull(result)
        assertFalse(result!!.detected)
        assertNull(result.person)
    }

    @Test
    fun testProcessImageWrongFace() = runTest {
        val testImage = BitmapFactory.decodeResource(context.resources, R.drawable.wrongface)

        // Mock callback to capture the result
        val deferredResult = CompletableDeferred<FacialRecognition.FaceRecognitionResult?>()
        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel) { recognitionResult ->
            deferredResult.complete(recognitionResult)
        }

        // Process image
        analyzer.processImage(testImage) {}

        val result = deferredResult.await()
        assertNotNull(result)
        assertTrue(result!!.detected)
        assertNull(result.person)
    }
}

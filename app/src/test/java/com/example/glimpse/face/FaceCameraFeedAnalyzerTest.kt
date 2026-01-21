package com.example.glimpse.face

import android.content.Context
import com.example.glimpse.SharedViewModel
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FaceCameraFeedAnalyzerTest {
    private lateinit var context: Context
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var onFaceRecognized: (FacialRecognition.FaceRecognitionResult) -> Unit
    private lateinit var analyzer: FaceCameraFeedAnalyzer

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        sharedViewModel = mockk(relaxed = true)
        onFaceRecognized = mockk(relaxed = true)


        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun resultDetected() = runBlocking {
        mockkObject(FacialRecognition)
        val mockRecognition = mockk<FacialRecognition>()
        every { FacialRecognition.getInstance(context) } returns mockRecognition
        val mockResult = FacialRecognition.FaceRecognitionResult(mockk(), true)
        coEvery { mockRecognition.findFace(any()) } returns mockResult
        val finished = CompletableDeferred<Unit>()

        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel, onFaceRecognized)
        analyzer.processImage(mockk(relaxed = true)) {
            assert(analyzer.lastSuccessTime > 0)
            coVerify { onFaceRecognized(mockResult) }
            finished.complete(Unit)
        }
        finished.await()
    }

    @Test
    fun resultNotDetectedLong() = runBlocking {
        mockkObject(FacialRecognition)
        val mockRecognition = mockk<FacialRecognition>()
        every { FacialRecognition.getInstance(context) } returns mockRecognition
        val mockResult = FacialRecognition.FaceRecognitionResult(mockk(), false)
        coEvery { mockRecognition.findFace(any()) } returns mockResult
        val finished = CompletableDeferred<Unit>()

        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel, onFaceRecognized)
        analyzer.lastSuccessTime = System.currentTimeMillis() - 3000
        analyzer.processImage(mockk(relaxed = true)) {
            coVerify { onFaceRecognized(mockResult) }
            finished.complete(Unit)
        }
        finished.await()
    }

    @Test
    fun resultNotDetectedShort() = runBlocking {
        mockkObject(FacialRecognition)
        val mockRecognition = mockk<FacialRecognition>()
        every { FacialRecognition.getInstance(context) } returns mockRecognition
        val mockResult = FacialRecognition.FaceRecognitionResult(mockk(), false)
        coEvery { mockRecognition.findFace(any()) } returns mockResult
        val finished = CompletableDeferred<Unit>()

        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel, onFaceRecognized)
        analyzer.lastSuccessTime = System.currentTimeMillis()
        analyzer.processImage(mockk(relaxed = true)) {
            coVerify(exactly = 0) { onFaceRecognized(mockResult) }
            finished.complete(Unit)
        }
        finished.await()
    }

    @Test
    fun resultDetectedNoPerson() = runBlocking {
        mockkObject(FacialRecognition)
        val mockRecognition = mockk<FacialRecognition>()
        every { FacialRecognition.getInstance(context) } returns mockRecognition
        val mockResult = FacialRecognition.FaceRecognitionResult(null, true)
        coEvery { mockRecognition.findFace(any()) } returns mockResult
        val finished = CompletableDeferred<Unit>()

        analyzer = FaceCameraFeedAnalyzer(context, sharedViewModel, onFaceRecognized)
        analyzer.lastSuccessTime = System.currentTimeMillis()
        analyzer.processImage(mockk(relaxed = true)) {
            coVerify(exactly = 0) { onFaceRecognized(mockResult) }
            finished.complete(Unit)
        }
        finished.await()
    }
}
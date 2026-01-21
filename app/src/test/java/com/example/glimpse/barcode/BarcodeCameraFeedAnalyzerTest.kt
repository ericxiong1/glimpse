package com.example.glimpse.barcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.example.glimpse.SharedViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
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
class BarcodeCameraFeedAnalyzerTest {
    private lateinit var context: Context
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var onBarcodeScanned: (BarcodeScanningResult) -> Unit
    private lateinit var analyzer: BarcodeCameraFeedAnalyzer

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        sharedViewModel = mockk(relaxed = true)
        onBarcodeScanned = mockk(relaxed = true)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun teardown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun resultDetected() = runBlocking {
        mockkStatic(BarcodeScanning::class)
        val mockScanner = mockk<BarcodeScanner>(relaxed = true)
        every { BarcodeScanning.getClient() } returns mockScanner

        mockkStatic(InputImage::class)
        val mockInputImage = mockk<InputImage>()
        every { InputImage.fromBitmap(any(), 0) } returns mockInputImage

        val task = mockk<Task<List<Barcode>>>(relaxed = true)
        every { mockScanner.process(mockInputImage) } returns task

        val onSuccessSlot = slot<OnSuccessListener<List<Barcode>>>()
        val onCompleteSlot = slot<OnCompleteListener<List<Barcode>>>()

        every { task.addOnSuccessListener(capture(onSuccessSlot)) } returns task
        every { task.addOnCompleteListener(capture(onCompleteSlot)) } returns task

        val mockBitmap = mockk<Bitmap>(relaxed = true)
        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100

        val finished = CompletableDeferred<Unit>()
        analyzer = BarcodeCameraFeedAnalyzer(context, sharedViewModel, onBarcodeScanned)
        analyzer.processImage(mockk(relaxed = true)) {
            assert(analyzer.lastSuccessTime > 0)
            finished.complete(Unit)
        }

        val rect1 = mockk<Rect> {
        every { centerX() } returns 50
        every { centerY() } returns 50
        }
        val barcode1 = mockk<Barcode> {
            every { boundingBox } returns rect1
        }
        val rect2 = mockk<Rect> {
            every { centerX() } returns 80
            every { centerY() } returns 80
        }
        val barcode2 = mockk<Barcode> {
            every { boundingBox } returns rect2
        }
        val barcodes = listOf(barcode1, barcode2)

        onSuccessSlot.captured.onSuccess(barcodes)
        onCompleteSlot.captured.onComplete(task)

        finished.await()
        verify { onBarcodeScanned(BarcodeScanningResult(true, barcode1)) }
    }

    @Test
    fun resultDetectedSoon() = runBlocking {
        mockkStatic(BarcodeScanning::class)
        val mockScanner = mockk<BarcodeScanner>(relaxed = true)
        every { BarcodeScanning.getClient() } returns mockScanner

        mockkStatic(InputImage::class)
        val mockInputImage = mockk<InputImage>()
        every { InputImage.fromBitmap(any(), 0) } returns mockInputImage

        val task = mockk<Task<List<Barcode>>>(relaxed = true)
        every { mockScanner.process(mockInputImage) } returns task

        val onSuccessSlot = slot<OnSuccessListener<List<Barcode>>>()
        val onCompleteSlot = slot<OnCompleteListener<List<Barcode>>>()

        every { task.addOnSuccessListener(capture(onSuccessSlot)) } returns task
        every { task.addOnCompleteListener(capture(onCompleteSlot)) } returns task

        val mockBitmap = mockk<Bitmap>(relaxed = true)
        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100

        val finished = CompletableDeferred<Unit>()
        analyzer = BarcodeCameraFeedAnalyzer(context, sharedViewModel, onBarcodeScanned)
        analyzer.lastSuccessTime = System.currentTimeMillis()
        analyzer.processImage(mockk(relaxed = true)) {
            assert(analyzer.lastSuccessTime > 0)
            finished.complete(Unit)
        }

        val rect1 = mockk<Rect> {
            every { centerX() } returns 50
            every { centerY() } returns 50
        }
        val barcode1 = mockk<Barcode> {
            every { boundingBox } returns rect1
        }
        val rect2 = mockk<Rect> {
            every { centerX() } returns 80
            every { centerY() } returns 80
        }
        val barcode2 = mockk<Barcode> {
            every { boundingBox } returns rect2
        }
        val barcodes = listOf(barcode1, barcode2)

        onSuccessSlot.captured.onSuccess(barcodes)
        onCompleteSlot.captured.onComplete(task)

        finished.await()
        verify { onBarcodeScanned(BarcodeScanningResult(true, barcode1)) }
    }

    @Test
    fun noResult() = runBlocking {
        mockkStatic(BarcodeScanning::class)
        val mockScanner = mockk<BarcodeScanner>(relaxed = true)
        every { BarcodeScanning.getClient() } returns mockScanner

        mockkStatic(InputImage::class)
        val mockInputImage = mockk<InputImage>()
        every { InputImage.fromBitmap(any(), 0) } returns mockInputImage

        val task = mockk<Task<List<Barcode>>>(relaxed = true)
        every { mockScanner.process(mockInputImage) } returns task

        val onSuccessSlot = slot<OnSuccessListener<List<Barcode>>>()
        val onCompleteSlot = slot<OnCompleteListener<List<Barcode>>>()

        every { task.addOnSuccessListener(capture(onSuccessSlot)) } returns task
        every { task.addOnCompleteListener(capture(onCompleteSlot)) } returns task

        val finished = CompletableDeferred<Unit>()
        analyzer = BarcodeCameraFeedAnalyzer(context, sharedViewModel, onBarcodeScanned)
        analyzer.lastSuccessTime = 0
        analyzer.processImage(mockk(relaxed = true)) {
            assertEquals(0L, analyzer.lastSuccessTime)
            finished.complete(Unit)
        }

        onSuccessSlot.captured.onSuccess(listOf())
        onCompleteSlot.captured.onComplete(task)

        finished.await()
        verify { onBarcodeScanned(BarcodeScanningResult(false, null)) }
    }

    @Test
    fun noResultSoon() = runBlocking {
        mockkStatic(BarcodeScanning::class)
        val mockScanner = mockk<BarcodeScanner>(relaxed = true)
        every { BarcodeScanning.getClient() } returns mockScanner

        mockkStatic(InputImage::class)
        val mockInputImage = mockk<InputImage>()
        every { InputImage.fromBitmap(any(), 0) } returns mockInputImage

        val task = mockk<Task<List<Barcode>>>(relaxed = true)
        every { mockScanner.process(mockInputImage) } returns task

        val onSuccessSlot = slot<OnSuccessListener<List<Barcode>>>()
        val onCompleteSlot = slot<OnCompleteListener<List<Barcode>>>()

        every { task.addOnSuccessListener(capture(onSuccessSlot)) } returns task
        every { task.addOnCompleteListener(capture(onCompleteSlot)) } returns task

        val finished = CompletableDeferred<Unit>()
        analyzer = BarcodeCameraFeedAnalyzer(context, sharedViewModel, onBarcodeScanned)
        analyzer.lastSuccessTime = System.currentTimeMillis()
        analyzer.processImage(mockk(relaxed = true)) {
            finished.complete(Unit)
        }

        onSuccessSlot.captured.onSuccess(listOf())
        onCompleteSlot.captured.onComplete(task)

        finished.await()
        verify(exactly=0) { onBarcodeScanned(BarcodeScanningResult(false, null)) }
    }

    @Test
    fun resultNoBox() = runBlocking {
        mockkStatic(BarcodeScanning::class)
        val mockScanner = mockk<BarcodeScanner>(relaxed = true)
        every { BarcodeScanning.getClient() } returns mockScanner

        mockkStatic(InputImage::class)
        val mockInputImage = mockk<InputImage>()
        every { InputImage.fromBitmap(any(), 0) } returns mockInputImage

        val task = mockk<Task<List<Barcode>>>(relaxed = true)
        every { mockScanner.process(mockInputImage) } returns task

        val onSuccessSlot = slot<OnSuccessListener<List<Barcode>>>()
        val onCompleteSlot = slot<OnCompleteListener<List<Barcode>>>()

        every { task.addOnSuccessListener(capture(onSuccessSlot)) } returns task
        every { task.addOnCompleteListener(capture(onCompleteSlot)) } returns task

        val mockBitmap = mockk<Bitmap>(relaxed = true)
        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100

        val finished = CompletableDeferred<Unit>()
        analyzer = BarcodeCameraFeedAnalyzer(context, sharedViewModel, onBarcodeScanned)
        analyzer.processImage(mockk(relaxed = true)) {
            assert(analyzer.lastSuccessTime > 0)
            finished.complete(Unit)
        }

        val barcode = mockk<Barcode> {
            every { boundingBox } returns null
        }
        val barcodes = listOf(barcode, barcode)

        onSuccessSlot.captured.onSuccess(barcodes)
        onCompleteSlot.captured.onComplete(task)

        finished.await()
        verify { onBarcodeScanned(BarcodeScanningResult(true, barcode)) }
    }
}
package com.example.glimpse.barcode

import android.content.Context
import android.graphics.Bitmap
import com.example.glimpse.SharedViewModel
import com.example.glimpse.camera.CameraFeedAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/*
FR13 - Barcode.Scanning
FR15 - Barcode.Priority
Camera feed analyzer. Converts frames to bitmaps, rotates them so the ground is down
and then sends them for barcode scanning. Prioritizes central most barcode
 */
class BarcodeCameraFeedAnalyzer(
    context: Context,
    sharedViewModel: SharedViewModel,
    val onBarcodeScanned: (BarcodeScanningResult) -> Unit
) : CameraFeedAnalyzer<BarcodeScanningResult>(context, sharedViewModel) {

    public override fun processImage(bitmap: Bitmap, onFinished: () -> Unit) {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
        val image = InputImage.fromBitmap(bitmap, 0)
        var result = BarcodeScanningResult(false, null)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    // FR15 - Barcode.Priority: Get central barcode
                    val centralBarcode = barcodes.minByOrNull { barcode ->
                        val centerX = barcode.boundingBox?.centerX() ?: Int.MAX_VALUE
                        val centerY = barcode.boundingBox?.centerY() ?: Int.MAX_VALUE
                        val imageCenterX = bitmap.width / 2
                        val imageCenterY = bitmap.height / 2
                        (centerX - imageCenterX) * (centerX - imageCenterX) +
                                (centerY - imageCenterY) * (centerY - imageCenterY)
                    }
                    result = BarcodeScanningResult(true, centralBarcode)
                    lastSuccessTime = System.currentTimeMillis()
                }
            }
            .addOnCompleteListener {
                if (result.detected || System.currentTimeMillis() - lastSuccessTime > successTimeout)
                    onBarcodeScanned(result)
                barcodeScanner.close()
                onFinished()
            }
    }
}

data class BarcodeScanningResult(
    val detected: Boolean,
    val barcode: Barcode?
)
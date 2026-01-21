package com.example.glimpse.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

/*
FR13 - Barcode.Scanning
FR19 - Chat.Widget
FR20 - Recognition.Upload
FR21 - Recognition.Widget
Helpers for various bitmap operations needed for camera based widgets
*/

fun orientBitmap(inputStream: InputStream, bitmap: Bitmap): Bitmap {
    val orientation = ExifInterface(inputStream).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
        else -> bitmap
    }
}

fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    val matrix = android.graphics.Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    // Get bitmap
    val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream)
    } ?: return null
    // Orient bitmap
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        orientBitmap(stream, bitmap)
    }
}


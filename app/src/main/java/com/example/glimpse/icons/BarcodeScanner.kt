package com.example.glimpse.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val BarcodeScanner: ImageVector
    get() {
        if (_BarcodeScanner != null) {
            return _BarcodeScanner!!
        }
        _BarcodeScanner = ImageVector.Builder(
            name = "BarcodeScanner",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(40f, 840f)
                lineTo(40f, 640f)
                lineTo(120f, 640f)
                lineTo(120f, 760f)
                lineTo(240f, 760f)
                lineTo(240f, 840f)
                lineTo(40f, 840f)
                close()
                moveTo(720f, 840f)
                lineTo(720f, 760f)
                lineTo(840f, 760f)
                lineTo(840f, 640f)
                lineTo(920f, 640f)
                lineTo(920f, 840f)
                lineTo(720f, 840f)
                close()
                moveTo(160f, 720f)
                lineTo(160f, 240f)
                lineTo(240f, 240f)
                lineTo(240f, 720f)
                lineTo(160f, 720f)
                close()
                moveTo(280f, 720f)
                lineTo(280f, 240f)
                lineTo(320f, 240f)
                lineTo(320f, 720f)
                lineTo(280f, 720f)
                close()
                moveTo(400f, 720f)
                lineTo(400f, 240f)
                lineTo(480f, 240f)
                lineTo(480f, 720f)
                lineTo(400f, 720f)
                close()
                moveTo(520f, 720f)
                lineTo(520f, 240f)
                lineTo(640f, 240f)
                lineTo(640f, 720f)
                lineTo(520f, 720f)
                close()
                moveTo(680f, 720f)
                lineTo(680f, 240f)
                lineTo(720f, 240f)
                lineTo(720f, 720f)
                lineTo(680f, 720f)
                close()
                moveTo(760f, 720f)
                lineTo(760f, 240f)
                lineTo(800f, 240f)
                lineTo(800f, 720f)
                lineTo(760f, 720f)
                close()
                moveTo(40f, 320f)
                lineTo(40f, 120f)
                lineTo(240f, 120f)
                lineTo(240f, 200f)
                lineTo(120f, 200f)
                lineTo(120f, 320f)
                lineTo(40f, 320f)
                close()
                moveTo(840f, 320f)
                lineTo(840f, 200f)
                lineTo(720f, 200f)
                lineTo(720f, 120f)
                lineTo(920f, 120f)
                lineTo(920f, 320f)
                lineTo(840f, 320f)
                close()
            }
        }.build()

        return _BarcodeScanner!!
    }

@Suppress("ObjectPropertyName")
private var _BarcodeScanner: ImageVector? = null

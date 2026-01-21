package com.example.glimpse.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Directions: ImageVector
    get() {
        if (_Directions != null) {
            return _Directions!!
        }
        _Directions = ImageVector.Builder(
            name = "Directions",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(320f, 600f)
                lineTo(400f, 600f)
                lineTo(400f, 480f)
                lineTo(540f, 480f)
                lineTo(540f, 580f)
                lineTo(680f, 440f)
                lineTo(540f, 300f)
                lineTo(540f, 400f)
                lineTo(360f, 400f)
                quadTo(343f, 400f, 331.5f, 411.5f)
                quadTo(320f, 423f, 320f, 440f)
                lineTo(320f, 600f)
                close()
                moveTo(480f, 880f)
                quadTo(465f, 880f, 450.5f, 874f)
                quadTo(436f, 868f, 424f, 856f)
                lineTo(104f, 536f)
                quadTo(92f, 524f, 86f, 509.5f)
                quadTo(80f, 495f, 80f, 480f)
                quadTo(80f, 465f, 86f, 450.5f)
                quadTo(92f, 436f, 104f, 424f)
                lineTo(424f, 104f)
                quadTo(436f, 92f, 450.5f, 86f)
                quadTo(465f, 80f, 480f, 80f)
                quadTo(495f, 80f, 509.5f, 86f)
                quadTo(524f, 92f, 536f, 104f)
                lineTo(856f, 424f)
                quadTo(868f, 436f, 874f, 450.5f)
                quadTo(880f, 465f, 880f, 480f)
                quadTo(880f, 495f, 874f, 509.5f)
                quadTo(868f, 524f, 856f, 536f)
                lineTo(536f, 856f)
                quadTo(524f, 868f, 509.5f, 874f)
                quadTo(495f, 880f, 480f, 880f)
                close()
                moveTo(320f, 640f)
                lineTo(480f, 800f)
                quadTo(480f, 800f, 480f, 800f)
                quadTo(480f, 800f, 480f, 800f)
                lineTo(800f, 480f)
                quadTo(800f, 480f, 800f, 480f)
                quadTo(800f, 480f, 800f, 480f)
                lineTo(480f, 160f)
                quadTo(480f, 160f, 480f, 160f)
                quadTo(480f, 160f, 480f, 160f)
                lineTo(160f, 480f)
                quadTo(160f, 480f, 160f, 480f)
                quadTo(160f, 480f, 160f, 480f)
                lineTo(320f, 640f)
                close()
                moveTo(480f, 480f)
                lineTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                lineTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                lineTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                lineTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                close()
            }
        }.build()

        return _Directions!!
    }

@Suppress("ObjectPropertyName")
private var _Directions: ImageVector? = null

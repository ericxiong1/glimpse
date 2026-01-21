package com.example.glimpse.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChatBubble: ImageVector
    get() {
        if (_ChatBubble != null) {
            return _ChatBubble!!
        }
        _ChatBubble = ImageVector.Builder(
            name = "ChatBubble",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(80f, 880f)
                lineTo(80f, 160f)
                quadTo(80f, 127f, 103.5f, 103.5f)
                quadTo(127f, 80f, 160f, 80f)
                lineTo(800f, 80f)
                quadTo(833f, 80f, 856.5f, 103.5f)
                quadTo(880f, 127f, 880f, 160f)
                lineTo(880f, 640f)
                quadTo(880f, 673f, 856.5f, 696.5f)
                quadTo(833f, 720f, 800f, 720f)
                lineTo(240f, 720f)
                lineTo(80f, 880f)
                close()
                moveTo(206f, 640f)
                lineTo(800f, 640f)
                quadTo(800f, 640f, 800f, 640f)
                quadTo(800f, 640f, 800f, 640f)
                lineTo(800f, 160f)
                quadTo(800f, 160f, 800f, 160f)
                quadTo(800f, 160f, 800f, 160f)
                lineTo(160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                lineTo(160f, 685f)
                lineTo(206f, 640f)
                close()
                moveTo(160f, 640f)
                lineTo(160f, 640f)
                lineTo(160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                lineTo(160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                lineTo(160f, 640f)
                quadTo(160f, 640f, 160f, 640f)
                quadTo(160f, 640f, 160f, 640f)
                close()
            }
        }.build()

        return _ChatBubble!!
    }

@Suppress("ObjectPropertyName")
private var _ChatBubble: ImageVector? = null

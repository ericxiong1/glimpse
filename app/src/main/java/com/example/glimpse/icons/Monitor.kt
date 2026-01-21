package com.example.glimpse.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Monitor: ImageVector
    get() {
        if (_Monitor != null) {
            return _Monitor!!
        }
        _Monitor = ImageVector.Builder(
            name = "Monitor",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(320f, 840f)
                lineTo(320f, 760f)
                lineTo(160f, 760f)
                quadTo(127f, 760f, 103.5f, 736.5f)
                quadTo(80f, 713f, 80f, 680f)
                lineTo(80f, 200f)
                quadTo(80f, 167f, 103.5f, 143.5f)
                quadTo(127f, 120f, 160f, 120f)
                lineTo(800f, 120f)
                quadTo(833f, 120f, 856.5f, 143.5f)
                quadTo(880f, 167f, 880f, 200f)
                lineTo(880f, 680f)
                quadTo(880f, 713f, 856.5f, 736.5f)
                quadTo(833f, 760f, 800f, 760f)
                lineTo(640f, 760f)
                lineTo(640f, 840f)
                lineTo(320f, 840f)
                close()
                moveTo(160f, 680f)
                lineTo(800f, 680f)
                quadTo(800f, 680f, 800f, 680f)
                quadTo(800f, 680f, 800f, 680f)
                lineTo(800f, 200f)
                quadTo(800f, 200f, 800f, 200f)
                quadTo(800f, 200f, 800f, 200f)
                lineTo(160f, 200f)
                quadTo(160f, 200f, 160f, 200f)
                quadTo(160f, 200f, 160f, 200f)
                lineTo(160f, 680f)
                quadTo(160f, 680f, 160f, 680f)
                quadTo(160f, 680f, 160f, 680f)
                close()
                moveTo(160f, 680f)
                quadTo(160f, 680f, 160f, 680f)
                quadTo(160f, 680f, 160f, 680f)
                lineTo(160f, 200f)
                quadTo(160f, 200f, 160f, 200f)
                quadTo(160f, 200f, 160f, 200f)
                lineTo(160f, 200f)
                quadTo(160f, 200f, 160f, 200f)
                quadTo(160f, 200f, 160f, 200f)
                lineTo(160f, 680f)
                quadTo(160f, 680f, 160f, 680f)
                quadTo(160f, 680f, 160f, 680f)
                close()
            }
        }.build()

        return _Monitor!!
    }

@Suppress("ObjectPropertyName")
private var _Monitor: ImageVector? = null

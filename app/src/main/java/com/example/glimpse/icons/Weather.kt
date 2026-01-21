package com.example.glimpse.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Weather: ImageVector
    get() {
        if (_Weather != null) {
            return _Weather!!
        }
        _Weather = ImageVector.Builder(
            name = "Weather",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(440f, 200f)
                lineTo(440f, 40f)
                lineTo(520f, 40f)
                lineTo(520f, 200f)
                lineTo(440f, 200f)
                close()
                moveTo(706f, 310f)
                lineTo(650f, 254f)
                lineTo(763f, 140f)
                lineTo(819f, 197f)
                lineTo(706f, 310f)
                close()
                moveTo(760f, 520f)
                lineTo(760f, 440f)
                lineTo(920f, 440f)
                lineTo(920f, 520f)
                lineTo(760f, 520f)
                close()
                moveTo(763f, 819f)
                lineTo(650f, 706f)
                lineTo(706f, 650f)
                lineTo(820f, 762f)
                lineTo(763f, 819f)
                close()
                moveTo(254f, 310f)
                lineTo(141f, 197f)
                lineTo(198f, 140f)
                lineTo(310f, 254f)
                lineTo(254f, 310f)
                close()
                moveTo(240f, 760f)
                lineTo(420f, 760f)
                quadTo(445f, 760f, 462.5f, 742.5f)
                quadTo(480f, 725f, 480f, 700f)
                quadTo(480f, 675f, 463f, 657.5f)
                quadTo(446f, 640f, 421f, 640f)
                lineTo(370f, 640f)
                lineTo(350f, 592f)
                quadTo(336f, 559f, 306f, 539.5f)
                quadTo(276f, 520f, 240f, 520f)
                quadTo(190f, 520f, 155f, 555f)
                quadTo(120f, 590f, 120f, 640f)
                quadTo(120f, 690f, 155f, 725f)
                quadTo(190f, 760f, 240f, 760f)
                close()
                moveTo(240f, 840f)
                quadTo(157f, 840f, 98.5f, 781.5f)
                quadTo(40f, 723f, 40f, 640f)
                quadTo(40f, 557f, 98.5f, 498.5f)
                quadTo(157f, 440f, 240f, 440f)
                quadTo(300f, 440f, 349.5f, 472.5f)
                quadTo(399f, 505f, 423f, 560f)
                lineTo(423f, 560f)
                lineTo(423f, 560f)
                quadTo(481f, 560f, 520.5f, 603f)
                quadTo(560f, 646f, 560f, 706f)
                quadTo(558f, 763f, 517.5f, 801.5f)
                quadTo(477f, 840f, 420f, 840f)
                lineTo(240f, 840f)
                close()
                moveTo(560f, 706f)
                quadTo(555f, 686f, 550f, 667f)
                quadTo(545f, 648f, 540f, 628f)
                quadTo(585f, 609f, 612.5f, 569f)
                quadTo(640f, 529f, 640f, 480f)
                quadTo(640f, 414f, 593f, 367f)
                quadTo(546f, 320f, 480f, 320f)
                quadTo(420f, 320f, 375f, 359f)
                quadTo(330f, 398f, 322f, 458f)
                quadTo(302f, 453f, 281f, 449f)
                quadTo(260f, 445f, 240f, 440f)
                quadTo(254f, 352f, 322.5f, 296f)
                quadTo(391f, 240f, 480f, 240f)
                quadTo(580f, 240f, 650f, 310f)
                quadTo(720f, 380f, 720f, 480f)
                quadTo(720f, 557f, 676f, 618.5f)
                quadTo(632f, 680f, 560f, 706f)
                close()
                moveTo(481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                quadTo(481f, 480f, 481f, 480f)
                close()
            }
        }.build()

        return _Weather!!
    }

@Suppress("ObjectPropertyName")
private var _Weather: ImageVector? = null

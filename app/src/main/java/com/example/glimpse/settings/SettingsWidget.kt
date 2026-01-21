package com.example.glimpse.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glimpse.SharedViewModel
/*
FR5 - Change.Brightness
FR24 - Change.Volume
FR9 - Notification.DoNotDisturb
UI components for changing brightness, volume, and dnd state
 */
@Composable
fun SettingsWidget(sharedViewModel: SharedViewModel) {
    Column(modifier = Modifier.padding(start = 200.dp, end = 200.dp)) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Volume", color = sharedViewModel.hudForegroundColor, fontSize = 24.sp, fontFamily = sharedViewModel.selectedFont.fontResource)
        Slider(
            value = sharedViewModel.volume,
            onValueChange = { sharedViewModel.updateVolume(it) },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = sharedViewModel.hudForegroundColor,
                activeTrackColor = sharedViewModel.hudForegroundColor,
                inactiveTrackColor = Color.DarkGray.copy(alpha = sharedViewModel.hudMenuOpacity)
            ),
            modifier = Modifier.testTag("volumeSlider")
        )
        Text("Brightness", color = sharedViewModel.hudForegroundColor, fontSize = 24.sp, fontFamily = sharedViewModel.selectedFont.fontResource)
        Slider(
            value = sharedViewModel.brightness,
            onValueChange = { sharedViewModel.updateBrightness(it) },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = sharedViewModel.hudForegroundColor,
                activeTrackColor = sharedViewModel.hudForegroundColor,
                inactiveTrackColor = Color.DarkGray.copy(alpha = sharedViewModel.hudMenuOpacity)
            ),
            modifier = Modifier.testTag("brightnessSlider")
        )
        Text("Do Not Disturb", color = sharedViewModel.hudForegroundColor, fontSize = 24.sp, fontFamily = sharedViewModel.selectedFont.fontResource,)
        Switch(
            checked = sharedViewModel.dnd,
            onCheckedChange = { sharedViewModel.updateDnd(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.DarkGray.copy(alpha = sharedViewModel.hudMenuOpacity),
                uncheckedThumbColor = sharedViewModel.hudForegroundColor,
                checkedTrackColor = sharedViewModel.hudForegroundColor,
                uncheckedTrackColor = Color.DarkGray.copy(alpha = sharedViewModel.hudMenuOpacity),
                uncheckedBorderColor = sharedViewModel.hudForegroundColor
            ),
            modifier = Modifier.testTag("dndSwitch")
        )
    }
}
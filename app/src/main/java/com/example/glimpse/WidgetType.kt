package com.example.glimpse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.glimpse.barcode.BarcodeWidget
import com.example.glimpse.chat.ChatWidget
import com.example.glimpse.compass.CompassWidget
import com.example.glimpse.face.FaceWidget
import com.example.glimpse.icons.BarcodeScanner
import com.example.glimpse.icons.ChatBubble
import com.example.glimpse.icons.Directions
import com.example.glimpse.icons.Explore
import com.example.glimpse.navigation.NavigationWidget
import com.example.glimpse.settings.SettingsWidget
import com.example.glimpse.weather.WeatherWidget

sealed class WidgetType {
    abstract val icon: ImageVector
    open fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit = { }

    data object Barcode : WidgetType() {
        override val icon: ImageVector = BarcodeScanner
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit =
            { BarcodeWidget(sharedViewModel = sharedViewModel) }
    }

    data object Chat : WidgetType() {
        override val icon: ImageVector = ChatBubble
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit =
            { ChatWidget(sharedViewModel = sharedViewModel) }
    }

    data object Compass : WidgetType() {
        override val icon: ImageVector = Explore
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit = {
            CompassWidget(sharedViewModel = sharedViewModel)
        }
    }

    data object Face : WidgetType() {
        override val icon: ImageVector = Icons.Default.Face
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit = {
            FaceWidget(sharedViewModel = sharedViewModel)
        }
    }

    data object Navigate : WidgetType() {
        override val icon: ImageVector = Directions
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit = {
            NavigationWidget(sharedViewModel = sharedViewModel)
        }
    }

    data object Weather : WidgetType() {
        override val icon: ImageVector = com.example.glimpse.icons.Weather
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit = {
            WeatherWidget(sharedViewModel = sharedViewModel)
        }
    }

    data object Settings : WidgetType() {
        override val icon: ImageVector = Icons.Default.Settings
        override fun composable(sharedViewModel: SharedViewModel): @Composable () -> Unit = {
            SettingsWidget(sharedViewModel = sharedViewModel)
        }
    }

    companion object {
        val allWidgetTypes = listOf(
            Barcode,
            Chat,
            Compass,
            Face,
            Navigate,
            Weather,
            Settings
        )
    }
}
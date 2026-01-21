package com.example.glimpse.weather

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.glimpse.SharedViewModel
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/*
FR10 - Weather.Widget
    Composable and scheduler functions for weather widget
 */

// scheduler for weather to be fetched at a desired interval
fun scheduleWeatherUpdate(context: Context) {
    val weatherWorkRequest = PeriodicWorkRequestBuilder<WeatherWorker>(15, TimeUnit.MINUTES)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "WeatherWorker", ExistingPeriodicWorkPolicy.KEEP, weatherWorkRequest
    )

    // Manually perform the first fetch upon function call
    WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<WeatherWorker>().build())
}

@Composable
fun WeatherWidget(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current

    val sharedPreferences =
        remember { context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE) }

    // weather details
    var temperature by remember { mutableStateOf(sharedPreferences.getString("temperature", null)) }
    var description by remember { mutableStateOf(sharedPreferences.getString("description", null)) }
    var name by remember { mutableStateOf(sharedPreferences.getString("name", null)) }
    var feelsLike by remember { mutableStateOf(sharedPreferences.getString("feels_like", null)) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "temperature" || key == "description" || key == "name") {
                temperature = sharedPreferences.getString("temperature", null)
                description = sharedPreferences.getString("description", null)
                name = sharedPreferences.getString("name", null)
                feelsLike = sharedPreferences.getString("feels_like", null)
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // display temperature on HUD
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                temperature == null || description == null || name == null -> {
                    Text(
                        text = "Weather unavailable. Please ensure location services are enabled and you are connected to a network.",
                        style = TextStyle(
                            color = Color.Green,
                            fontSize = 24.sp,
                            fontFamily = sharedViewModel.selectedFont.fontResource,
                        )
                    )
                }

                else -> {
                    Text(
                        text = "${temperature?.toDouble()?.roundToInt()}°C $name",
                        style = TextStyle(
                            color = sharedViewModel.hudForegroundColor,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sharedViewModel.selectedFont.fontResource,
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Conditions: $description | Feels like ${
                            feelsLike?.toDouble()?.roundToInt()
                        }°C",
                        style = TextStyle(
                            color = sharedViewModel.hudForegroundColor,
                            fontSize = 30.sp,
                            fontFamily = sharedViewModel.selectedFont.fontResource,
                        )
                    )
                }
            }
        }
    }
}
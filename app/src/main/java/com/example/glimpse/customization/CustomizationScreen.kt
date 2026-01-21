package com.example.glimpse.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.glimpse.HUDScreen
import com.example.glimpse.SharedViewModel

/*
FR2 - Change.Font
FR3 - Change.Colours
Colour and font customization features
 */

sealed class AppFont(val fontName: String, val fontResource: FontFamily) {
    // System fonts
    object Default : AppFont("System Default", FontFamily.Default)
    object Serif : AppFont("Serif", FontFamily.Serif)
    object SansSerif : AppFont("Sans-Serif", FontFamily.SansSerif)
    object Monospace : AppFont("Monospace", FontFamily.Monospace)

    companion object {
        // Make sure the list is never null and contains all font objects
        val allFonts: List<AppFont> by lazy {
            listOf(
                Default,
                SansSerif,
                Serif,
                Monospace,
            ).also {
                require(it.all { font -> font.fontName.isNotEmpty() }) {
                    "All fonts must have a name"
                }
            }
        }
    }
}

@Composable
fun CustomizationScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val originalBackgroundColor = sharedViewModel.hudBackgroundColor.copy()
    val originalForegroundColor = sharedViewModel.hudForegroundColor.copy()
    val originalFontWeight = sharedViewModel.hudFontWeight
    val originalIconMode = sharedViewModel.iconMode
    val originalMenuOpacity = sharedViewModel.hudMenuOpacity
    val originalFont = sharedViewModel.selectedFont
    var submitted by remember { mutableStateOf(false) }

    // Background color sliders
    var backgroundRed by remember { mutableFloatStateOf(sharedViewModel.hudBackgroundColor.red) }
    var backgroundGreen by remember { mutableFloatStateOf(sharedViewModel.hudBackgroundColor.green) }
    var backgroundBlue by remember { mutableFloatStateOf(sharedViewModel.hudBackgroundColor.blue) }

    // Foreground color sliders
    var foregroundRed by remember { mutableFloatStateOf(sharedViewModel.hudForegroundColor.red) }
    var foregroundGreen by remember { mutableFloatStateOf(sharedViewModel.hudForegroundColor.green) }
    var foregroundBlue by remember { mutableFloatStateOf(sharedViewModel.hudForegroundColor.blue) }

    var fontWeight by remember { mutableStateOf(sharedViewModel.hudFontWeight) }
    var menuOpacity by remember { mutableStateOf(sharedViewModel.hudMenuOpacity) }
    var iconMode by remember { mutableStateOf(sharedViewModel.iconMode) }
    var selectedFont by remember { mutableStateOf(sharedViewModel.selectedFont) }

    LaunchedEffect(
        backgroundRed, backgroundGreen, backgroundBlue,
        foregroundRed, foregroundGreen, foregroundBlue,
        iconMode, fontWeight, menuOpacity, selectedFont
    ) {
        sharedViewModel.updateHudBackgroundColor(Color(backgroundRed, backgroundGreen, backgroundBlue))
        sharedViewModel.updateForegroundColor(Color(foregroundRed, foregroundGreen, foregroundBlue))
        sharedViewModel.updateIconMode(iconMode)
        sharedViewModel.updateHudFontWeight(fontWeight)
        sharedViewModel.updateMenuOpacity(menuOpacity)
        sharedViewModel.updateSelectedFont(selectedFont)
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!submitted) {
                sharedViewModel.updateHudBackgroundColor(originalBackgroundColor)
                sharedViewModel.updateForegroundColor(originalForegroundColor)
                sharedViewModel.updateIconMode(originalIconMode)
                sharedViewModel.updateHudFontWeight(originalFontWeight)
                sharedViewModel.updateMenuOpacity(originalMenuOpacity)
                sharedViewModel.updateSelectedFont(originalFont)
            }
        }
    }

    fun resetToDefaults() {
        backgroundRed = 0f
        backgroundGreen = 0f
        backgroundBlue = 0f
        foregroundRed = 0f
        foregroundGreen = 1f
        foregroundBlue = 0f
        fontWeight = FontWeight.Normal
        menuOpacity = 1f
        iconMode = true
        selectedFont = AppFont.Default
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            HUDScreen(
                navController = navController,
                isPreview = true,
                sharedViewModel = sharedViewModel,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "HUD Foreground Color",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Red: ${(foregroundRed * 255).toInt()}")
                Slider(
                    value = foregroundRed,
                    onValueChange = { foregroundRed = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.testTag("ForegroundRed")
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Green: ${(foregroundGreen * 255).toInt()}")
                Slider(
                    value = foregroundGreen,
                    onValueChange = { foregroundGreen = it },
                    valueRange = 0f..1f
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Blue: ${(foregroundBlue * 255).toInt()}")
                Slider(
                    value = foregroundBlue,
                    onValueChange = { foregroundBlue = it },
                    valueRange = 0f..1f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "HUD Background Color",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Red: ${(backgroundRed * 255).toInt()}")
                Slider(
                    value = backgroundRed,
                    onValueChange = { backgroundRed = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.testTag("BackgroundRed")
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Green: ${(backgroundGreen * 255).toInt()}")
                Slider(
                    value = backgroundGreen,
                    onValueChange = { backgroundGreen = it },
                    valueRange = 0f..1f
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Blue: ${(backgroundBlue * 255).toInt()}")
                Slider(
                    value = backgroundBlue,
                    onValueChange = { backgroundBlue = it },
                    valueRange = 0f..1f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Font Selection",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )

        var fontExpanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { fontExpanded = true }) {
                Text(
                    text = selectedFont.fontName,
                    fontFamily = selectedFont.fontResource,
                )
            }
            DropdownMenu(
                expanded = fontExpanded,
                onDismissRequest = { fontExpanded = false },
            ) {
                AppFont.allFonts.forEach { font ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = font.fontName,
                                fontFamily = font.fontResource,
                            )
                        },
                        onClick = {
                            selectedFont = font
                            fontExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { resetToDefaults() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Reset to Defaults")
        }

        Button(
            onClick = {
                submitted = true
                sharedViewModel.savePreferences()
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Save")
        }
    }
}
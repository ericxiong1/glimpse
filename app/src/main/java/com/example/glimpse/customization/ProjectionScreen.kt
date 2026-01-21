package com.example.glimpse.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.glimpse.HUDScreen
import com.example.glimpse.SharedViewModel

/*
FR1 - Manipulate.HUD
HUD scale, sizing, orientation etc customization features
 */

@Composable
fun ProjectionScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val originalScaleX = sharedViewModel.scaleX
    val originalScaleY = sharedViewModel.scaleY
    val originalMirrorX = sharedViewModel.mirrorX
    val originalMirrorY = sharedViewModel.mirrorY
    val originalOffsetX = sharedViewModel.offsetX
    val originalOffsetY = sharedViewModel.offsetY
    val originalRotationX = sharedViewModel.rotationX
    val originalRotationY = sharedViewModel.rotationY
    val originalRotationZ = sharedViewModel.rotationZ
    val originalRotateCamera = sharedViewModel.rotateCamera

    var scaleX by remember { mutableStateOf(sharedViewModel.scaleX) }
    var scaleY by remember { mutableStateOf(sharedViewModel.scaleY) }
    var mirrorX by remember { mutableStateOf(sharedViewModel.mirrorX) }
    var mirrorY by remember { mutableStateOf(sharedViewModel.mirrorY) }
    var offsetX by remember { mutableStateOf(sharedViewModel.offsetX) }
    var offsetY by remember { mutableStateOf(sharedViewModel.offsetY) }
    var rotationX by remember { mutableStateOf(sharedViewModel.rotationX) }
    var rotationY by remember { mutableStateOf(sharedViewModel.rotationY) }
    var rotationZ by remember { mutableStateOf(sharedViewModel.rotationZ) }
    var rotateCamera by remember { mutableStateOf(sharedViewModel.rotateCamera) }
    var submitted by remember { mutableStateOf(false) }

    LaunchedEffect(scaleX, scaleY, mirrorX, mirrorY, offsetX, offsetY, rotationX, rotationY, rotationZ, rotateCamera) {
        sharedViewModel.updateScaleX(scaleX)
        sharedViewModel.updateScaleY(scaleY)
        sharedViewModel.updateMirrorX(mirrorX)
        sharedViewModel.updateMirrorY(mirrorY)
        sharedViewModel.updateOffsetX(offsetX)
        sharedViewModel.updateOffsetY(offsetY)
        sharedViewModel.updateRotationX(rotationX)
        sharedViewModel.updateRotationY(rotationY)
        sharedViewModel.updateRotationZ(rotationZ)
        sharedViewModel.updateRotateCamera(rotateCamera)
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!submitted) {
                sharedViewModel.updateScaleX(originalScaleX)
                sharedViewModel.updateScaleY(originalScaleY)
                sharedViewModel.updateMirrorX(originalMirrorX)
                sharedViewModel.updateMirrorY(originalMirrorY)
                sharedViewModel.updateOffsetX(originalOffsetX)
                sharedViewModel.updateOffsetY(originalOffsetY)
                sharedViewModel.updateRotationX(originalRotationX)
                sharedViewModel.updateRotationY(originalRotationY)
                sharedViewModel.updateRotationZ(originalRotationZ)
                sharedViewModel.updateRotateCamera(originalRotateCamera)
            }
        }
    }

    fun resetToDefaults() {
        scaleX = 1f
        scaleY = 1f
        mirrorX = false
        mirrorY = false
        offsetX = 0f
        offsetY = 0f
        rotationX = 0f
        rotationY = 0f
        rotationZ = 0f
        rotateCamera = false
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                sharedViewModel = sharedViewModel
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scale Section
        Text(text = "Scale", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "X: ${"%.2f".format(scaleX)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = scaleX,
                    onValueChange = { scaleX = it },
                    valueRange = 0.5f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Y: ${"%.2f".format(scaleY)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = scaleY,
                    onValueChange = { scaleY = it },
                    valueRange = 0.5f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Mirror Section
        Text(text = "Mirror", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "X", fontWeight = FontWeight.Bold)
                Switch(
                    checked = mirrorX,
                    onCheckedChange = { mirrorX = it },
                    modifier = Modifier.testTag("MirrorXSwitch")
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Y", fontWeight = FontWeight.Bold)
                Switch(
                    checked = mirrorY,
                    onCheckedChange = { mirrorY = it },
                    modifier = Modifier.testTag("MirrorYSwitch")
                )
            }
        }

        // Offset Section
        Text(text = "Offset", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "X: ${"%.2f".format(offsetX)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = offsetX,
                    onValueChange = { offsetX = it },
                    valueRange = -200f..200f,
                    modifier = Modifier.fillMaxWidth().testTag("OffsetXSlider")
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Y: ${"%.2f".format(offsetY)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = offsetY,
                    onValueChange = { offsetY = it },
                    valueRange = -200f..200f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Rotation Section
        Text(text = "Rotation", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "X: ${"%.2f".format(rotationX)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = rotationX,
                    onValueChange = { rotationX = it },
                    valueRange = -180f..180f,
                    modifier = Modifier.fillMaxWidth().testTag("RotationXSlider")
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Y: ${"%.2f".format(rotationY)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = rotationY,
                    onValueChange = { rotationY = it },
                    valueRange = -180f..180f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Z: ${"%.2f".format(rotationZ)}", fontWeight = FontWeight.Bold)
                Slider(
                    value = rotationZ,
                    onValueChange = { rotationZ = it },
                    valueRange = -180f..180f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Flip Camera Feed", fontWeight = FontWeight.Bold)
        Switch(
            checked = rotateCamera,
            onCheckedChange = { rotateCamera = it },
            modifier = Modifier.padding(top = 8.dp).testTag("FlipCameraSwitch")
        )

        Button(
            onClick = {
                resetToDefaults()
            },
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
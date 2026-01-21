package com.example.glimpse.face

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glimpse.SharedViewModel
import com.example.glimpse.camera.CameraAnalyzer

/*
FR21 - Recognition.Widget
FR22 - Recognition.View
UI components for facial recognition widget
 */


@Composable
fun FaceWidget(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    var recognizedName by remember { mutableStateOf("") }
    var recognizedInfo by remember { mutableStateOf("") }

    val analyzer = remember {
        FaceCameraFeedAnalyzer(context, sharedViewModel) { result ->
            if (result.detected) {
                if (result.person == null) {
                    recognizedName = "Do not know"
                    recognizedInfo = ""
                } else {
                    recognizedName = result.person.name
                    recognizedInfo = result.person.information
                }
            } else {
                recognizedName = ""
                recognizedInfo = ""
            }
        }
    }

    CameraAnalyzer(analyzer = analyzer)

    // Display the recognized face name and information
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(0.dp, 0.dp, 0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = recognizedName,
                style = TextStyle(
                    color = sharedViewModel.hudForegroundColor,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sharedViewModel.selectedFont.fontResource,
                )
            )
            if (recognizedInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recognizedInfo,
                    style = TextStyle(
                        color = sharedViewModel.hudForegroundColor,
                        fontSize = 30.sp,
                        fontFamily = sharedViewModel.selectedFont.fontResource,
                    ),
                )
            }
        }
    }
}
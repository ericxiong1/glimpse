package com.example.glimpse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.glimpse.customization.HUDEditorScreen
import com.example.glimpse.icons.Monitor

@Composable
fun EditScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionCard("Projection Options", Monitor) {
            navController.navigate(Screen.Projection.rout)
        }

        ActionCard("Customization", Icons.Default.Edit) {
            navController.navigate(Screen.Customization.rout)
        }

        ActionCard("HUD Editor", Icons.Default.Build) {
            navController.navigate(Screen.HUDEditor.rout)
        }
    }
}
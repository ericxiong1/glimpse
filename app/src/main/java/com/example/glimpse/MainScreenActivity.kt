package com.example.glimpse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.glimpse.ui.theme.GlimpseTheme
import FaceUploadScreen
import ManagerScreen
import PersonListScreen
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.glimpse.customization.CustomizationScreen
import com.example.glimpse.customization.HUDEditorScreen
import com.example.glimpse.customization.ProjectionScreen
import com.example.glimpse.navigation.DestinationUploadScreen
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

class MainScreenActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()
    var mapboxNavigation: MapboxNavigation? = null

    override fun onDestroy() {
        super.onDestroy()
        println("📌 Detaching MapboxNavigationApp from lifecycle...")
        MapboxNavigationApp.detach(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlimpseTheme {
                MainScreen(sharedViewModel)
            }
        }

        if (!MapboxNavigationApp.isSetup()) {
            println("🚀 Setting up Mapbox Navigation...")
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this).build()
            }
        }

        println("📌 Attaching MapboxNavigationApp to lifecycle...")
        MapboxNavigationApp.attach(this)
        mapboxNavigation = MapboxNavigationApp.current()
    }
}

fun requestNotificationPermission(context: Context) {
    // Needed to listen to notifications
    if (!NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName))
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
}

fun requestWriteSettingsPermission(context: Context) {
    // Write settings needed to modify brightness
    if (!Settings.System.canWrite(context)) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBar = currentRoute != Screen.HUD.rout
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        requestWriteSettingsPermission(context)
        requestNotificationPermission(context)
    }

    Scaffold(
        topBar = {
            if (showBar)
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.glimpse_icon),
                                contentDescription = "Glimpse Icon",
                                modifier = Modifier
                                    .size(56.dp)
                                    .padding(end = 8.dp),
                            )
                            Text(
                                text = "Glimpse",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = { if (showBar) BottomNavigationBar(navController) },
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)}
    ) { innerPadding ->
        val graph =
            navController.createGraph(startDestination = Screen.Home.rout) {
                composable(Screen.Home.rout) { HomeScreen(navController) }
                composable(Screen.Edit.rout) { EditScreen(navController) }
                composable(Screen.HUD.rout) { HUDScreen(navController, false, sharedViewModel) }
                composable(Screen.FaceUpload.rout) { FaceUploadScreen(navController, snackbarHostState) }
                composable(Screen.Customization.rout) { CustomizationScreen(navController, sharedViewModel) }
                composable(Screen.Projection.rout) { ProjectionScreen(navController, sharedViewModel) }
                composable(Screen.DestinationInput.rout) { DestinationUploadScreen(navController, sharedViewModel) }
                composable(Screen.PersonList.rout) { PersonListScreen() }
                composable(Screen.FaceManager.rout) { ManagerScreen(navController) }
                composable(Screen.HUDEditor.rout) { HUDEditorScreen(navController, sharedViewModel) }
            }
        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
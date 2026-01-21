package com.example.glimpse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/*
Creates the bottom navigation bar for the Glimpse UI
This navigation bar is implemented, using the following tutorials:
https://medium.com/@rzmeneghelo/creating-a-bottom-navigation-bar-with-jetpack-compose-a-comprehensive-guide-a5451aefc0ab
https://medium.com/@santosh_yadav321/bottom-navigation-bar-in-jetpack-compose-5b3c5f2cea9b
 */
data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

sealed class Screen(val rout: String) {
    object Home: Screen("home_screen")
    object Edit: Screen("edit_screen")
    object HUD: Screen("hud_screen")
    object FaceUpload: Screen("face_upload_screen")
    object FaceManager: Screen("face_management_screen")
    object PersonList: Screen("person_list_screen")
    object DestinationInput: Screen("destination_input_screen")
    object Customization: Screen("customization_screen")
    object Projection: Screen("projection_screen")
    object HUDEditor: Screen("hud_editor")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem(title = "Home", icon = Icons.Default.Home, route = Screen.Home.rout),
        NavigationItem(title = "Edit", icon = Icons.Default.Edit, route = Screen.Edit.rout)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedIndex = remember(currentRoute) {
        navigationItems.indexOfFirst { it.route == currentRoute }
    }

    Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        NavigationBar {
            navigationItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(imageVector = item.icon, contentDescription = item.title)
                    },
                )
            }
        }
    }
}
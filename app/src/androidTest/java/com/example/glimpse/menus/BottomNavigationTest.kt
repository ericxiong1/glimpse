package com.example.glimpse.menus

import android.Manifest
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import com.example.glimpse.BottomNavigationBar
import com.example.glimpse.EditScreen
import com.example.glimpse.HomeScreen
import com.example.glimpse.Screen
import org.junit.Rule
import org.junit.Test

class BottomNavigationTest {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Test
    fun testHomeToEdit() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Home.rout) {
                composable(Screen.Home.rout) { HomeScreen(navController) }
                composable(Screen.Edit.rout) { EditScreen(navController) }
            }
            BottomNavigationBar(navController)
        }

        composeTestRule.onNodeWithContentDescription("Edit").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "edit_screen")
    }

    @Test
    fun testEditToHome() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Edit.rout) {
                composable(Screen.Home.rout) { HomeScreen(navController) }
                composable(Screen.Edit.rout) { EditScreen(navController) }
            }
            BottomNavigationBar(navController)
        }


        composeTestRule.onNodeWithContentDescription("Home").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "home_screen")
    }
}
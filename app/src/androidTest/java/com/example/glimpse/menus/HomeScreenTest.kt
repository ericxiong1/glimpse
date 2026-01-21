package com.example.glimpse.menus

import ManagerScreen
import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import com.example.glimpse.HUDScreen
import com.example.glimpse.HomeScreen
import com.example.glimpse.Screen
import com.example.glimpse.SharedViewModel
import com.example.glimpse.navigation.DestinationUploadScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
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
    private val sharedViewModel = mockk<SharedViewModel>(relaxed = true)

    @Test
    fun testHUD() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Home.rout) {
                composable(Screen.Home.rout) { HomeScreen(navController) }
                composable(Screen.HUD.rout) { HUDScreen(navController, false, sharedViewModel) }
            }
        }

        composeTestRule.onNodeWithText("HUD").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "hud_screen")
    }

    @Test
    fun testFacialRecognition() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Home.rout) {
                composable(Screen.Home.rout) { HomeScreen(navController) }
                composable(Screen.FaceManager.rout) { ManagerScreen(navController) }
            }
        }

        composeTestRule.onNodeWithText("Manage Facial Recognition").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "face_management_screen")
    }

    @Test
    fun testDestinationInput() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Home.rout) {
                composable(Screen.Home.rout) { HomeScreen(navController) }
                composable(Screen.DestinationInput.rout) { DestinationUploadScreen(navController, sharedViewModel) }
            }
        }

        composeTestRule.onNodeWithText("Destination Input").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "destination_input_screen")
    }
}

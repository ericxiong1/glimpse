package com.example.glimpse.menus

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import com.example.glimpse.EditScreen
import com.example.glimpse.Screen
import com.example.glimpse.SharedViewModel
import com.example.glimpse.customization.CustomizationScreen
import com.example.glimpse.customization.HUDEditorScreen
import com.example.glimpse.customization.ProjectionScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class EditScreenTest {
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
    fun testProjection() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Edit.rout) {
                composable(Screen.Edit.rout) { EditScreen(navController) }
                composable(Screen.Projection.rout) { ProjectionScreen(navController, sharedViewModel) }
            }
        }

        composeTestRule.onNodeWithText("Projection Options").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "projection_screen")
    }

    @Test
    fun testCustomization() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Edit.rout) {
                composable(Screen.Edit.rout) { EditScreen(navController) }
                composable(Screen.Customization.rout) { CustomizationScreen(navController, sharedViewModel) }
            }
        }

        composeTestRule.onNodeWithText("Customization").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "customization_screen")
    }

    @Test
    fun testHUDEditor() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Edit.rout) {
                composable(Screen.Edit.rout) { EditScreen(navController) }
                composable(Screen.HUDEditor.rout) { HUDEditorScreen(navController, sharedViewModel) }
            }
        }

        composeTestRule.onNodeWithText("HUD Editor").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "hud_editor")
    }
}

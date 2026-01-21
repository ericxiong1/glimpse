package com.example.glimpse.menus

import FaceUploadScreen
import ManagerScreen
import PersonListScreen
import android.Manifest
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import com.example.glimpse.Screen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class FacialRecognitionScreenTest {
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
    private val snackbarHostState = mockk<SnackbarHostState>(relaxed = true)

    @Test
    fun testUpload() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.FaceManager.rout) {
                composable(Screen.FaceManager.rout) { ManagerScreen(navController) }
                composable(Screen.FaceUpload.rout) { FaceUploadScreen(navController, snackbarHostState) }
            }
        }

        composeTestRule.onNodeWithText("Upload New Face").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "face_upload_screen")
    }

    @Test
    fun testPersonList() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.FaceManager.rout) {
                composable(Screen.FaceManager.rout) { ManagerScreen(navController) }
                composable(Screen.PersonList.rout) { PersonListScreen() }
            }
        }

        composeTestRule.onNodeWithText("View Existing Users").performClick()
        assert(navController.currentBackStackEntry?.destination?.route == "person_list_screen")
    }
}

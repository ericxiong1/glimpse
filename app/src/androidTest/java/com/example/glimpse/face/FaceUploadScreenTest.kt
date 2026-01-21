package com.example.glimpse.face

import FaceUploadScreen
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavController
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import io.objectbox.kotlin.boxFor
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaceUploadScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: NavController
    private lateinit var testUri: Uri
    private lateinit var snackbarHostState: SnackbarHostState

    @Before
    fun setup() {
        ObjectBox.store.boxFor(Person::class).removeAll()
        ObjectBox.store.boxFor(Face::class).removeAll()

        Intents.init()
        testUri = Uri.parse("android.resource://com.example.glimpse/drawable/face")
        navController = mockk<NavController>(relaxed = true)
        snackbarHostState = mockk<SnackbarHostState>(relaxed = true)
        composeTestRule.setContent {
            val registryOwner = object: ActivityResultRegistryOwner {
                override val activityResultRegistry = object : ActivityResultRegistry() {
                    override fun <I : Any?, O : Any?> onLaunch(
                        requestCode: Int,
                        contract: ActivityResultContract<I, O>,
                        input: I,
                        options: ActivityOptionsCompat?
                    ) {
                        val intent = Intent().setData(testUri)
                        this.dispatchResult(requestCode, Activity.RESULT_OK, intent)
                    }
                }
            }

            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                FaceUploadScreen(navController, snackbarHostState)
            }
        }
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testNameAndInfoInputFields() {
        val testName = "John Doe"
        val testInfo = "Test User Info"

        composeTestRule.onNodeWithText("Name").performTextInput(testName)
        composeTestRule.onNodeWithText("Additional Information").performTextInput(testInfo)

        composeTestRule.onNodeWithText(testName).assertIsDisplayed()
        composeTestRule.onNodeWithText(testInfo).assertIsDisplayed()
    }

    @Test
    fun testGalleryButtonLaunch() {
        composeTestRule.onNodeWithText("Choose from Gallery").performClick()

        composeTestRule.onNodeWithTag("imageTestTag")
            .assertIsDisplayed()
    }

    @Test
    fun testSubmitButtonEnabledOnlyWhenFieldsAreFilled() {
        val testName = "John Doe"

        composeTestRule.onNodeWithText("Submit").assertIsNotEnabled()

        composeTestRule.onNodeWithText("Name").performTextInput(testName)
        composeTestRule.onNodeWithText("Choose from Gallery").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Submit").assertIsEnabled()
    }

    @Test
    fun testSubmitButtonFunctionality() {
        val testName = "John Doe"
        val testInfo = "Sample info"
        testUri = Uri.parse("android.resource://com.example.glimpse/drawable/face")

        composeTestRule.onNodeWithText("Name").performTextInput(testName)
        composeTestRule.onNodeWithText("Additional Information").performTextInput(testInfo)
        composeTestRule.onNodeWithText("Choose from Gallery").performClick()
        composeTestRule.onNodeWithText("Submit").performClick()

        // Wait for processing
        Thread.sleep(1000)
        verify { navController.popBackStack() }

        // Check db
        val savedPerson = ObjectBox.store.boxFor(Person::class).query(Person_.name.equal(testName)).build().findUnique()
        assertNotNull("Person was not saved in the database", savedPerson)
        assertTrue( "Person should have at least one associated Face", savedPerson?.faces?.isNotEmpty()!!)
    }

    @Test
    fun testSubmitNoFace() {
        val testName = "John Doe"
        testUri = Uri.parse("android.resource://com.example.glimpse/drawable/noface")

        composeTestRule.onNodeWithText("Name").performTextInput(testName)
        composeTestRule.onNodeWithText("Choose from Gallery").performClick()
        composeTestRule.onNodeWithText("Submit").performClick()

        Thread.sleep(1000)
        coVerify { snackbarHostState.showSnackbar("No faces detected") }
        verify(exactly = 0) { navController.popBackStack() }
    }

    @Test
    fun testSubmitTwoFace()  {
        val testName = "John Doe"
        testUri = Uri.parse("android.resource://com.example.glimpse/drawable/twoface")

        composeTestRule.onNodeWithText("Name").performTextInput(testName)
        composeTestRule.onNodeWithText("Choose from Gallery").performClick()
        composeTestRule.onNodeWithText("Submit").performClick()

        Thread.sleep(1000)
        coVerify { snackbarHostState.showSnackbar("Multiple faces detected") }
        verify(exactly = 0) { navController.popBackStack() }
    }
}

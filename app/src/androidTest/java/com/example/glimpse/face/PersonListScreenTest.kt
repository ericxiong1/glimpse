package com.example.glimpse.face

import ManagerScreen
import PersonListScreen
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import io.mockk.verify
import io.objectbox.kotlin.boxFor
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class PersonListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var samplePeople: List<Person>

    @Before
    fun setup() {
        ObjectBox.store.boxFor(Person::class).removeAll()

        samplePeople = listOf(
            Person(name="John Doe", information = "information0"),
            Person(name="Jane Smith", information = "information1"),
            Person(name="Alex Brown", information = "information2")
        )
        ObjectBox.store.boxFor(Person::class).put(samplePeople)
        composeTestRule.setContent {
            PersonListScreen()
        }
    }

    @Test
    fun testViewUsers() {
        for (person in samplePeople) {
            composeTestRule.onAllNodesWithText(person.name).assertCountEquals(1)
        }
    }

    @Test
    fun testDeleteUser() {
        val personToDelete = samplePeople[0]

        composeTestRule.onNodeWithContentDescription("Delete"+personToDelete.name)
            .performClick()

        composeTestRule.onAllNodesWithText(personToDelete.name).assertCountEquals(0)
        val deletedPerson = ObjectBox.store.boxFor(Person::class.java).query(Person_.name.equal(personToDelete.name)).build().find()
        assertTrue(deletedPerson.isEmpty())
    }

}

@RunWith(AndroidJUnit4::class)
class ManagerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: NavController

    @Before
    fun setup() {
        navController = mockk<NavController>(relaxed = true)
        composeTestRule.setContent {
            ManagerScreen(navController)
        }
    }

    @Test
    fun testManagerScreenNavUpload() {
        composeTestRule.onNodeWithText("Upload New Face").performClick()
        verify { navController.navigate("face_upload_screen") }
    }

    @Test
    fun testManagerScreenNavList() {
        composeTestRule.onNodeWithText("View Existing Users").performClick()
        verify { navController.navigate("person_list_screen") }
    }
}

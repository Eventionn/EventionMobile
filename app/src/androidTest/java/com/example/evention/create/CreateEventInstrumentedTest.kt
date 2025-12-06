package com.example.evention.create

import UserPreferences
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.evention.di.NetworkModule
import com.example.evention.ui.screens.auth.login.LoginScreen
import com.example.evention.ui.theme.EventionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.evention.ui.screens.event.create.CreateEventScreen
import com.example.evention.ui.screens.event.create.SelectLocationScreen
import com.example.evention.ui.screens.home.HomeScreen
import com.example.evention.ui.screens.profile.userEvents.UserEvents

@RunWith(AndroidJUnit4::class)
class CreateScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    private fun setNavHost(startDestination: String = "signIn") {
        composeRule.setContent {
            EventionTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDestination) {
                    composable("signIn") { LoginScreen(navController) }
                    composable("home") { HomeScreen(emptyList(), navController) }
                    composable("create") { CreateEventScreen(navController) }
                    composable("selectLocation") { SelectLocationScreen(navController) }
                    composable("userEvents") { UserEvents(emptyList(), navController) }
                }
            }
        }
    }

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        val userPrefs = UserPreferences(context)
        userPrefs.saveToken("fake-token")
        NetworkModule.init(userPrefs)
    }

    @Test
    fun testCreateEvent_NoData_ShowsErrorMessage() {
        setNavHost()

        composeRule.onNodeWithTag("emailField").performTextInput("utilizador2@gmail.com")
        composeRule.onNodeWithTag("passwordField").performTextInput("123456789")
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("upcomingField").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("upcomingField").assertIsDisplayed()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("menu_Create").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("menu_Create").performClick()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithTag("createEventButton").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("createEventButton").performClick()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithText("Incorrect fields").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Incorrect fields").assertIsDisplayed()
    }

    @Test
    fun testCreateEvent() {
        setNavHost()

        composeRule.onNodeWithTag("emailField").performTextInput("utilizador2@gmail.com")
        composeRule.onNodeWithTag("passwordField").performTextInput("123456789")
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(20000) {
            composeRule.onAllNodesWithTag("upcomingField").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("menu_Create").performClick()

        composeRule.waitUntil(20000) {
            composeRule.onAllNodesWithText("Event Name").fetchSemanticsNodes().isNotEmpty()
        }

        val eventName = "Night Party 2025"
        val eventDescription = "Night Party in Barcelo"
        val eventPrice = "100"

        composeRule.onNodeWithText("Event Name").performTextInput(eventName)
        composeRule.onNodeWithText("Description").performTextInput(eventDescription)
        composeRule.onNodeWithText("Price").performTextInput(eventPrice)
        composeRule.onNodeWithTag("createEventButton").performClick()

        composeRule.waitUntil(timeoutMillis = 20000) {
            composeRule.onAllNodesWithText("Event created successfully!").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Event created successfully!").assertIsDisplayed()
    }
}
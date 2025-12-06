package com.example.evention.approveEvent

import UserPreferences
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.evention.di.NetworkModule
import com.example.evention.ui.screens.auth.login.LoginScreen
import com.example.evention.ui.screens.home.HomeScreen
import com.example.evention.ui.screens.home.HomeScreenViewModel
import com.example.evention.ui.screens.profile.admin.AdminMenu
import com.example.evention.ui.screens.profile.admin.events.EventsToApprove
import com.example.evention.ui.screens.profile.admin.events.EventsToApproveViewModel
import com.example.evention.ui.screens.profile.user.userProfile.UserProfile
import com.example.evention.ui.theme.EventionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApproveEventAdminTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val userPrefs = UserPreferences(context)
        userPrefs.saveToken("fake-token")
        NetworkModule.init(userPrefs)
    }

    private fun setNavHost(startDestination: String = "signIn") {
        composeRule.setContent {
            EventionTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = startDestination) {

                    composable("signIn") { LoginScreen(navController) }

                    composable("home") {
                        val viewModel: HomeScreenViewModel = viewModel()
                        val events by viewModel.events.collectAsState()

                        HomeScreen(events = events, navController = navController)
                    }

                    composable("profile") {
                        UserProfile(navController)
                    }

                    composable("adminMenu") {
                        AdminMenu(navController)
                    }

                    composable("approveEvents") {
                        val viewModel: EventsToApproveViewModel = viewModel()
                        val events by viewModel.events.collectAsState()

                        EventsToApprove(events = events, navController = navController)
                    }
                }
            }
        }
    }

    /**
     * TC6.1 - Testar aprovação de evento
     * Pré requisito: Fazer login como administrador e ter criado evento
     */
    @Test
    fun testEventApproval() {
        setNavHost()

        val email = "utilizador2@gmail.com"
        val password = "123456789"

        // Login
        composeRule.onNodeWithTag("emailField").performTextInput(email)
        composeRule.onNodeWithTag("passwordField").performTextInput(password)
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("upcomingField").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("upcomingField").assertIsDisplayed()

        // Espera pela UI
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Profile").assertIsDisplayed().performClick()

        composeRule.onNodeWithText("Admin Menu").assertIsDisplayed().performClick()

        composeRule.onNodeWithText("Events to approve").assertIsDisplayed().performClick()

        composeRule.onNodeWithText("Night Party 2025").assertIsDisplayed()

        composeRule.onAllNodesWithContentDescription("Mais opções")[0]
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithText("Approve event")
            .assertIsDisplayed()
            .performClick()

        // Aguarda o banner
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("approveBanner").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("approveBanner").assertIsDisplayed()

        // Aguarda o banner desaparecer
        composeRule.waitUntil(timeoutMillis = 7000) {
            composeRule.onAllNodesWithTag("approveBanner").fetchSemanticsNodes().isEmpty()
        }

        composeRule.onNodeWithTag("backButton").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("backButton").assertIsDisplayed().performClick()

        // Volta para Home
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Home").assertIsDisplayed().performClick()

        composeRule.onNodeWithText("Night Party 2025").assertIsDisplayed()
    }


}
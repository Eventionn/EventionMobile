package com.example.evention.login

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
import com.example.evention.ui.screens.home.HomeScreen

@RunWith(AndroidJUnit4::class)
class LoginScreenTests {

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
    fun testLoginWithInvalidCredentials_showsError() {
        setNavHost()

        val email = "utilizador1@gmail.com"
        val password = "utilizador123"

        composeRule.onNodeWithTag("emailField").performTextInput(email)
        composeRule.onNodeWithTag("passwordField").performTextInput(password)
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithText("Incorrect Email or Password!").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Incorrect Email or Password!").assertIsDisplayed()
    }

    @Test
    fun testLoginWithoutInput_showsRequiredFieldsError() {
        setNavHost()

        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("Campos obrigatórios").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Campos obrigatórios").assertIsDisplayed()
    }

    @Test
    fun testLoginWithShortPassword_showsPasswordTooShortError() {
        setNavHost()

        val email = "utilizador1@gmail.com"
        val password = "utiliza"

        composeRule.onNodeWithTag("emailField").performTextInput(email)
        composeRule.onNodeWithTag("passwordField").performTextInput(password)
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("A password deve ter pelo menos 8 caracteres")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("A password deve ter pelo menos 8 caracteres").assertIsDisplayed()
    }

     @Test
     fun testLoginWith8Chars() {
         setNavHost()

         val email = "utilizador1@gmail.com"
         val password = "12345678"

         composeRule.onNodeWithTag("emailField").performTextInput(email)
         composeRule.onNodeWithTag("passwordField").performTextInput(password)
         composeRule.onNodeWithTag("loginButton").performClick()

         composeRule.waitUntil(timeoutMillis = 5000) {
             composeRule.onAllNodesWithTag("upcomingField").fetchSemanticsNodes().isNotEmpty()
         }

         composeRule.onNodeWithTag("upcomingField").assertIsDisplayed()
     }

    @Test
    fun testLoginWith9Chars() {
        setNavHost()

        val email = "utilizador2@gmail.com"
        val password = "123456789"

        composeRule.onNodeWithTag("emailField").performTextInput(email)
        composeRule.onNodeWithTag("passwordField").performTextInput(password)
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("upcomingField").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("upcomingField").assertIsDisplayed()
    }
}
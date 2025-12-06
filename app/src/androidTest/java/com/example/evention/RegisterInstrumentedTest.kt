package com.example.evention

import UserPreferences
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.evention.di.NetworkModule
import com.example.evention.ui.screens.auth.login.LoginScreen
import com.example.evention.ui.screens.auth.register.RegisterScreen
import com.example.evention.ui.theme.EventionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTests {

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
                    composable("signUp") { RegisterScreen(navController) }
                    composable("signIn") { LoginScreen(navController) }
                }
            }
        }
    }

    @Test
    fun testInvalidEmailShowsErrorMessage() {
        setNavHost()

        composeRule.onNodeWithText("Sign up")
            .performClick()

        composeRule.onNodeWithText("Full Name")
            .performTextInput("utilizador1")

        composeRule.onNodeWithText("abc@email.com")
            .performTextInput("utilizador1gmail.com")

        composeRule.onNodeWithText("Your Password")
            .performTextInput("12345678")

        composeRule.onNodeWithText("Confirm Password")
            .performTextInput("12345678")

        composeRule.onNodeWithTag("register")
            .performClick()

        composeRule.onNodeWithTag("register_error_message")
            .assertIsDisplayed()
            .assertTextEquals("Email inválido")
    }

    @Test
    fun testInvalidPasswordMinimumCharShowsErrorMessage() {
        setNavHost("signUp")

        composeRule.onNodeWithText("Full Name")
            .performTextInput("utilizador1")

        composeRule.onNodeWithText("abc@email.com")
            .performTextInput("utilizador1@gmail.com")

        composeRule.onNodeWithText("Your Password")
            .performTextInput("utili")

        composeRule.onNodeWithText("Confirm Password")
            .performTextInput("utili")

        composeRule.onNodeWithTag("register")
            .performClick()

        composeRule.onNodeWithTag("register_error_message")
            .assertIsDisplayed()
            .assertTextEquals("A password deve ter pelo menos 8 caracteres")
    }

    @Test
    fun testFieldsEmptyShowsErrorMessage() {
        setNavHost("signUp")

        composeRule.onNodeWithTag("register")
            .performClick()

        composeRule.onNodeWithTag("register_error_message")
            .assertIsDisplayed()
            .assertTextEquals("All fields are required")
    }

    @Test
    fun testRegisterShowsSuccessMessage() {
        setNavHost()

        composeRule.onNodeWithText("Sign up")
            .performClick()

        composeRule.onNodeWithText("Full Name")
            .performTextInput("utilizador1")

        composeRule.onNodeWithText("abc@email.com")
            .performTextInput("utilizador1@gmail.com")

        composeRule.onNodeWithText("Your Password")
            .performTextInput("12345678")

        composeRule.onNodeWithText("Confirm Password")
            .performTextInput("12345678")

        composeRule.onNodeWithTag("register")
            .performClick()

        composeRule.waitUntil(3000) {
            val node = composeRule.onAllNodesWithTag("register_error_message")
                .fetchSemanticsNodes()
            node.isNotEmpty() && node.first().config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text == "User registed successfully!"
        }


    }



}

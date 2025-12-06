package com.example.evention

import UserPreferences
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.evention.di.NetworkModule
import com.example.evention.model.Event
import com.example.evention.ui.navigation.AppNavHost
import com.example.evention.ui.screens.home.HomeScreen
import com.example.evention.ui.screens.home.details.EventDetails
import com.example.evention.ui.screens.home.payment.PaymentScreen
import com.example.evention.ui.screens.ticket.TicketsScreen
import com.example.evention.ui.theme.EventionTheme
import com.google.gson.Gson
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class EventJoinPaidTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var userPrefs: UserPreferences

    val email = "utilizador1@gmail.com"
    val password = "12345678"
    private val eventName = "Night Party"
    private val paypalEmail = "admin@mail.com"

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        userPrefs = UserPreferences(context)
        userPrefs.saveToken("fake-jwt-token")
        NetworkModule.init(userPrefs)
    }

    private fun setNavHost(startDestination: String = "signIn") {
        composeRule.setContent {
            EventionTheme {
                AppNavHost(startDestination)
            }
        }
    }

    @Test
    fun test_AdesaoEventoPago() {
        // Passo 1: Aceder à página Home
        setNavHost("signIn")
        composeRule.onNodeWithTag("emailField").performTextInput(email)
        composeRule.onNodeWithTag("passwordField").performTextInput(password)
        composeRule.onNodeWithTag("loginButton").performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule
                .onAllNodesWithText("Upcoming Events")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithTag("searchField")
            .assertIsDisplayed()
            .performTextInput(eventName)

        composeRule.waitUntil(3000) {
            composeRule.onAllNodesWithText(eventName, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText(eventName, ignoreCase = true)
            .get(0)
            .performClick()


        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule
                .onAllNodesWithTag("event_card")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule
            .onAllNodesWithTag("event_card")
            .onFirst()
            .performClick()

        composeRule.onNodeWithTag("buy_ticket_button")
            .assertIsDisplayed()
            .performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("PayPal", ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

       composeRule.onNodeWithText("PayPal Email", ignoreCase = true)
            .assertIsDisplayed()
            .performTextInput(paypalEmail)

        composeRule.onNodeWithText(paypalEmail)
            .assertIsDisplayed()

        composeRule.onNodeWithText("PAY")
            .assertIsDisplayed()
            .performClick()


    }
}

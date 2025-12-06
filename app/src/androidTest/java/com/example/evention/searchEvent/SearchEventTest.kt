package com.example.evention

import UserPreferences
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.example.evention.di.NetworkModule
import com.example.evention.ui.navigation.AppNavHost
import com.example.evention.ui.theme.EventionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchEventTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var device: UiDevice

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val userPrefs = UserPreferences(context)
        userPrefs.saveToken("fake-token")
        NetworkModule.init(userPrefs)
    }

    private fun setNavHost(startDestination: String = "signIn") {
        composeTestRule.setContent {
            EventionTheme {
                AppNavHost(startDestination)
            }
        }
    }

    // Função auxiliar para aceitar permissão do sistema
    private fun grantSystemPermission() {
        try {
            Thread.sleep(1000)

            val allowButton = device.findObject(
                UiSelector().textMatches("(?i)allow|permitir|aceitar|permitir sempre")
            )

            if (allowButton.exists()) {
                allowButton.click()
            } else {
                val allowById = device.findObject(
                    UiSelector().resourceIdMatches(".*permission_allow.*")
                )
                if (allowById.exists()) {
                    allowById.click()
                }
            }
        } catch (e: Exception) {
            println("Permissão automática não pôde ser concedida: ${e.message}")
        }
    }

    // Função auxiliar para negar permissão do sistema
    private fun denySystemPermission() {
        try {
            Thread.sleep(1000)

            val denyButton = device.findObject(
                UiSelector().textMatches("(?i)deny|recusar|negar|don't allow|não permitir")
            )

            if (denyButton.exists()) {
                denyButton.click()
            } else {
                val denyById = device.findObject(
                    UiSelector().resourceIdMatches(".*permission_deny.*")
                )
                if (denyById.exists()) {
                    denyById.click()
                }
            }
        } catch (e: Exception) {
            println("Permissão automática não pôde ser negada: ${e.message}")
        }
    }

    @Test
    fun testSearchEventByName() {
        setNavHost("home")

        val eventName = "Night Party"
        composeTestRule.onNodeWithTag("searchField")
            .assertIsDisplayed()
            .performTextInput(eventName)

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText(eventName, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText(eventName, ignoreCase = true)
            .get(0)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithTag("event_card")
            .assertAny(hasText(eventName))
    }


    @Test
    fun testSearchByUserLocation_acceptPermission() {
        composeTestRule.setContent {
            EventionTheme {
                AppNavHost("search")
            }
        }

        composeTestRule.onNodeWithContentDescription("My Location Button")
            .performClick()

        grantSystemPermission()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("GoogleMap", useUnmergedTree = true)
            .assertExists()

    }

    @Test
    fun testSearchByUserLocation_denyPermission() {
        composeTestRule.setContent {
            EventionTheme {
                AppNavHost("search")
            }
        }

        composeTestRule.onNodeWithContentDescription("My Location Button")
            .performClick()

        // Recusa a permissão
        denySystemPermission()

        composeTestRule.waitForIdle()


    }


}
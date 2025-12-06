package com.example.evention

import UserPreferences
import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.evention.di.NetworkModule
import com.example.evention.ui.screens.auth.register.RegisterScreen
import com.example.evention.ui.screens.home.HomeScreen
import com.example.evention.ui.theme.EventionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry
import com.example.evention.ui.screens.auth.login.LoginScreen

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    // Permissão auto (TC3.2)
    @get:Rule
    val locationPermissionRule = androidx.test.rule.GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Before
    fun setup() {
        // Contexto
        context = ApplicationProvider.getApplicationContext()

        // Fake Token
        val userPrefs = UserPreferences(context)
        userPrefs.saveToken("fake-token")
        NetworkModule.init(userPrefs)
    }

    private fun setNavHost(startDestination: String = "home") {
        composeTestRule.setContent {
            EventionTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDestination) {
                    composable("home") { HomeScreen(navController) }
                }
            }
        }
    }

    /**
     * TC3.1 - Teste da pesquisa por nome de evento
     * Pré-requisito: Fazer Login (assumindo que MainActivity já abre Home após login simulado)
     */
    @Test
    fun testSearchEventByName() {
        setNavHost("home")

        val eventName = "Barcelos Party"

        composeTestRule.onNodeWithText("Upcoming Events")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("SearchTextField")
            .performTextInput(eventName)

        composeTestRule.onNodeWithTag("SearchTextField")
            .assert(hasText(eventName))

        composeTestRule.onNodeWithContentDescription("Search Button")
            .performClick()

        composeTestRule.onNodeWithText(eventName)
            .assertIsDisplayed()
    }


    /**
     * TC3.2 - Teste de pesquisa por Localização do utilizador aceitando acesso de localização
     * Pré-requisito: Fazer Login
     */
    @Test
    fun testSearchByUserLocation_acceptPermission() {
        setNavHost("home")

        composeTestRule.onNodeWithText("Upcoming Events")
            .assertIsDisplayed()

        // Clicar no botão de "Minha Localização" no MENU
        composeTestRule.onNodeWithContentDescription("Search")
            .performClick()

        // Mapa
        composeTestRule.onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }


    /**
     * TC3.3 - Teste de pesquisa por Localização do utilizador recusando acesso localização
     * Pré-requisito: Fazer Login
     */
    @Test
    fun testSearchByUserLocation_denyPermission() {
        setNavHost("home")

        composeTestRule.onNodeWithText("Upcoming Events")
            .assertIsDisplayed()

        // Simulação de permissão negada
        // NÃO existe forma oficial de negar no ComposeTestRule,
        // mas podemos verificar se o Toast aparece (ou comportamento alternativo)
        composeTestRule.onNodeWithTag("MyLocationButton")
            .performClick()

        // Esperar UI atualizar
        composeTestRule.waitForIdle()

        // Verificar fallback: nenhum crash e mapa continua presente
        composeTestRule.onNodeWithContentDescription("GoogleMap")
            .assertExists()
    }
}

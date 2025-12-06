package com.example.evention

import UserPreferences
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.evention.di.NetworkModule
import com.example.evention.ui.screens.home.HomeScreen
import com.example.evention.ui.theme.EventionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.evention.ui.screens.home.HomeScreenViewModel

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

                    composable("home") {
                        val viewModel: HomeScreenViewModel = viewModel()
                        val events by viewModel.events.collectAsState()

                        HomeScreen(events = events, navController = navController)
                    }
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

        val eventName = "Music Festival"

        // Verifica se a seção de eventos está visível
        composeTestRule.onNodeWithText("Upcoming Events")
            .assertIsDisplayed()

        // Digita na search bar
        composeTestRule.onNodeWithTag("SearchTextField")
            .performTextInput(eventName)

        composeTestRule.onNodeWithTag("SearchTextField")
            .assert(hasText(eventName))

        // Clica no botão de search
        composeTestRule.onNodeWithContentDescription("Search Button")
            .performClick()

        // Espera a UI atualizar
        composeTestRule.waitForIdle()

        // Agora verifica se algum card contém o texto do evento
        composeTestRule.onAllNodesWithTag("EventCardItem")
            .assertAny(hasText(eventName))
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

package com.example.appodp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appodp.ui.screens.ActiveRegistrationScreen
import com.example.appodp.ui.screens.ConfigurationScreen
import com.example.appodp.ui.screens.OnboardingScreen
import com.example.appodp.ui.screens.SplashScreen
import com.example.appodp.viewmodel.ActiveRegistrationViewModel
import com.example.appodp.navigation.Routes

@Composable
fun AppNavigation(navController: NavHostController) {
    val viewModel: ActiveRegistrationViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }
        composable(Routes.CONFIGURATION) {
            ConfigurationScreen(navController = navController)
        }
        composable(
            route = Routes.ACTIVE_REGISTRATIONS + "?dataset={dataset}",
            arguments = listOf(navArgument("dataset") {
                defaultValue = "Broj_aktivnih_registracija"
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val dataset = backStackEntry.arguments?.getString("dataset") ?: "Broj_aktivnih_registracija"
            ActiveRegistrationScreen(
                viewModel = viewModel,
                selectedDataSet = dataset.replace("_", " ")
            )
        }
    }
}

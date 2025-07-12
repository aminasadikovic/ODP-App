package com.example.appodp.navigation

import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appodp.viewmodel.ActiveRegistrationViewModel
import com.example.appodp.viewmodel.RegisteredVehiclesViewModel
import com.example.appodp.ui.screens.OnboardingScreen
import com.example.appodp.ui.screens.ConfigurationScreen
import com.example.appodp.ui.screens.RegisteredVehiclesScreen
import com.example.appodp.ui.screens.ActiveRegistrationScreen
import com.example.appodp.navigation.Routes
import com.example.appodp.ui.screens.RegisteredVehiclesIndividualsScreen
import com.example.appodp.ui.screens.SplashScreen
import com.example.appodp.viewmodel.RegisteredVehiclesIndividualsViewModel
import com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModel
import com.example.appodp.ui.screens.VehicleRegistrationRequestsScreen
import com.example.appodp.viewmodel.RegisteredVehiclesBulletinViewModel
import com.example.appodp.ui.screens.RegisteredVehiclesBulletinScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val activeViewModel: ActiveRegistrationViewModel = viewModel()
    val registeredViewModel: RegisteredVehiclesViewModel = viewModel()
    val viewModel: RegisteredVehiclesIndividualsViewModel = viewModel()
    val viewModel2: VehicleRegistrationRequestsViewModel = viewModel()
    val viewModel3: RegisteredVehiclesBulletinViewModel = viewModel()

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
        composable(Routes.ACTIVE_REGISTRATIONS) {
            ActiveRegistrationScreen(
                viewModel = activeViewModel
            )
        }

        composable(Routes.REGISTERED_VEHICLES) {
            RegisteredVehiclesScreen(viewModel = registeredViewModel)
        }

        composable(Routes.REGISTERED_VEHICLES_INDIVIDUALS) {
            RegisteredVehiclesIndividualsScreen(viewModel = viewModel)
        }

        composable(Routes.VEHICLE_REGISTRATION_REQUESTS) {
            VehicleRegistrationRequestsScreen(viewModel = viewModel2)
        }

        composable(Routes.REGISTERED_VEHICLES_BULLETIN) {
            RegisteredVehiclesBulletinScreen(viewModel = viewModel3)
        }


    }
}

package com.example.appodp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.appodp.ui.screens.OnboardingScreen
import com.example.appodp.ui.screens.ConfigurationScreen
import com.example.appodp.ui.screens.RegisteredVehiclesScreen
import com.example.appodp.ui.screens.SplashScreen
import com.example.appodp.ui.screens.VehicleRegistrationRequestsScreen
import com.example.appodp.ui.screens.ActiveRegistrationScreen
import com.example.appodp.ui.screens.VehicleRequestDetailsScreen
import com.example.appodp.data.model.VehicleRegistrationRequestResponse
import com.example.appodp.ui.screens.ActiveRegistrationsGraphScreen
import com.example.appodp.ui.screens.FavoriteRegisteredVehiclesScreen

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(
    navController: NavHostController,
    onToggleTheme: () -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController = navController, onToggleTheme = onToggleTheme)
        }
        composable(Routes.CONFIGURATION) {
            ConfigurationScreen(navController = navController,onToggleTheme = onToggleTheme)
        }
        composable(Routes.ACTIVE_REGISTRATIONS) {
            ActiveRegistrationScreen(navController=navController,onToggleTheme=onToggleTheme)
        }
        composable(Routes.REGISTERED_VEHICLES) {
            RegisteredVehiclesScreen(navController=navController,onToggleTheme=onToggleTheme)
        }
        composable(Routes.FAVORITE_REGISTERED_VEHICLES) {
            FavoriteRegisteredVehiclesScreen(navController = navController)
        }
        composable(Routes.ACTIVE_REGISTRATIONS_GRAPH) {
            ActiveRegistrationsGraphScreen(navController=navController,onToggleTheme=onToggleTheme)
        }
        composable(Routes.VEHICLE_REGISTRATION_REQUESTS) {
            VehicleRegistrationRequestsScreen(
                onNavigateToDetails = { request ->
                    navController.navigate(
                        Routes.vehicleRequestDetailsRoute(
                            registrationPlace = request.registrationPlace,
                            permanentRegistration = request.permanentRegistration,
                            firstTimeRequestsTotal = request.firstTimeRequestsTotal,
                            renewalRequestsTotal = request.renewalRequestsTotal,
                            ownershipChangesTotal = request.ownershipChangesTotal,
                            deregisteredTotal = request.deregisteredTotal
                        )
                    )
                },
                navController = navController,
                onToggleTheme = onToggleTheme
            )
        }
        composable(
            route = Routes.VEHICLE_REQUEST_DETAILS,
            arguments = listOf(
                navArgument("registrationPlace") { type = NavType.StringType },
                navArgument("permanentRegistration") { type = NavType.IntType },
                navArgument("firstTimeRequestsTotal") { type = NavType.IntType },
                navArgument("renewalRequestsTotal") { type = NavType.IntType },
                navArgument("ownershipChangesTotal") { type = NavType.IntType },
                navArgument("deregisteredTotal") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val registrationPlace = backStackEntry.arguments?.getString("registrationPlace")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: ""
            val permanentRegistration = backStackEntry.arguments?.getInt("permanentRegistration") ?: 0
            val firstTimeRequestsTotal = backStackEntry.arguments?.getInt("firstTimeRequestsTotal") ?: 0
            val renewalRequestsTotal = backStackEntry.arguments?.getInt("renewalRequestsTotal") ?: 0
            val ownershipChangesTotal = backStackEntry.arguments?.getInt("ownershipChangesTotal") ?: 0
            val deregisteredTotal = backStackEntry.arguments?.getInt("deregisteredTotal") ?: 0

            val tempRequest = VehicleRegistrationRequestResponse(
                registrationPlace = registrationPlace,
                permanentRegistration = permanentRegistration,
                firstTimeRequestsTotal = firstTimeRequestsTotal,
                renewalRequestsTotal = renewalRequestsTotal,
                ownershipChangesTotal = ownershipChangesTotal,
                deregisteredTotal = deregisteredTotal
            )
            VehicleRequestDetailsScreen(
                request = tempRequest,
                navController = navController
            )
        }
    }
}

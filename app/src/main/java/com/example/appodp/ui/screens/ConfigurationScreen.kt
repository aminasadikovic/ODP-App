package com.example.appodp.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appodp.navigation.Routes

@Composable
fun ConfigurationScreen(navController: NavController) {
    val options = listOf(
        "Broj aktivnih registracija",
        "Registrovana vozila",
        "Registrovana vozila od strane fizičkih lica",
        "Broj zahtjeva za registraciju vozila",
        "Bilteni registrovanih vozila"
    )
    var selectedDataSet by remember { mutableStateOf(options[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Izaberite tip podataka za prikaz:",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Radio Button lista
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedDataSet),
                    onClick = { selectedDataSet = option }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (selectedDataSet) {
                    "Broj aktivnih registracija" -> {
                        navController.navigate(Routes.ACTIVE_REGISTRATIONS) {
                            popUpTo(Routes.CONFIGURATION) { inclusive = true }
                        }
                    }
                    "Registrovana vozila" -> {
                        navController.navigate(Routes.REGISTERED_VEHICLES) {
                            popUpTo(Routes.CONFIGURATION) { inclusive = true }
                        }
                    }
                    "Registrovana vozila od strane fizičkih lica" -> {
                        navController.navigate(Routes.REGISTERED_VEHICLES_INDIVIDUALS) {
                            popUpTo(Routes.CONFIGURATION) { inclusive = true }
                        }
                    }
                    "Broj zahtjeva za registraciju vozila" -> {
                        navController.navigate(Routes.VEHICLE_REGISTRATION_REQUESTS) {
                            popUpTo(Routes.CONFIGURATION) { inclusive = true }
                        }
                    }
                    "Bilteni registrovanih vozila" -> {
                        navController.navigate(Routes.REGISTERED_VEHICLES_BULLETIN) {
                            popUpTo(Routes.CONFIGURATION) { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Prikaži podatke")
        }
    }
}

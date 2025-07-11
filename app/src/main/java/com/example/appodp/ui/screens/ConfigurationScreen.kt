package com.example.appodp.ui.screens

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appodp.navigation.Routes

@Composable
fun ConfigurationScreen(navController: NavController) {
    var selectedDataSet by remember { mutableStateOf("Broj aktivnih registracija") }

    val options = listOf(
        "Broj aktivnih registracija",
        "Registrovana vozila",
        "Registrovana vozila od strane fizičkih lica",
        "Broj zahtjeva za registraciju vozila",
        "Bilteni registrovanih vozila"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Izaberite tip podataka za prikaz:", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text(text = selectedDataSet)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            selectedDataSet = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (selectedDataSet) {
                    "Registrovana vozila" -> {
                        navController.navigate(Routes.REGISTERED_VEHICLES) {
                            popUpTo(Routes.CONFIGURATION) { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate("${Routes.ACTIVE_REGISTRATIONS}?dataset=${selectedDataSet.replace(" ", "_")}") {
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

package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.viewmodel.RegisteredVehiclesIndividualsViewModel

@Composable
fun RegisteredVehiclesIndividualsScreen(viewModel: RegisteredVehiclesIndividualsViewModel) {
    val vehicles by viewModel.vehicles.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVehicles()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrovana vozila fizičkih lica",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (error.isNotEmpty()) {
            Text(
                text = "Greška: $error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(vehicles) { vehicle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mjesto registracije: ${vehicle.registrationPlace}")
                            Text("Muškarci: ${vehicle.totalMale}")
                            Text("Žene: ${vehicle.totalFemale}")
                            Text("Ukupno: ${vehicle.total}")
                        }
                    }
                }
            }
        }
    }
}

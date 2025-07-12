package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.viewmodel.RegisteredVehiclesBulletinViewModel

@Composable
fun RegisteredVehiclesBulletinScreen(viewModel: RegisteredVehiclesBulletinViewModel) {
    val bulletins by viewModel.bulletins.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBulletins()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bilteni registrovanih vozila",
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
                items(bulletins) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Općina: ${item.municipality}")
                            Text("Marka: ${item.vehicleBrand}")
                            Text("Tip vozila: ${item.vehicleType}")
                            Text("Vrsta vozila: ${item.vehicleKind}")
                        }
                    }
                }
            }
        }
    }
}



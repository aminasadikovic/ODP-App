// com.example.appodp.ui.screens.VehicleRequestDetailsScreen.kt
package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.data.model.VehicleRegistrationRequestResponse // Koristi se za tip argumenta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRequestDetailsScreen(
    // Prima VehicleRegistrationRequestResponse direktno
    request: VehicleRegistrationRequestResponse,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalji zahtjeva") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Detalji za mjesto registracije: ${request.registrationPlace}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            DetailRow("Stalne registracije:", request.permanentRegistration.toString())
            DetailRow("Prve prijave:", request.firstTimeRequestsTotal.toString())
            DetailRow("Obnove:", request.renewalRequestsTotal.toString())
            DetailRow("Promjene vlasništva:", request.ownershipChangesTotal.toString())
            DetailRow("Odjavljeno:", request.deregisteredTotal.toString())
            // Nema vehicleCount i updateDate za prikaz
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
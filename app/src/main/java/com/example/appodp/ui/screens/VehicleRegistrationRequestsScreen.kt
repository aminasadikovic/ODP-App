package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModel

@Composable
fun VehicleRegistrationRequestsScreen(viewModel: VehicleRegistrationRequestsViewModel) {
    val requests by viewModel.requests.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRequests()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Zahtjevi za registraciju vozila",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (error.isNotEmpty()) {
            Text(
                text = "Greška: $error",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(requests) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mjesto: ${item.registrationPlace}")
                            Text("Prvi put: ${item.firstTimeRequestsTotal}")
                            Text("Produženje: ${item.renewalRequestsTotal}")
                            Text("Promjene vlasništva: ${item.ownershipChangesTotal}")
                            Text("Deregistracije: ${item.deregisteredTotal}")
                            Text("Stalna registracija: ${item.permanentRegistration}")
                        }
                    }
                }
            }
        }
    }
}

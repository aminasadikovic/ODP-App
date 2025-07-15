package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.data.model.VehicleRegistrationRequestResponse
import androidx.compose.foundation.shape.RoundedCornerShape // Import za RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController // Dodajemo NavHostController
import com.example.appodp.navigation.BottomNavigationBar // Import BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRequestDetailsScreen(
    request: VehicleRegistrationRequestResponse,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalji zahtjeva",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Detalji za mjesto: ${request.registrationPlace}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline
                    )

                    DetailRow(
                        label = "Stalne registracije:",
                        value = request.permanentRegistration.toString(),
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DetailRow(
                        label = "Prve prijave:",
                        value = request.firstTimeRequestsTotal.toString(),
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DetailRow(
                        label = "Obnove:",
                        value = request.renewalRequestsTotal.toString(),
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DetailRow(
                        label = "Promjene vlasništva:",
                        value = request.ownershipChangesTotal.toString(),
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DetailRow(
                        label = "Odjavljeno:",
                        value = request.deregisteredTotal.toString(),
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, textColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = textColor)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = textColor)
    }
}

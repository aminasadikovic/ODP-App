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
    navController: NavHostController // Dodajemo navController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalji zahtjeva", // Naslov ekrana
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Boja naslova
                    )
                },
                // Uklonjeno navigationIcon (dugme za nazad) jer će BottomNavigationBar to preuzeti
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant // Boja TopAppBar-a kao kartica
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController) // Dodana BottomNavigationBar
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Primijeni padding od Scaffold-a
                .padding(16.dp), // Dodatni padding za cijeli ekran
            horizontalAlignment = Alignment.CenterHorizontally, // Centriraj karticu horizontalno
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Dodatni razmak ispod TopAppBar-a

            Card(
                modifier = Modifier
                    .fillMaxWidth() // Kartica popunjava širinu
                    .padding(horizontal = 8.dp), // Mali horizontalni padding za karticu
                shape = RoundedCornerShape(16.dp), // Zaobljeni uglovi
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Sjaj kartice
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Boja kartice prema temi
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Padding unutar kartice
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Razmak između redova detalja
                ) {
                    Text(
                        text = "Detalji za mjesto: ${request.registrationPlace}",
                        style = MaterialTheme.typography.titleLarge, // Veći stil za glavni naslov unutar kartice
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Boja teksta kao na kartici
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline // Boja linije
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
fun DetailRow(label: String, value: String, textColor: androidx.compose.ui.graphics.Color) { // Dodan textColor parametar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = textColor) // Primijeni boju
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = textColor) // Primijeni boju
    }
}

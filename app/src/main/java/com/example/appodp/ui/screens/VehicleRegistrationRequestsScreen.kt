// com.example.appodp.ui.screens.VehicleRegistrationRequestsScreen.kt
package com.example.appodp.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // Uklonjen Icons.filled.Sort jer se više ne koristi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appodp.data.model.VehicleRegistrationRequestResponse
import com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModel
import com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRegistrationRequestsScreen(
    onNavigateToDetails: (VehicleRegistrationRequestResponse) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: VehicleRegistrationRequestsViewModel = viewModel(
        factory = VehicleRegistrationRequestsViewModelFactory(application)
    )

    val requests by viewModel.filteredAndSortedRequests.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedEntityId by viewModel.selectedEntityId.collectAsState()
    // clientSortOption i sve vezano za sortiranje je uklonjeno
    // val clientSortOption by viewModel.clientSortOption.collectAsState() // UKLONJENO

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    var expandedEntityDropdown by remember { mutableStateOf(false) }
    // expandedSortDropdown je uklonjen jer nema sortiranja
    // var expandedSortDropdown by remember { mutableStateOf(false) } // UKLONJENO

    val entityOptions = listOf(
        Triple("Svi", 0, 0),
        Triple("FBIH", 1, 0),
        Triple("RS", 2, 0),
        Triple("BD", 3, 0)
    )

    // sortOptions je uklonjen jer nema sortiranja
    // val sortOptions = listOf(...) // UKLONJENO

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

        // Dropdown za odabir entiteta je zadržan
        Text(text = "Odaberi entitet:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            Button(onClick = { expandedEntityDropdown = true }) {
                Text(text = entityOptions.find { it.second == selectedEntityId }?.first ?: "Svi")
            }
            DropdownMenu(
                expanded = expandedEntityDropdown,
                onDismissRequest = { expandedEntityDropdown = false }
            ) {
                entityOptions.forEach { (label, entityId, _) ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            viewModel.updateSelectedEntityId(entityId)
                            expandedEntityDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dugme za sortiranje i pripadajući Dropdown su uklonjeni
        // Box { ... } // UKLONJENO
        // Spacer(modifier = Modifier.height(16.dp)) // UKLONJENO, ako je bio samo za sortiranje

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.fetchDataFromNetwork() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (error != null) {
                    Text(
                        "Greška: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (isLoading && requests.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (requests.isEmpty() && !isLoading) {
                    Text("Nema pronađenih podataka.", color = MaterialTheme.colorScheme.onSurface)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(requests) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToDetails(item) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Prikazujemo SAMO Mjesto registracije
                                    Text(
                                        text = item.registrationPlace,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    // Ostali tekstualni prikazi su uklonjeni
                                    // Text(text = "Stalne registracije: ${item.permanentRegistration}") // UKLONJENO
                                    // ... i ostali
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// com.example.appodp.ui.screens.RegisteredVehiclesScreen.kt
package com.example.appodp.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appodp.viewmodel.RegisteredVehiclesViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appodp.viewmodel.RegisteredVehiclesViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.appodp.data.model.RegisteredVehicleUiItem // KLJUČNO: Import RegisteredVehicleUiItem
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisteredVehiclesScreen() { // Uklonjen onNavigateToDetails, jer nema detail ekrana
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: RegisteredVehiclesViewModel = viewModel(
        factory = RegisteredVehiclesViewModelFactory(application)
    )

    // KLJUČNA PROMJENA: Ovdje sada primamo List<RegisteredVehicleUiItem>
    val vehiclesUiItems by viewModel.filteredAndSortedVehicles.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedEntityId by viewModel.selectedEntityId.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val clientSortOption by viewModel.clientSortOption.collectAsState()

    var expandedEntityDropdown by remember { mutableStateOf(false) }
    var expandedYearDropdown by remember { mutableStateOf(false) }
    var expandedSortDropdown by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val entityOptions = listOf(
        Triple("Svi", 0, 0),
        Triple("FBIH", 1, 0),
        Triple("RS", 2, 0),
        Triple("BD", 3, 0)
    )

    val yearOptions = listOf(null) + (2023..2025).toList().reversed()
    val sortOptions = listOf("Bez sortiranja", "Po ukupnom broju (opadajuće)")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrovana vozila",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Entitet:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Button(onClick = { expandedEntityDropdown = true }) {
                        Text(text = entityOptions.find { it.second == selectedEntityId }?.first ?: "Svi")
                    }
                    DropdownMenu(expanded = expandedEntityDropdown, onDismissRequest = { expandedEntityDropdown = false }) {
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
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Godina:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Button(onClick = { expandedYearDropdown = true }) {
                        Text(text = selectedYear?.toString() ?: "Odaberite godinu")
                    }
                    DropdownMenu(expanded = expandedYearDropdown, onDismissRequest = { expandedYearDropdown = false }) {
                        yearOptions.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(text = year?.toString() ?: "Odaberite godinu") },
                                onClick = {
                                    viewModel.updateSelectedYear(year)
                                    expandedYearDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            Button(onClick = { expandedSortDropdown = true }) {
                Icon(imageVector = Icons.Default.Sort, contentDescription = "Sortiraj")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = clientSortOption.ifEmpty { "Bez sortiranja" })
            }
            DropdownMenu(expanded = expandedSortDropdown, onDismissRequest = { expandedSortDropdown = false }) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            viewModel.updateClientSortOption(option)
                            expandedSortDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

                if (isLoading && vehiclesUiItems.isEmpty()) { // Koristi vehiclesUiItems
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (vehiclesUiItems.isEmpty()) { // Koristi vehiclesUiItems
                    Text("Nema pronađenih podataka.", color = MaterialTheme.colorScheme.onSurface)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(vehiclesUiItems) { uiItem -> // KLJUČNA PROMJENA: Sada je 'uiItem' tipa RegisteredVehicleUiItem
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                // Uklonjen .clickable { onNavigateToDetails(vehicle) }
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        // Pristupite podacima preko uiItem.vehicle
                                        Text(text = "Mjesto registracije: ${uiItem.vehicle.registrationPlace}", style = MaterialTheme.typography.titleMedium)
                                        Spacer(Modifier.height(4.dp))
                                        Text(text = "Domaća vozila: ${uiItem.vehicle.totalDomestic}")
                                        Text(text = "Strana vozila: ${uiItem.vehicle.totalForeign}")
                                        Text(text = "Ukupno: ${uiItem.vehicle.total}")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Srce za favorite u donjem desnom uglu
                                    IconButton(
                                        onClick = {
                                            // KLJUČNA PROMJENA: Pozovi toggle funkciju s cijelim uiItem objektom
                                            viewModel.toggleFavoriteStatus(uiItem)
                                        },
                                        modifier = Modifier.align(Alignment.Bottom)
                                    ) {
                                        Icon(
                                            // KLJUČNA PROMJENA: Pristupite isFavorite statusu direktno iz uiItem
                                            imageVector = if (uiItem.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = if (uiItem.isFavorite) "Ukloni iz favorita" else "Dodaj u favorite",
                                            tint = if (uiItem.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
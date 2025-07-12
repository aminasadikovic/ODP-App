package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.viewmodel.RegisteredVehiclesViewModel

@Composable
fun RegisteredVehiclesScreen(viewModel: RegisteredVehiclesViewModel) {
    val vehicles by viewModel.vehicles.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedEntityLabel by remember { mutableStateOf("Svi") }
    var selectedYearText by remember { mutableStateOf("") }  // String input od korisnika
    var selectedMonthText by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("") }

    val entityOptions = listOf("Svi", "FBIH", "RS", "BD")
    val sortOptions = listOf("Bez sortiranja", "Po mjesecu (1-12)", "Po ukupnom broju (opadajuće)")

    // Parsiraj stringove u Int? da prosledimo ViewModelu
    val selectedYear = selectedYearText.toIntOrNull()
    val selectedMonth = selectedMonthText.toIntOrNull()

    // Mapiranje entiteta u ID
    val entityId = when (selectedEntityLabel) {
        "FBIH" -> 1
        "RS" -> 2
        "BD" -> 3
        else -> 0
    }

    // Kad se promijene filteri ili sortiranje, reload podataka
    LaunchedEffect(entityId, selectedYear, selectedMonth) {
        viewModel.loadVehicles(entityId, selectedYear, selectedMonth)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrovana vozila", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

        // Entity filter
        Text("Odaberi entitet:")
        var entityExpanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { entityExpanded = true }) {
                Text(selectedEntityLabel)
            }
            DropdownMenu(expanded = entityExpanded, onDismissRequest = { entityExpanded = false }) {
                entityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedEntityLabel = option
                            entityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Year filter (text input)
        OutlinedTextField(
            value = selectedYearText,
            onValueChange = { selectedYearText = it.filter { c -> c.isDigit() } },
            label = { Text("Godina (npr. 2023)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Month filter (text input)
        OutlinedTextField(
            value = selectedMonthText,
            onValueChange = {
                selectedMonthText = it.filter { c -> c.isDigit() }
                    .take(2) // max 2 cifre
            },
            label = { Text("Mjesec (1-12)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Sortiranje dropdown
        Text("Sortiraj po:")
        var sortExpanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { sortExpanded = true }) {
                Text(sortOption.ifEmpty { "Bez sortiranja" })
            }
            DropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sortOption = option
                            sortExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (error.isNotEmpty()) {
            Text("Greška: $error", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        } else {
            val listToDisplay = when (sortOption) {
                "Po mjesecu (1-12)" -> vehicles.sortedBy { it.month ?: 0 }
                "Po ukupnom broju (opadajuće)" -> vehicles.sortedByDescending { it.total }
                else -> vehicles
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listToDisplay) { vehicle ->
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mjesto registracije: ${vehicle.registrationPlace}", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("Domaća vozila: ${vehicle.totalDomestic}")
                            Text("Strana vozila: ${vehicle.totalForeign}")
                            Text("Ukupno: ${vehicle.total}")
                            Text("Godina: ${vehicle.year ?: "-"}")
                            Text("Mjesec: ${vehicle.month ?: "-"}")
                        }
                    }
                }
            }
        }
    }
}

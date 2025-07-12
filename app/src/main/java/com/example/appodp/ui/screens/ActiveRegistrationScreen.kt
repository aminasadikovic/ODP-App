package com.example.appodp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appodp.viewmodel.ActiveRegistrationViewModel

@Composable
fun ActiveRegistrationScreen(viewModel: ActiveRegistrationViewModel) {
    val entityOptions = listOf("Svi", "FBIH", "RS", "BD")
    var selectedEntityLabel by remember { mutableStateOf("Svi") }
    var isSorted by remember { mutableStateOf(false) }

    val registrations by viewModel.registrations.collectAsState()
    val error by viewModel.error.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    val entityId = when (selectedEntityLabel) {
        "FBIH" -> 1
        "RS" -> 2
        "BD" -> 3
        else -> 0
    }

    // Učitavanje kad entitet promijeniš
    LaunchedEffect(entityId) {
        viewModel.loadRegistrations(entityId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aktivne registracije",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 🔽 Dropdown za entitet
        Text("Odaberi entitet:", style = MaterialTheme.typography.titleMedium)
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedEntityLabel)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                entityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedEntityLabel = option
                            expanded = false
                            isSorted = false // resetiraj sortiranje
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔃 Dugme sa ikonom za toggle sortiranje
        Button(
            onClick = { isSorted = !isSorted },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Sort, contentDescription = "Sortiraj")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isSorted) "Ukloni sortiranje" else "Sortiraj po broju registracija"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error.isNotEmpty()) {
            Text("Greška: $error", color = MaterialTheme.colorScheme.error)
        } else {
            val listToDisplay = if (isSorted) {
                registrations.sortedByDescending { it.total }
            } else {
                registrations
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listToDisplay) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mjesto registracije: ${item.registrationPlace}")
                            Text("Ukupno: ${item.total}")
                        }
                    }
                }
            }
        }
    }
}

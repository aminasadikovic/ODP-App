// com.example.appodp.ui.screens.ActiveRegistrationScreen.kt
package com.example.appodp.ui.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.viewmodel.ActiveRegistrationViewModel
import com.example.appodp.viewmodel.ActiveRegistrationViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.compose.chart.Chart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRegistrationScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: ActiveRegistrationViewModel = viewModel(
        factory = ActiveRegistrationViewModelFactory(application)
    )

    val registrations by viewModel.filteredAndSortedRegistrations.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val clientSearchText by viewModel.clientSearchText.collectAsState()
    val sortByTotalAscending by viewModel.clientSortByTotalAscending.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    // Priprema podataka za grafikon
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    val registrationPlaces = remember(registrations) { registrations.map { it.registrationPlace } }

    LaunchedEffect(registrations) {
        val entries = registrations.mapIndexed { index, registration ->
            FloatEntry(x = index.toFloat(), y = registration.total.toFloat())
        }
        chartEntryModelProducer.setEntries(listOf(entries))
    }

    // Formatter za X-osu (horizontalnu) da prikazuje nazive mjesta
    val bottomAxisValueFormatter =
        remember(registrationPlaces) {
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                registrationPlaces.getOrNull(value.toInt()) ?: ""
            }
        }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.fetchDataFromNetwork() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aktivne registracije",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        shareActiveRegistrationsData(context, registrations)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Podijeli podatke o aktivnim registracijama",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = clientSearchText ?: "",
                onValueChange = { viewModel.updateClientSearchText(it) },
                label = { Text("Pretraži (Mjesto registracije)") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Pretraga") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.toggleSortByTotal() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Sort, contentDescription = "Sortiraj")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (sortByTotalAscending) "Ukupno: Uzlazno" else "Ukupno: Silazno"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Početak bloka za grafikon ---
            if (registrations.isNotEmpty()) { // Prikazujemo grafikon samo ako ima podataka
                Text(
                    text = "Grafički prikaz ukupnog broja registracija",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Dajte grafikonu fiksnu visinu
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Chart(
                        chart = columnChart(), // Stupčasti dijagram
                        chartModelProducer = chartEntryModelProducer, // <-- ovo je novi tačan parametar
                        startAxis = rememberStartAxis(), // Y-osa
                        bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter), // X-osa sa nazivima mjesta
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            // --- Kraj bloka za grafikon ---

            if (error != null) {
                Text(
                    "Greška: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (isLoading && registrations.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (registrations.isEmpty()) {
                Text("Nema pronađenih podataka.", color = MaterialTheme.colorScheme.onSurface)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(registrations) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Mjesto registracije: ${item.registrationPlace}")
                                Text(
                                    "Ukupno: ${item.total}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Funkcija za dijeljenje podataka o aktivnim registracijama (ostaje ista)
fun shareActiveRegistrationsData(context: Context, registrations: List<ActiveRegistration>) {
    val odpLink = "https://odp.iddeea.gov.ba/datasets/number-of-active-registrations/18"

    val summaryText = registrations.take(5)
        .joinToString(separator = "\n") { "${it.registrationPlace}: ${it.total}" }

    val shareText = """
        Pregled aktivnih registracija:
        ${if (registrations.isNotEmpty()) summaryText else "Nema dostupnih podataka."}
        ${if (registrations.size > 5) "... (${registrations.size - 5} više stavki)" else ""}

        Više podataka pronađite na: $odpLink
        (Izvor: Aplikacija AppODP)
    """.trimIndent()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Podijeli podatke o aktivnim registracijama putem...")
    ContextCompat.startActivity(context, shareIntent, null)
}
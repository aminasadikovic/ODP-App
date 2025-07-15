// com.example.appodp.ui.screens.ActiveRegistrationsGraphScreen.kt
package com.example.appodp.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appodp.navigation.BottomNavigationBar
import com.example.appodp.ui.theme.DarkBackground
import com.example.appodp.viewmodel.ActiveRegistrationViewModel
import com.example.appodp.viewmodel.ActiveRegistrationViewModelFactory

// Vico Charts imports
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRegistrationsGraphScreen(
    navController: NavHostController,
    onToggleTheme: () -> Unit // I dalje primamo, ali ne koristimo u TopAppBaru
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: ActiveRegistrationViewModel = viewModel(
        factory = ActiveRegistrationViewModelFactory(application)
    )

    val registrationsByEntity by viewModel.registrationsByEntity.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // isDarkThemeActive je i dalje potreban ako se koristi za bojenje UI elemenata izvan TopAppBar-a
    val isDarkThemeActive = MaterialTheme.colorScheme.background == DarkBackground

    // Priprema podataka za grafikon
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    val entityNames = remember(registrationsByEntity) { registrationsByEntity.keys.toList().sorted() }

    LaunchedEffect(registrationsByEntity) {
        val entries = entityNames.mapIndexed { index, entityName ->
            FloatEntry(x = index.toFloat(), y = registrationsByEntity[entityName]?.toFloat() ?: 0f)
        }
        chartEntryModelProducer.setEntries(listOf(entries))
    }

    // Formatter za X-osu (horizontalnu) da prikazuje nazive entiteta
    val bottomAxisValueFormatter =
        remember(entityNames) {
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                entityNames.getOrNull(value.toInt()) ?: ""
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Prikaz grafa", // Naslov
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, // Centriran naslov
                        modifier = Modifier.fillMaxWidth(), // Omogućava da naslov zauzme cijelu širinu za centriranje
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    // Uklonjeno: Dugme za promjenu teme
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Omogući kartici da se proširi
                    .padding(bottom = 16.dp), // Dodan donji padding za razmak od bottom nav
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                        Text("Učitavanje podataka za grafikon...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else if (error != null) {
                        Text(
                            "Greška pri učitavanju podataka: $error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    } else if (registrationsByEntity.isEmpty()) {
                        Text(
                            "Nema dostupnih podataka za grafikon.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "Ukupan broj aktivnih registracija po entitetima",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Chart(
                            chart = columnChart(),
                            chartModelProducer = chartEntryModelProducer,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp) // Fiksna visina za grafikon
                        )
                    }
                }
            }
        }
    }
}

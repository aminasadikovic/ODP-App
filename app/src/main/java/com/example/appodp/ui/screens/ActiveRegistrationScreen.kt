// com.example.appodp.ui.screens.ActiveRegistrationScreen.kt
package com.example.appodp.ui.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape // Import za RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults // Import za TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight // Import za FontWeight
import androidx.compose.ui.text.style.TextAlign // Import za TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // Promijenjen tip na NavHostController
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.viewmodel.ActiveRegistrationViewModel
import com.example.appodp.viewmodel.ActiveRegistrationViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.appodp.navigation.BottomNavigationBar // Import BottomNavigationBar
import com.example.appodp.ui.theme.DarkBackground // Import DarkBackground za provjeru teme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRegistrationScreen(
    navController: NavHostController, // Dodan navController
    onToggleTheme: () -> Unit // Dodan callback za promjenu teme
) {
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

    val isDarkThemeActive = MaterialTheme.colorScheme.background == DarkBackground

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aktivne registracije",
                        style = MaterialTheme.typography.headlineMedium, // Veći i istaknutiji naslov
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start, // Poravnaj lijevo
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    // Dugme za dijeljenje
                    IconButton(
                        onClick = { shareActiveRegistrationsData(context, registrations) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Podijeli podatke",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    // Dugme za promjenu teme
                    IconButton(
                        onClick = onToggleTheme
                    ) {
                        Icon(
                            imageVector = if (isDarkThemeActive) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkThemeActive) "Prebaci na svijetlu temu" else "Prebaci na tamnu temu",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // Boja TopAppBar-a kao pozadina
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
                .padding(horizontal = 16.dp), // Horizontalni padding za cijeli ekran
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Razmak ispod TopAppBar-a

            // Glavni sadržaj (pretraga, sortiranje, lista) unutar Card-a
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp), // DODANO: Donji padding za karticu
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Boja kartice prema temi
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Padding unutar kartice
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = clientSearchText,
                        onValueChange = { viewModel.updateClientSearchText(it) },
                        label = { Text("Pretraži (Mjesto)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Pretraga", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.toggleSortByTotal() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface), // Boja dugmeta kao manja kartica
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sortiraj",
                            tint = MaterialTheme.colorScheme.onSurface // Boja ikone kao tekst na manjoj kartici
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (sortByTotalAscending) "Ukupno: Uzlazno" else "Ukupno: Silazno",
                            color = MaterialTheme.colorScheme.onSurface // Boja teksta kao na manjoj kartici
                        )
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

                            if (isLoading && registrations.isEmpty()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else if (registrations.isEmpty()) {
                                Text(
                                    "Nema pronađenih podataka.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Boja teksta kao na kartici
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(registrations) { item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Boja kartice za stavku
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = "Mjesto registracije: ${item.registrationPlace}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface // Boja teksta na stavci
                                                )
                                                Text(
                                                    text = "Ukupno: ${item.total}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface // Boja teksta na stavci
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

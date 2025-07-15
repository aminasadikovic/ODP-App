// com.example.appodp.ui.screens.FavoriteRegisteredVehiclesScreen.kt
package com.example.appodp.ui.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape // Import za RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults // Import za TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight // Import za FontWeight
import androidx.compose.ui.text.style.TextAlign // Import za TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // Promijenjen tip na NavHostController
import com.example.appodp.data.model.RegisteredVehicleUiItem
import com.example.appodp.viewmodel.RegisteredVehiclesViewModel
import com.example.appodp.viewmodel.RegisteredVehiclesViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.appodp.navigation.BottomNavigationBar // Import BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRegisteredVehiclesScreen(navController: NavHostController) { // Promijenjen tip
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: RegisteredVehiclesViewModel = viewModel(
        factory = RegisteredVehiclesViewModelFactory(application)
    )

    val allVehiclesUiItems by viewModel.filteredAndSortedVehicles.collectAsState(initial = emptyList())
    val favoriteVehiclesUiItems = remember(allVehiclesUiItems) {
        allVehiclesUiItems.filter { it.isFavorite }
    }

    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favoriti", // Naslov "Favoriti"
                        style = MaterialTheme.typography.headlineLarge, // Veći i istaknutiji naslov
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(), // Centriraj naslov
                        color = MaterialTheme.colorScheme.onBackground // Boja teksta naslova
                    )
                },
                // Uklonjeno navigationIcon (dugme za nazad)
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // Neka TopAppBar bude boje pozadine
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
            verticalArrangement = Arrangement.Top // Postavi sadržaj na vrh
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Razmak ispod TopAppBar-a

            // Glavni sadržaj unutar Card-a
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp),
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

                            if (isLoading && favoriteVehiclesUiItems.isEmpty()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else if (favoriteVehiclesUiItems.isEmpty()) {
                                Text(
                                    "Nema sačuvanih vozila.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Boja teksta kao na kartici
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(favoriteVehiclesUiItems) { uiItem ->
                                        // Kartica za pojedinačno vozilo unutar LazyColumn-a
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Malo tamnija/svjetlija boja od glavne kartice
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "Mjesto registracije: ${uiItem.vehicle.registrationPlace}",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSurface // Boja teksta na manjoj kartici
                                                    )
                                                    Spacer(Modifier.height(4.dp))
                                                    Text(
                                                        text = "Domaća vozila: ${uiItem.vehicle.totalDomestic}",
                                                        color = MaterialTheme.colorScheme.onSurface // Boja teksta na manjoj kartici
                                                    )
                                                    Text(
                                                        text = "Strana vozila: ${uiItem.vehicle.totalForeign}",
                                                        color = MaterialTheme.colorScheme.onSurface // Boja teksta na manjoj kartici
                                                    )
                                                    Text(
                                                        text = "Ukupno: ${uiItem.vehicle.total}",
                                                        color = MaterialTheme.colorScheme.onSurface // Boja teksta na manjoj kartici
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    // Srce za favorite
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.toggleFavoriteStatus(uiItem)
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = if (uiItem.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                            contentDescription = if (uiItem.isFavorite) "Ukloni iz favorita" else "Dodaj u favorite",
                                                            tint = if (uiItem.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface // Boja ikone na manjoj kartici
                                                        )
                                                    }

                                                    // Dugme za dijeljenje
                                                    IconButton(
                                                        onClick = {
                                                            shareVehicleData(context, uiItem)
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Share,
                                                            contentDescription = "Podijeli podatke o vozilu",
                                                            tint = MaterialTheme.colorScheme.onSurface // Boja ikone na manjoj kartici
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
    }
}

// Funkcija za dijeljenje podataka o vozilu - izvan Composable funkcije
fun shareVehicleData(context: Context, vehicle: RegisteredVehicleUiItem) {
    val shareText = """
        Informacije o registrovanom vozilu:
        Mjesto registracije: ${vehicle.vehicle.registrationPlace}
        Domaća vozila: ${vehicle.vehicle.totalDomestic}
        Strana vozila: ${vehicle.vehicle.totalForeign}
        Ukupno: ${vehicle.vehicle.total}
        (Izvor: Aplikacija AppODP)
    """.trimIndent()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Podijeli podatke o vozilu putem...")
    ContextCompat.startActivity(context, shareIntent, null)
}

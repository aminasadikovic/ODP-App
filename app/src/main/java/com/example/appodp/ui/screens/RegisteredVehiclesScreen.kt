package com.example.appodp.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appodp.viewmodel.RegisteredVehiclesViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.appodp.viewmodel.RegisteredVehiclesViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.appodp.data.model.RegisteredVehicleUiItem
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
// Uklonjen import za Share ikonu
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.appodp.navigation.BottomNavigationBar
import com.example.appodp.ui.theme.DarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisteredVehiclesScreen(
    navController: NavHostController,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: RegisteredVehiclesViewModel = viewModel(
        factory = RegisteredVehiclesViewModelFactory(application)
    )

    val vehiclesUiItems by viewModel.filteredAndSortedVehicles.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedEntityId by viewModel.selectedEntityId.collectAsState()
    val sortByTotalAscending by viewModel.sortByTotalAscending.collectAsState()

    var expandedEntityDropdown by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val isDarkThemeActive = MaterialTheme.colorScheme.background == DarkBackground

    val entityOptions = listOf(
        Triple("Svi", 0, 0),
        Triple("FBiH", 1, 0),
        Triple("RS", 2, 0),
        Triple("BD", 3, 0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrovana vozila",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
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
                    .weight(1f)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Filter po entitetu
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Entitet:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                Button(
                                    onClick = { expandedEntityDropdown = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = entityOptions.find { it.second == selectedEntityId }?.first ?: "Svi",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                DropdownMenu(
                                    expanded = expandedEntityDropdown,
                                    onDismissRequest = { expandedEntityDropdown = false }
                                ) {
                                    entityOptions.forEach { (label, entityId, _) ->
                                        DropdownMenuItem(
                                            text = { Text(text = label, color = MaterialTheme.colorScheme.onSurface) },
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
                            Text(
                                text = "Sortiraj:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.toggleSortByTotal() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Sortiraj",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (sortByTotalAscending) "Ukupno: Uzlazno" else "Ukupno: Silazno",
                                    color = MaterialTheme.colorScheme.onSurface
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

                            if (isLoading && vehiclesUiItems.isEmpty()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else if (vehiclesUiItems.isEmpty()) {
                                Text(
                                    "Nema pronađenih podataka.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(vehiclesUiItems) { uiItem ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Spacer(Modifier.height(4.dp))
                                                    Text(
                                                        text = "Domaća vozila: ${uiItem.vehicle.totalDomestic}",
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "Strana vozila: ${uiItem.vehicle.totalForeign}",
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "Ukupno: ${uiItem.vehicle.total}",
                                                        color = MaterialTheme.colorScheme.onSurface
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
                                                            tint = if (uiItem.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
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

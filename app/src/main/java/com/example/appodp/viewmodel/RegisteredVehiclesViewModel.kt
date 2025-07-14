// com.example.appodp.viewmodel.RegisteredVehiclesViewModel.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.local.entity.RegisteredVehicleEntity
import com.example.appodp.data.local.entity.toDomain
import com.example.appodp.data.model.RegisteredVehicleRequest
import com.example.appodp.data.repository.RegisteredVehiclesRepository
import com.example.appodp.data.model.RegisteredVehicleUiItem // NOVO: Import RegisteredVehicleUiItem
import com.example.appodp.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RegisteredVehiclesViewModel(
    private val application: Application,
    private val repository: RegisteredVehiclesRepository
) : ViewModel() {

    private val _allVehicles = MutableStateFlow<List<RegisteredVehicleEntity>>(emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedEntityId = MutableStateFlow<Int?>(0)
    val selectedEntityId: StateFlow<Int?> = _selectedEntityId.asStateFlow()

    private val _selectedYear = MutableStateFlow<Int?>(null)
    val selectedYear: StateFlow<Int?> = _selectedYear.asStateFlow()

    private val _clientSortOption = MutableStateFlow<String>("Bez sortiranja")
    val clientSortOption: StateFlow<String> = _clientSortOption.asStateFlow()

    // KLJUČNA PROMJENA: Ovaj Flow će emitirati List<RegisteredVehicleUiItem>
    private val _filteredAndSortedVehicles = MutableStateFlow<List<RegisteredVehicleUiItem>>(emptyList())
    val filteredAndSortedVehicles: StateFlow<List<RegisteredVehicleUiItem>> = _filteredAndSortedVehicles.asStateFlow()

    init {
        repository.getCachedRegisteredVehicles()
            .onEach { cachedEntities ->
                _allVehicles.value = cachedEntities
            }
            .launchIn(viewModelScope)

        fetchDataFromNetwork()

        combine(
            _selectedEntityId,
            _selectedYear
        ) { _, _ ->
            fetchDataFromNetwork()
        }.launchIn(viewModelScope)

        // Klijentsko filtriranje i sortiranje (primijeni na _allVehicles)
        combine(
            _allVehicles,
            _clientSortOption,
            _selectedEntityId,
            _selectedYear
        ) { allVehiclesEntities, sortOption, entityFilterId, yearFilter ->
            val filteredList = allVehiclesEntities.filter { entity ->
                val matchesEntity = when (entityFilterId) {
                    0 -> true
                    1 -> entity.registrationPlace.contains("FBIH", ignoreCase = true)
                    2 -> entity.registrationPlace.contains("RS", ignoreCase = true)
                    3 -> entity.registrationPlace.contains("BD", ignoreCase = true)
                    else -> true
                }
                val matchesYear = (yearFilter == null)
                matchesEntity && matchesYear
            }
            // KLJUČNA PROMJENA: Mapiramo entitete u RegisteredVehicleUiItem
            performClientSideSorting(filteredList, sortOption).map { entity ->
                RegisteredVehicleUiItem(
                    vehicle = entity.toDomain(),
                    isFavorite = entity.isFavorite,
                    entityId = entity.id // Dodajemo ID iz Entiteta (za Room operacije)
                )
            }
        }
            .onEach { sortedUiItemsList ->
                _filteredAndSortedVehicles.value = sortedUiItemsList
            }
            .launchIn(viewModelScope)
    }

    fun updateSelectedEntityId(entityId: Int?) {
        _selectedEntityId.value = entityId
    }

    fun updateSelectedYear(year: Int?) {
        _selectedYear.value = year
    }

    fun updateClientSortOption(option: String) {
        _clientSortOption.value = option
    }

    // KLJUČNA PROMJENA: Funkcija za prebacivanje statusa favorita sada prima RegisteredVehicleUiItem
    fun toggleFavoriteStatus(uiItem: RegisteredVehicleUiItem) {
        viewModelScope.launch {
            // Proslijedimo ID entiteta i trenutni isFavorite status
            repository.toggleFavoriteStatus(uiItem.entityId, uiItem.isFavorite)
        }
    }

    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null

        if (NetworkUtils.isConnectedToInternet(application)) {
            val yearString = _selectedYear.value?.toString()

            val request = RegisteredVehicleRequest(
                updateDate = null,
                entityId = _selectedEntityId.value,
                cantonId = null,
                municipalityId = null,
                year = yearString,
                month = null
            )

            viewModelScope.launch {
                repository.fetchAndCacheRegisteredVehicles(
                    request = request,
                    scope = viewModelScope,
                    onSuccess = { _ ->
                        _isLoading.value = false
                        _error.value = null
                    },
                    onError = { err ->
                        _error.value = err
                        _isLoading.value = false
                    }
                )
            }
        } else {
            _error.value = "Nema internetske veze. Prikazuju se keširani podaci."
            _isLoading.value = false
        }
    }

    private fun performClientSideSorting(
        data: List<RegisteredVehicleEntity>,
        sortOption: String
    ): List<RegisteredVehicleEntity> {
        return when (sortOption) {
            "Po ukupnom broju (opadajuće)" -> data.sortedByDescending { it.total }
            else -> data
        }
    }
}
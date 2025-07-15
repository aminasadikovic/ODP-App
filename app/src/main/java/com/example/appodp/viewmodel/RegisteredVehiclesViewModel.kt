// com.example.appodp.viewmodel.RegisteredVehiclesViewModel.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle // Import SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.local.entity.RegisteredVehicleEntity
import com.example.appodp.data.local.entity.toDomain
import com.example.appodp.data.model.RegisteredVehicleRequest
import com.example.appodp.data.repository.RegisteredVehiclesRepository
import com.example.appodp.data.model.RegisteredVehicleUiItem
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
    private val repository: RegisteredVehiclesRepository,
    private val savedStateHandle: SavedStateHandle // Dodajemo SavedStateHandle
) : ViewModel() {

    private val _allVehicles = MutableStateFlow<List<RegisteredVehicleEntity>>(emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Koristimo SavedStateHandle za selectedEntityId
    // Inicijalna vrijednost 0 odgovara "Svi" u UI-u
    private val _selectedEntityId = savedStateHandle.getStateFlow("selectedEntityId", 0)
    val selectedEntityId: StateFlow<Int> = _selectedEntityId

    // Koristimo SavedStateHandle za sortByTotalAscending
    // Inicijalna vrijednost false znači opadajuće sortiranje po defaultu
    private val _sortByTotalAscending = savedStateHandle.getStateFlow("sortByTotalAscending", false)
    val sortByTotalAscending: StateFlow<Boolean> = _sortByTotalAscending

    // Ovaj Flow će emitirati List<RegisteredVehicleUiItem>
    private val _filteredAndSortedVehicles = MutableStateFlow<List<RegisteredVehicleUiItem>>(emptyList())
    val filteredAndSortedVehicles: StateFlow<List<RegisteredVehicleUiItem>> = _filteredAndSortedVehicles.asStateFlow()

    init {
        // Učitavanje keširanih podataka i ažuriranje _allVehicles
        // Ovo se radi samo jednom pri inicijalizaciji, a podaci se ažuriraju putem fetchDataFromNetwork
        repository.getCachedRegisteredVehicles()
            .onEach { cachedEntities ->
                _allVehicles.value = cachedEntities
            }
            .launchIn(viewModelScope)

        // Kombinacija za sortiranje (filtriranje po entitetu se radi na nivou mreže/API-ja)
        // Ovaj combine sluša promjene u _allVehicles i _sortByTotalAscending
        combine(
            _allVehicles,
            _sortByTotalAscending
        ) { allVehiclesEntities, sortByTotalAscending ->
            // Izvršavamo samo klijentsko sortiranje na dohvaćenim podacima
            performClientSideSorting(allVehiclesEntities, sortByTotalAscending)
        }
            .onEach { sortedUiItemsList ->
                _filteredAndSortedVehicles.value = sortedUiItemsList
            }
            .launchIn(viewModelScope)

        // Slušamo promjene u _selectedEntityId i ponovo dohvaćamo podatke sa mreže
        // Ovo osigurava da se podaci ponovo dohvate sa API-ja sa novim entityId
        _selectedEntityId
            .onEach {
                fetchDataFromNetwork()
            }
            .launchIn(viewModelScope)

        // Odmah pokušaj dohvaćanja podataka sa mreže pri inicijalizaciji
        fetchDataFromNetwork()
    }

    /**
     * Ažurira odabrani ID entiteta i pokreće novo dohvaćanje podataka.
     * @param entityId ID odabranog entiteta (0 za "Svi", 1 za FBiH, itd.).
     */
    fun updateSelectedEntityId(entityId: Int) {
        savedStateHandle["selectedEntityId"] = entityId
        // fetchDataFromNetwork() će se automatski pozvati zbog .onEach na _selectedEntityId
    }

    /**
     * Prebacuje opciju sortiranja između uzlaznog i silaznog po ukupnom broju vozila.
     */
    fun toggleSortByTotal() {
        savedStateHandle["sortByTotalAscending"] = !(_sortByTotalAscending.value)
    }

    /**
     * Prebacuje status favorita za dato vozilo.
     * @param uiItem Vozilo čiji status favorita treba promijeniti.
     */
    fun toggleFavoriteStatus(uiItem: RegisteredVehicleUiItem) {
        viewModelScope.launch {
            // Proslijedimo ID entiteta i trenutni isFavorite status repozitorijumu
            repository.toggleFavoriteStatus(uiItem.entityId, uiItem.isFavorite)
        }
    }

    /**
     * Dohvaća podatke o registrovanim vozilima sa mreže.
     * Podaci se dohvaćaju na osnovu trenutno odabranog entiteta.
     */
    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null

        if (NetworkUtils.isConnectedToInternet(application)) {
            val request = RegisteredVehicleRequest(
                updateDate = "2025-07-03", // Fiksni datum za primjer, može biti dinamički
                entityId = _selectedEntityId.value, // Koristi odabrani entitet iz SavedStateHandle
                cantonId = null,
                municipalityId = null,
                year = null, // Uklonjen filter po godini
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

    /**
     * Obavlja klijentsko sortiranje liste registrovanih vozila.
     * Filtriranje po entitetu se očekuje da je već obavljeno na serverskoj strani.
     * @param data Lista RegisteredVehicleEntity objekata za sortiranje.
     * @param sortByTotalAscending True za uzlazno sortiranje, false za silazno.
     * @return Sortirana lista RegisteredVehicleUiItem objekata.
     */
    private fun performClientSideSorting(
        data: List<RegisteredVehicleEntity>,
        sortByTotalAscending: Boolean
    ): List<RegisteredVehicleUiItem> {
        val sortedList = if (sortByTotalAscending) {
            data.sortedBy { it.total }
        } else {
            data.sortedByDescending { it.total }
        }

        // Mapiranje u RegisteredVehicleUiItem
        return sortedList.map { entity ->
            RegisteredVehicleUiItem(
                vehicle = entity.toDomain(),
                isFavorite = entity.isFavorite,
                entityId = entity.id // Dodajemo ID iz Entiteta (za Room operacije)
            )
        }
    }
}

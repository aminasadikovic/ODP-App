// com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModel.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.VehicleRegistrationRequestRequest
import com.example.appodp.data.model.VehicleRegistrationRequestResponse
import com.example.appodp.data.repository.VehicleRegistrationRequestsRepository
import com.example.appodp.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class VehicleRegistrationRequestsViewModel(
    private val application: Application,
    private val repository: VehicleRegistrationRequestsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _requests = MutableStateFlow<List<VehicleRegistrationRequestResponse>>(emptyList())
    val requests: StateFlow<List<VehicleRegistrationRequestResponse>> = _requests.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Koristimo SavedStateHandle za selectedEntityId
    private val _selectedEntityId = savedStateHandle.getStateFlow("selectedEntityId", 0) // 0 za "Svi"
    val selectedEntityId: StateFlow<Int> = _selectedEntityId

    // Uklonjeno: _sortByTotalAscending StateFlow

    // _filteredAndSortedRequests sada samo prati _requests jer nema klijentskog sortiranja
    private val _filteredAndSortedRequests = MutableStateFlow<List<VehicleRegistrationRequestResponse>>(emptyList())
    val filteredAndSortedRequests: StateFlow<List<VehicleRegistrationRequestResponse>> = _filteredAndSortedRequests.asStateFlow()

    init {
        // 1. Pratite keširane podatke iz Room baze
        repository.getCachedVehicleRegistrationRequests()
            .onEach { cachedList ->
                _requests.value = cachedList
                // Kada se _requests ažurira, ažuriramo i _filteredAndSortedRequests direktno
                _filteredAndSortedRequests.value = cachedList
            }
            .launchIn(viewModelScope)

        // 2. Slušajte promjene u _selectedEntityId i ponovo dohvaćajte podatke sa mreže
        _selectedEntityId
            .onEach {
                fetchDataFromNetwork()
            }
            .launchIn(viewModelScope)

        // Uklonjeno: combine blok za klijentsko sortiranje

        // Odmah pokušaj dohvaćanja podataka s mreže pri inicijalizaciji
        fetchDataFromNetwork()
    }

    fun updateSelectedEntityId(entityId: Int) {
        savedStateHandle["selectedEntityId"] = entityId
    }

    // Uklonjeno: toggleSortByTotal() funkcija

    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null

        if (NetworkUtils.isConnectedToInternet(application)) {
            val currentEntityId = _selectedEntityId.value

            val request = VehicleRegistrationRequestRequest(
                updateDate = "2025-07-03", // Primjer, možete razmisliti o dinamičkim vrijednostima ili ukloniti ako API podržava null
                entityId = currentEntityId, // Koristi odabrani entitet iz SavedStateHandle
                cantonId = null,
                municipalityId = null,
                year = null,
                month = null
            )

            viewModelScope.launch {
                repository.fetchAndCacheRequests(
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

    // Uklonjeno: performClientSideSorting funkcija
}

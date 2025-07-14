// com.example.appodp.viewmodel.ActiveRegistrationViewModel.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.data.model.ActiveRegistrationRequest
import com.example.appodp.data.repository.ActiveRegistrationsRepository
import com.example.appodp.util.NetworkUtils // Pretpostavljam da imate ovu klasu
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActiveRegistrationViewModel(
    private val application: Application,
    private val repository: ActiveRegistrationsRepository
) : ViewModel() {

    private val _registrations = MutableStateFlow<List<ActiveRegistration>>(emptyList())
    val registrations: StateFlow<List<ActiveRegistration>> = _registrations.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _clientSearchText = MutableStateFlow<String?>(null)
    val clientSearchText: StateFlow<String?> = _clientSearchText.asStateFlow()

    private val _clientSortByTotalAscending = MutableStateFlow(false)
    val clientSortByTotalAscending: StateFlow<Boolean> = _clientSortByTotalAscending.asStateFlow()

    private val _filteredAndSortedRegistrations = MutableStateFlow<List<ActiveRegistration>>(emptyList())
    val filteredAndSortedRegistrations: StateFlow<List<ActiveRegistration>> = _filteredAndSortedRegistrations.asStateFlow()

    init {
        _clientSearchText.value = null // Resetuj filter na početku

        // Učitavanje keširanih podataka i ažuriranje _registrations
        repository.getCachedActiveRegistrations()
            .onEach { cachedList ->
                _registrations.value = cachedList
            }
            .launchIn(viewModelScope)

        // Kombinacija za filtriranje i sortiranje (ostaje isto)
        combine(
            _registrations,
            _clientSearchText.debounce(300L),
            _clientSortByTotalAscending
        ) { regs, searchText, sortByTotalAscending ->
            performClientSideFilteringAndSorting(regs, searchText, sortByTotalAscending)
        }
            .onEach { filteredList ->
                _filteredAndSortedRegistrations.value = filteredList
            }
            .launchIn(viewModelScope)

        // Odmah pokušaj dohvaćanja podataka sa mreže pri inicijalizaciji
        fetchDataFromNetwork()
    }

    fun updateClientSearchText(text: String) {
        _clientSearchText.value = if (text.isBlank()) null else text.trim()
    }

    fun toggleSortByTotal() {
        _clientSortByTotalAscending.value = !_clientSortByTotalAscending.value
    }

    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null // Resetuj grešku prije pokušaja dohvaćanja

        if (NetworkUtils.isConnectedToInternet(application)) {
            val request = ActiveRegistrationRequest(
                updateDate = "2025-07-03",
                entityId = 0,
                cantonId = 0,
                municipalityId = 0
            )

            viewModelScope.launch { // Koristite viewModelScope za pokretanje korutine
                repository.fetchAndCacheRegistrations(
                    request = request,
                    scope = viewModelScope,
                    onSuccess = {
                        _isLoading.value = false
                        _error.value = null // Uspješno dohvaćeno, nema greške
                    },
                    onError = { err ->
                        _error.value = err // Postavi mrežnu grešku
                        _isLoading.value = false
                    }
                )
            }
        } else {
            // Ako nema interneta, prikaži poruku, ali ne poništavaj postojeće podatke
            _error.value = "Nema internetske veze. Prikazuju se keširani podaci."
            _isLoading.value = false
            // Podaci ostaju u _registrations flow-u iz init bloka
        }
    }

    private fun performClientSideFilteringAndSorting(
        data: List<ActiveRegistration>,
        searchText: String?,
        sortByTotalAscending: Boolean
    ): List<ActiveRegistration> {
        var filteredList = data

        if (!searchText.isNullOrBlank()) {
            filteredList = filteredList.filter {
                it.registrationPlace.contains(searchText, ignoreCase = true)
            }
        }

        return if (sortByTotalAscending) {
            filteredList.sortedBy { it.total }
        } else {
            filteredList.sortedByDescending { it.total }
        }
    }
}
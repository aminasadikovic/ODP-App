// com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModel.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.VehicleRegistrationRequestRequest
import com.example.appodp.data.model.VehicleRegistrationRequestResponse
import com.example.appodp.data.repository.VehicleRegistrationRequestsRepository
import com.example.appodp.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class VehicleRegistrationRequestsViewModel(
    private val application: Application,
    private val repository: VehicleRegistrationRequestsRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<VehicleRegistrationRequestResponse>>(emptyList())
    // _requests će uvijek pratiti keširane podatke iz Room baze

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedEntityId = MutableStateFlow<Int?>(0)
    val selectedEntityId: StateFlow<Int?> = _selectedEntityId.asStateFlow()

    // _clientSearchText je UKLONJEN

    private val _clientSortOption = MutableStateFlow<String>("Bez sortiranja")
    val clientSortOption: StateFlow<String> = _clientSortOption.asStateFlow()

    private val _filteredAndSortedRequests = MutableStateFlow<List<VehicleRegistrationRequestResponse>>(emptyList())
    val filteredAndSortedRequests: StateFlow<List<VehicleRegistrationRequestResponse>> = _filteredAndSortedRequests.asStateFlow()


    init {
        // 1. Pratite keširane podatke iz Room baze
        // Kada se Room baza promijeni (nakon mrežnog poziva), _requests se automatski ažurira.
        repository.getCachedVehicleRegistrationRequests()
            .onEach { cachedList ->
                _requests.value = cachedList
            }
            .launchIn(viewModelScope)

        // 2. Kombinirajte tokove za mrežni poziv
        // Ovaj combine će se pokrenuti kada se promijeni _selectedEntityId,
        // što će pokrenuti fetchDataFromNetwork() za osvježavanje keša s mreže.
        // Ovi filteri (entityId) se šalju serveru.
        combine(
            _selectedEntityId
        ) { _ ->
            fetchDataFromNetwork()
        }
            .launchIn(viewModelScope)

        // 3. Kombinirajte tokove za klijentsko filtriranje i sortiranje
        // Ova kombinacija se aktivira kada se promijene _requests (iz Room-a)
        // ili opcije za sortiranje.
        // Ovdje se dešava OFFLINE filtriranje po entitetu i sortiranje.
        combine(
            _requests,           // Svi podaci iz Room-a
            _selectedEntityId,   // Odabrani entitet za OFFLINE filtriranje
            _clientSortOption    // Opcija sortiranja
        ) { requests, entityId, sortOption ->
            performClientSideFilteringAndSorting(requests, entityId, sortOption)
        }
            .onEach { filteredList ->
                _filteredAndSortedRequests.value = filteredList
            }
            .launchIn(viewModelScope)

        // Odmah pokušaj dohvaćanja podataka s mreže pri inicijalizaciji
        // Ovo osigurava da pri pokretanju odmah pokušamo dobiti najnovije podatke.
        fetchDataFromNetwork()
    }

    fun updateSelectedEntityId(entityId: Int?) {
        _selectedEntityId.value = entityId
    }

    // updateClientSearchText funkcija je UKLONJENA
    // fun updateClientSearchText(text: String) { ... }

    fun updateClientSortOption(option: String) {
        _clientSortOption.value = option
    }

    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null

        if (NetworkUtils.isConnectedToInternet(application)) {
            val currentEntityId = _selectedEntityId.value ?: 0

            val request = VehicleRegistrationRequestRequest(
                updateDate = "2025-07-03", // Primjer, možete razmisliti o dinamičkim vrijednostima ili ukloniti ako API podržava null
                entityId = currentEntityId,
                cantonId = null, // Postavljeno na null jer se ne koristi
                municipalityId = null, // Postavljeno na null jer se ne koristi
                year = null, // Postavljeno na null jer se ne koristi
                month = null // Postavljeno na null jer se ne koristi
            )

            viewModelScope.launch {
                repository.fetchAndCacheRequests(
                    request = request,
                    scope = viewModelScope,
                    onSuccess = { _ ->
                        _isLoading.value = false
                        _error.value = null // Uspješno dohvaćeno, resetuj grešku
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
            // Keširani podaci se automatski prikazuju jer _requests Flow prati Room bazu
        }
    }

    // PROMIJENJENO: Uklonjena je searchText, dodan entityId za klijentsko filtriranje
    private fun performClientSideFilteringAndSorting(
        data: List<VehicleRegistrationRequestResponse>,
        entityId: Int?, // Sada se entityId koristi za OFFLINE filtriranje
        sortOption: String
    ): List<VehicleRegistrationRequestResponse> {
        var filteredList = data

        // Klijentsko filtriranje po entitetu
        if (entityId != null && entityId != 0) { // Ako nije "Svi" (ID 0)
            // BITNO: Ovdje pretpostavljamo da VehicleRegistrationRequestResponse
            // IMA polje 'entityId' ili da se 'registrationPlace' može mapirati na entitet ID.
            // Ako VehicleRegistrationRequestResponse NEMA 'entityId' polje,
            // onda offline filtriranje PO ENTITETU NEĆE RADITI bez izmjene modela.
            // Za sada, ostavljam kako bi se vidjela namjera.
            // Ako `registrationPlace` sadrži naziv entiteta:
            filteredList = filteredList.filter { response ->
                when (entityId) {
                    1 -> response.registrationPlace.contains("FBIH", ignoreCase = true)
                    2 -> response.registrationPlace.contains("RS", ignoreCase = true)
                    3 -> response.registrationPlace.contains("BD", ignoreCase = true)
                    else -> true // Svi ili nepoznato
                }
            }
            // ILI, ako vaš VehicleRegistrationRequestResponse model ima 'entityId' polje:
            // filteredList = filteredList.filter { it.entityId == entityId }
        }

        // Klijentsko sortiranje
        filteredList = when (sortOption) {
            "Po ukupnom broju (opadajuće)" -> filteredList.sortedByDescending { it.permanentRegistration + it.firstTimeRequestsTotal + it.renewalRequestsTotal + it.ownershipChangesTotal + it.deregisteredTotal }
            "Po ukupnom broju (rastuće)" -> filteredList.sortedBy { it.permanentRegistration + it.firstTimeRequestsTotal + it.renewalRequestsTotal + it.ownershipChangesTotal + it.deregisteredTotal }
            else -> filteredList // Bez sortiranja
        }

        return filteredList
    }
}
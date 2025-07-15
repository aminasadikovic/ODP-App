package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.data.model.ActiveRegistrationRequest
import com.example.appodp.data.repository.ActiveRegistrationsRepository
import com.example.appodp.util.NetworkUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActiveRegistrationViewModel(
    private val application: Application,
    private val repository: ActiveRegistrationsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _registrations = MutableStateFlow<List<ActiveRegistration>>(emptyList())
    val registrations: StateFlow<List<ActiveRegistration>> = _registrations.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _clientSearchText = savedStateHandle.getStateFlow("clientSearchText", "")
    val clientSearchText: StateFlow<String> = _clientSearchText

    private val _clientSortByTotalAscending = savedStateHandle.getStateFlow("clientSortByTotalAscending", false)
    val clientSortByTotalAscending: StateFlow<Boolean> = _clientSortByTotalAscending

    private val _filteredAndSortedRegistrations = MutableStateFlow<List<ActiveRegistration>>(emptyList())
    val filteredAndSortedRegistrations: StateFlow<List<ActiveRegistration>> = _filteredAndSortedRegistrations.asStateFlow()

    private val _registrationsByEntity = MutableStateFlow<Map<String, Int>>(emptyMap())
    val registrationsByEntity: StateFlow<Map<String, Int>> = _registrationsByEntity.asStateFlow()

    init {
        repository.getCachedActiveRegistrations()
            .onEach { cachedList ->
                _registrations.value = cachedList
            }
            .launchIn(viewModelScope)

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

        _registrations
            .onEach { regs ->
                _registrationsByEntity.value = groupRegistrationsByEntity(regs)
            }
            .launchIn(viewModelScope)

        fetchDataFromNetwork()
    }

    fun updateClientSearchText(text: String) {
        savedStateHandle["clientSearchText"] = text.trim()
    }

    fun toggleSortByTotal() {
        savedStateHandle["clientSortByTotalAscending"] = !(_clientSortByTotalAscending.value)
    }

    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null

        if (NetworkUtils.isConnectedToInternet(application)) {
            val request = ActiveRegistrationRequest(
                updateDate = "2025-07-03",
                entityId = 0,
                cantonId = 0,
                municipalityId = 0
            )

            viewModelScope.launch {
                repository.fetchAndCacheRegistrations(
                    request = request,
                    scope = viewModelScope,
                    onSuccess = {
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

    private fun performClientSideFilteringAndSorting(
        data: List<ActiveRegistration>,
        searchText: String,
        sortByTotalAscending: Boolean
    ): List<ActiveRegistration> {
        var filteredList = data

        if (searchText.isNotBlank()) {
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

    private fun groupRegistrationsByEntity(registrations: List<ActiveRegistration>): Map<String, Int> {
        val entityTotals = mutableMapOf<String, Int>()
        val fbihPlaces = listOf("Sarajevo", "Mostar", "Tuzla", "Zenica", "Bihać", "Travnik", "Orašje", "Goražde", "Livno", "Široki Brijeg", "Posušje", "Grude", "Konjic", "Jablanica", "Prozor-Rama", "Čapljina", "Stolac", "Neum", "Kiseljak", "Vitez", "Novi Travnik", "Bugojno", "Donji Vakuf", "Jajce", "Busovača", "Kreševo", "Fojnica", "Gornji Vakuf-Uskoplje", "Dobretići", "Kakanj", "Maglaj", "Žepče", "Zavidovići", "Olovo", "Vareš", "Breza", "Visoko", "Doboj Jug", "Usora", "Tešanj", "Kladanj", "Banovići", "Živinice", "Srebrenik", "Gračanica", "Gradačac", "Lukavac", "Kalesija", "Sapna", "Čelić", "Doboj Istok", "Odžak", "Domaljevac-Šamac", "Bosanski Petrovac", "Bosansko Grahovo", "Drvar", "Glamoč", "Kupres", "Tomislavgrad")
        val rsPlaces = listOf("Banja Luka", "Bijeljina", "Prijedor", "Doboj", "Trebinje", "Istočno Sarajevo", "Zvornik", "Gradiška", "Teslić", "Kozarska Dubica", "Mrkonjić Grad", "Foča", "Višegrad", "Pale", "Sokolac", "Modriča", "Derventa", "Laktaši", "Prnjavor", "Šamac", "Brod", "Han Pijesak", "Čajniče", "Nevesinje", "Gacko", "Berkovići", "Ljubinje", "Kalinovik", "Rogatica", "Milići", "Vlasenica", "Srebrenica", "Bratunac", "Kotor Varoš", "Šipovo", "Ribnik", "Jezero", "Krupa na Uni", "Novi Grad", "Kostajnica", "Oštra Luka", "Petrovac", "Donji Žabar", "Pelagićevo", "Vukosavlje", "Stanari", "Osmaci", "Kneževo", "Čelinac", "Trnovo (RS)", "Istočni Stari Grad", "Istočna Ilidža", "Istočno Novo Sarajevo", "Istočni Drvar", "Kupres (RS)", "Novo Goražde", "Petrovo", "Rudo", "Višegrad", "Zvornik")
        val bdPlaces = listOf("Brčko")

        registrations.forEach { reg ->
            val place = reg.registrationPlace
            val entity = when {
                fbihPlaces.any { place.contains(it, ignoreCase = true) } -> "FBiH"
                rsPlaces.any { place.contains(it, ignoreCase = true) } -> "RS"
                bdPlaces.any { place.contains(it, ignoreCase = true) } -> "BD"
                else -> null
            }
            if (entity != null) {
                entityTotals[entity] = entityTotals.getOrDefault(entity, 0) + reg.total
            }
        }
        return entityTotals
    }
}

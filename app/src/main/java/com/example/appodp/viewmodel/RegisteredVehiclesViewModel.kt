package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _allVehicles = MutableStateFlow<List<RegisteredVehicleEntity>>(emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedEntityId = savedStateHandle.getStateFlow("selectedEntityId", 0)
    val selectedEntityId: StateFlow<Int> = _selectedEntityId

    private val _sortByTotalAscending = savedStateHandle.getStateFlow("sortByTotalAscending", false)
    val sortByTotalAscending: StateFlow<Boolean> = _sortByTotalAscending

    private val _filteredAndSortedVehicles = MutableStateFlow<List<RegisteredVehicleUiItem>>(emptyList())
    val filteredAndSortedVehicles: StateFlow<List<RegisteredVehicleUiItem>> = _filteredAndSortedVehicles.asStateFlow()

    init {
        repository.getCachedRegisteredVehicles()
            .onEach { cachedEntities ->
                _allVehicles.value = cachedEntities
            }
            .launchIn(viewModelScope)

        combine(
            _allVehicles,
            _sortByTotalAscending
        ) { allVehiclesEntities, sortByTotalAscending ->
            performClientSideSorting(allVehiclesEntities, sortByTotalAscending)
        }
            .onEach { sortedUiItemsList ->
                _filteredAndSortedVehicles.value = sortedUiItemsList
            }
            .launchIn(viewModelScope)

        _selectedEntityId
            .onEach {
                fetchDataFromNetwork()
            }
            .launchIn(viewModelScope)

        fetchDataFromNetwork()
    }


    fun updateSelectedEntityId(entityId: Int) {
        savedStateHandle["selectedEntityId"] = entityId
    }

    fun toggleSortByTotal() {
        savedStateHandle["sortByTotalAscending"] = !(_sortByTotalAscending.value)
    }

    fun toggleFavoriteStatus(uiItem: RegisteredVehicleUiItem) {
        viewModelScope.launch {
            repository.toggleFavoriteStatus(uiItem.entityId, uiItem.isFavorite)
        }
    }

    fun fetchDataFromNetwork() {
        _isLoading.value = true
        _error.value = null

        if (NetworkUtils.isConnectedToInternet(application)) {
            val request = RegisteredVehicleRequest(
                updateDate = "2025-07-03",
                entityId = _selectedEntityId.value,
                cantonId = null,
                municipalityId = null,
                year = null,
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
        sortByTotalAscending: Boolean
    ): List<RegisteredVehicleUiItem> {
        val sortedList = if (sortByTotalAscending) {
            data.sortedBy { it.total }
        } else {
            data.sortedByDescending { it.total }
        }

        return sortedList.map { entity ->
            RegisteredVehicleUiItem(
                vehicle = entity.toDomain(),
                isFavorite = entity.isFavorite,
                entityId = entity.id
            )
        }
    }
}

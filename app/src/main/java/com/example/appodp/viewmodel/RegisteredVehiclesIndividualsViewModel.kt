package com.example.appodp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.RegisteredVehicleIndividual
import com.example.appodp.data.model.RegisteredVehicleIndividualRequest
import com.example.appodp.data.repository.RegisteredVehiclesIndividualsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisteredVehiclesIndividualsViewModel : ViewModel() {

    private val repository = RegisteredVehiclesIndividualsRepository()

    private val _vehicles = MutableStateFlow<List<RegisteredVehicleIndividual>>(emptyList())
    val vehicles: StateFlow<List<RegisteredVehicleIndividual>> = _vehicles

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadVehicles() {
        val request = RegisteredVehicleIndividualRequest(
            updateDate = "2025-07-03",
            entityId = 0,
            cantonId = 0,
            municipalityId = 0,
            year = "",
            month = ""
        )

        viewModelScope.launch {
            repository.fetchRegisteredVehiclesIndividuals(
                request = request,
                onSuccess = { result -> _vehicles.value = result },
                onError = { err -> _error.value = err }
            )
        }
    }
}

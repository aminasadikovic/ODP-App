package com.example.appodp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.RegisteredVehicle
import com.example.appodp.data.model.RegisteredVehicleRequest
import com.example.appodp.data.repository.RegisteredVehiclesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisteredVehiclesViewModel : ViewModel() {

    private val repository = RegisteredVehiclesRepository()

    private val _vehicles = MutableStateFlow<List<RegisteredVehicle>>(emptyList())
    val vehicles: StateFlow<List<RegisteredVehicle>> = _vehicles

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadVehicles() {
        val request = RegisteredVehicleRequest(
            updateDate = "2023-06-29",
            entityId = 0,
            cantonId = 0,
            municipalityId = 0,
            year = "",
            month = ""
        )

        viewModelScope.launch {
            repository.fetchRegisteredVehicles(
                request = request,
                onSuccess = { _vehicles.value = it },
                onError = { _error.value = it }
            )
        }
    }
}

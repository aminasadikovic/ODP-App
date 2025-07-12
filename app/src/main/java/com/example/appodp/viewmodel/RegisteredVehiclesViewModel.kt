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

    fun loadVehicles(entityId: Int, year: Int?, month: Int?) {
        // Konvertuj Int? u String, ili prazno ako null
        val yearString = year?.toString() ?: ""
        // Mjesec sa vodećim nulama (01, 02...) ako je definisan
        val monthString = month?.toString()?.padStart(2, '0') ?: ""

        val request = RegisteredVehicleRequest(
            updateDate = "",
            entityId = entityId,
            cantonId = 0,
            municipalityId = 0,
            year = yearString,
            month = monthString
        )

        viewModelScope.launch {
            repository.fetchRegisteredVehicles(
                request = request,
                onSuccess = { result -> _vehicles.value = result },
                onError = { err -> _error.value = err }
            )
        }
    }
}

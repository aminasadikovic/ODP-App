package com.example.appodp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.VehicleRegistrationRequestRequest
import com.example.appodp.data.model.VehicleRegistrationRequestResponse
import com.example.appodp.data.repository.VehicleRegistrationRequestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehicleRegistrationRequestsViewModel : ViewModel() {

    private val repository = VehicleRegistrationRequestsRepository()

    private val _requests = MutableStateFlow<List<VehicleRegistrationRequestResponse>>(emptyList())
    val requests: StateFlow<List<VehicleRegistrationRequestResponse>> = _requests

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadRequests() {
        val request = VehicleRegistrationRequestRequest(
            updateDate = "2025-07-03",
            entityId = 0,
            cantonId = 0,
            municipalityId = 0,
            year = "",
            month = ""
        )

        viewModelScope.launch {
            repository.fetchRequests(
                request = request,
                onSuccess = { _requests.value = it },
                onError = { _error.value = it }
            )
        }
    }
}

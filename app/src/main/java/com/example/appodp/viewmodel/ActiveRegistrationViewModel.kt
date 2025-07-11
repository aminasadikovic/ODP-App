package com.example.appodp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.data.model.ActiveRegistrationRequest
import com.example.appodp.data.repository.ActiveRegistrationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActiveRegistrationViewModel : ViewModel() {

    private val repository = ActiveRegistrationsRepository()

    private val _registrations = MutableStateFlow<List<ActiveRegistration>>(emptyList())
    val registrations: StateFlow<List<ActiveRegistration>> = _registrations

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadRegistrations() {
        val request = ActiveRegistrationRequest(
            updateDate = "2025-04-07",
            entityId = 0,
            cantonId = 0,
            municipalityId = 0
        )

        viewModelScope.launch {
            repository.fetchRegistrations(
                request = request,
                onSuccess = { result -> _registrations.value = result },
                onError = { err -> _error.value = err }
            )
        }
    }
}

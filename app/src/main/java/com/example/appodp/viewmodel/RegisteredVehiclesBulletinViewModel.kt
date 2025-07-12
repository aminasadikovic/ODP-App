package com.example.appodp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appodp.data.model.RegisteredVehiclesBulletinRequest
import com.example.appodp.data.model.RegisteredVehiclesBulletinResponse
import com.example.appodp.data.repository.RegisteredVehiclesBulletinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisteredVehiclesBulletinViewModel : ViewModel() {

    private val repository = RegisteredVehiclesBulletinRepository()

    private val _bulletins = MutableStateFlow<List<RegisteredVehiclesBulletinResponse>>(emptyList())
    val bulletins: StateFlow<List<RegisteredVehiclesBulletinResponse>> = _bulletins

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun loadBulletins() {
        val request = RegisteredVehiclesBulletinRequest(
            updateDate = "2025-07-03",
            entityId = 0,
            cantonId = 0,
            municipalityId = 0,
            year = "",
            month = "",
            registrationRequestTypeId = "",
            vehicleOwnershipId = "",
            vehicleTypeId = "",
            vehicleBrandId = "",
            productionYearFrom = "",
            vehicleFuelTypeId = "",
            vehicleInsuranceId = "",
            ageStructureId = "",
            vehicleEcoCharacteristicsId = "",
            vehicleKindId= "",
            vehicleColorId= "",
            genderId= "",
            citizen= "",
        )

        viewModelScope.launch {
            repository.fetchBulletin(
                request = request,
                onSuccess = { result -> _bulletins.value = result },
                onError = { err -> _error.value = err }
            )
        }
    }
}

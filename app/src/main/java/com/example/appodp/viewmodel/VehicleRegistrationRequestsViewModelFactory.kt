// com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModelFactory.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras // Import CreationExtras
import androidx.lifecycle.createSavedStateHandle // Import createSavedStateHandle
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.VehicleRegistrationRequestsRepository

class VehicleRegistrationRequestsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T { // Dodan extras
        if (modelClass.isAssignableFrom(VehicleRegistrationRequestsViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val dao = database.vehicleRegistrationRequestDao()
            val repository = VehicleRegistrationRequestsRepository(dao)
            val savedStateHandle = extras.createSavedStateHandle() // Dohvati SavedStateHandle
            @Suppress("UNCHECKED_CAST")
            return VehicleRegistrationRequestsViewModel(application, repository, savedStateHandle) as T // Proslijedi SavedStateHandle
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

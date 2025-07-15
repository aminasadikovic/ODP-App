package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.VehicleRegistrationRequestsRepository

class VehicleRegistrationRequestsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(VehicleRegistrationRequestsViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val dao = database.vehicleRegistrationRequestDao()
            val repository = VehicleRegistrationRequestsRepository(dao)
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return VehicleRegistrationRequestsViewModel(application, repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

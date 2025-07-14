// com.example.appodp.viewmodel.VehicleRegistrationRequestsViewModelFactory.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.VehicleRegistrationRequestsRepository

class VehicleRegistrationRequestsViewModelFactory(
    private val application: Application // Prima Application kontekst
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleRegistrationRequestsViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val dao = database.vehicleRegistrationRequestDao() // Dohvati DAO
            val repository = VehicleRegistrationRequestsRepository(dao) // Kreiraj repozitorij
            @Suppress("UNCHECKED_CAST")
            return VehicleRegistrationRequestsViewModel(application, repository) as T // Proslijedi Application i Repozitorij
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
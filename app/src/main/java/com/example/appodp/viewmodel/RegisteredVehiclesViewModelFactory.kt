// com.example.appodp.viewmodel.RegisteredVehiclesViewModelFactory.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.RegisteredVehiclesRepository

class RegisteredVehiclesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisteredVehiclesViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val registeredVehicleDao = database.registeredVehicleDao()
            val repository = RegisteredVehiclesRepository(registeredVehicleDao)
            @Suppress("UNCHECKED_CAST")
            return RegisteredVehiclesViewModel(application, repository) as T // PROSJEĐUJEMO application
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
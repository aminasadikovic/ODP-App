package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.RegisteredVehiclesRepository

class RegisteredVehiclesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(RegisteredVehiclesViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val registeredVehicleDao = database.registeredVehicleDao()
            val repository = RegisteredVehiclesRepository(registeredVehicleDao)
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return RegisteredVehiclesViewModel(application, repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

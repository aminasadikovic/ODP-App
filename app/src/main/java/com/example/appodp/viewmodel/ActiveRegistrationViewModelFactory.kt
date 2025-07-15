// com.example.appodp.viewmodel.ActiveRegistrationViewModelFactory.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras // Import CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel // Potrebno za createSavedStateHandle
import androidx.lifecycle.createSavedStateHandle // Potrebno za createSavedStateHandle
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.ActiveRegistrationsRepository

class ActiveRegistrationViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T { // Dodan extras
        if (modelClass.isAssignableFrom(ActiveRegistrationViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val registrationDao = database.registrationDao()
            val repository = ActiveRegistrationsRepository(registrationDao)
            val savedStateHandle = extras.createSavedStateHandle() // Dohvati SavedStateHandle
            @Suppress("UNCHECKED_CAST")
            return ActiveRegistrationViewModel(application, repository, savedStateHandle) as T // Proslijedi SavedStateHandle
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

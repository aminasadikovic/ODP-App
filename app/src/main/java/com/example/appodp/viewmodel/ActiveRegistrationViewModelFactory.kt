// com.example.appodp.viewmodel.ActiveRegistrationViewModelFactory.kt
package com.example.appodp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appodp.data.local.DatabaseProvider
import com.example.appodp.data.repository.ActiveRegistrationsRepository

class ActiveRegistrationViewModelFactory(
    private val application: Application // Prima Application kontekst
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActiveRegistrationViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(application)
            val registrationDao = database.registrationDao()
            val repository = ActiveRegistrationsRepository(registrationDao)
            @Suppress("UNCHECKED_CAST")
            return ActiveRegistrationViewModel(application, repository) as T // PROSJEĐUJEMO application
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
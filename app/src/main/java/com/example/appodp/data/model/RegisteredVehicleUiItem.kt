package com.example.appodp.data.model

import com.example.appodp.data.model.RegisteredVehicle

data class RegisteredVehicleUiItem(
    val vehicle: RegisteredVehicle, // Vaš originalni domain model
    val isFavorite: Boolean,        // Status favorita za UI prikaz
    val entityId: Int               // ID iz Room entiteta, trebat će nam za toggle
)
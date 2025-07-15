package com.example.appodp.data.model
import com.example.appodp.data.model.RegisteredVehicle

data class RegisteredVehicleUiItem(
    val vehicle: RegisteredVehicle,
    val isFavorite: Boolean,
    val entityId: Int
)
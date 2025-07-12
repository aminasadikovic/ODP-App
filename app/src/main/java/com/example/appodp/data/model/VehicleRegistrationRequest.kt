package com.example.appodp.data.model

data class VehicleRegistrationRequestRequest(
    val updateDate: String,
    val entityId: Int,
    val cantonId: Int,
    val municipalityId: Int,
    val year: String,
    val month: String
)

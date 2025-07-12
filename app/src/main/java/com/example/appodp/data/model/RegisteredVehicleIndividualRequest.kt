package com.example.appodp.data.model

data class RegisteredVehicleIndividualRequest(
    val updateDate: String,
    val entityId: Int,
    val cantonId: Int,
    val municipalityId: Int,
    val year: String,
    val month: String
)
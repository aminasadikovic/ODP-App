package com.example.appodp.data.model

data class RegisteredVehicleRequest(
    val updateDate: String? = null,
    val entityId: Int? = null,
    val cantonId: Int? = null,
    val municipalityId: Int? = null,
    val year: String? = null,
    val month: String? = null
)
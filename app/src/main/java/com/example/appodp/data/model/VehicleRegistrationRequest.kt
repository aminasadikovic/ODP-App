package com.example.appodp.data.model

data class VehicleRegistrationRequestRequest(
    val updateDate: String? = null,
    val entityId: Int? = null,
    val cantonId: Int?,
    val municipalityId: Int?,
    val year: String?,
    val month: String?
)
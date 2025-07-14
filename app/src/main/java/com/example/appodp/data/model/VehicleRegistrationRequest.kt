package com.example.appodp.data.model

data class VehicleRegistrationRequestRequest(
    val updateDate: String? = null,
    val entityId: Int? = null,
    val cantonId: Int?, // Dodan '?' za nullable Int
    val municipalityId: Int?, // Dodan '?' za nullable Int
    val year: String?, // Dodan '?' za nullable String
    val month: String? // Dodan '?' za nullable String
)
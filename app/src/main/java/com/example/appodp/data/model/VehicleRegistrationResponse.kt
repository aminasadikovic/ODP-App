package com.example.appodp.data.model

data class VehicleRegistrationRequestResponse(
    val registrationPlace: String,
    val permanentRegistration: Int,
    val firstTimeRequestsTotal: Int,
    val renewalRequestsTotal: Int,
    val ownershipChangesTotal: Int,
    val deregisteredTotal: Int
)

package com.example.appodp.data.model

data class RegisteredVehiclesBulletinRequest(
    val updateDate: String,
    val entityId: Int,
    val cantonId: Int,
    val municipalityId: Int,
    val year: String,
    val month: String,
    val registrationRequestTypeId: String,
    val vehicleOwnershipId: String,
    val vehicleTypeId: String,
    val vehicleBrandId: String,
    val productionYearFrom: String,
    val vehicleFuelTypeId: String,
    val vehicleInsuranceId: String,
    val ageStructureId: String,
    val vehicleEcoCharacteristicsId: String,
    val vehicleKindId: String,
    val vehicleColorId: String,
    val genderId: String,
    val citizen: String
)

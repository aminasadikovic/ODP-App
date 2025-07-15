package com.example.appodp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appodp.data.model.VehicleRegistrationRequestResponse // Importirajte vaš model podataka

@Entity(tableName = "vehicle_registration_requests")
data class VehicleRegistrationRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registrationPlace: String,
    val permanentRegistration: Int,
    val firstTimeRequestsTotal: Int,
    val renewalRequestsTotal: Int,
    val ownershipChangesTotal: Int,
    val deregisteredTotal: Int
)

fun VehicleRegistrationRequestEntity.toDomain(): VehicleRegistrationRequestResponse {
    return VehicleRegistrationRequestResponse(
        registrationPlace = this.registrationPlace,
        permanentRegistration = this.permanentRegistration,
        firstTimeRequestsTotal = this.firstTimeRequestsTotal,
        renewalRequestsTotal = this.renewalRequestsTotal,
        ownershipChangesTotal = this.ownershipChangesTotal,
        deregisteredTotal = this.deregisteredTotal
    )
}

fun VehicleRegistrationRequestResponse.toEntity(): VehicleRegistrationRequestEntity {
    return VehicleRegistrationRequestEntity(
        registrationPlace = this.registrationPlace,
        permanentRegistration = this.permanentRegistration,
        firstTimeRequestsTotal = this.firstTimeRequestsTotal,
        renewalRequestsTotal = this.renewalRequestsTotal,
        ownershipChangesTotal = this.ownershipChangesTotal,
        deregisteredTotal = this.deregisteredTotal
    )
}
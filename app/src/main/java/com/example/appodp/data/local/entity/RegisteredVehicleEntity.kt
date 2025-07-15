// com.example.appodp.data.local.entity.RegisteredVehicleEntity.kt
package com.example.appodp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appodp.data.model.RegisteredVehicle

@Entity(tableName = "registered_vehicles")
data class RegisteredVehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registrationPlace: String,
    val totalDomestic: Int,
    val totalForeign: Int,
    val total: Int,
    val isFavorite: Boolean = false
)

fun RegisteredVehicleEntity.toDomain(): RegisteredVehicle {
    return RegisteredVehicle(
        registrationPlace = this.registrationPlace,
        totalDomestic = this.totalDomestic,
        totalForeign = this.totalForeign,
        total = this.total
    )
}

fun RegisteredVehicle.toEntity(id: Int = 0, isFavorite: Boolean = false): RegisteredVehicleEntity {
    return RegisteredVehicleEntity(
        id = id,
        registrationPlace = this.registrationPlace,
        totalDomestic = this.totalDomestic,
        totalForeign = this.totalForeign,
        total = this.total,
        isFavorite = isFavorite
    )
}
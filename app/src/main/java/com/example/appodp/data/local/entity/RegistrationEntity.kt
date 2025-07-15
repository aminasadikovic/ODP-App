package com.example.appodp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appodp.data.model.ActiveRegistration // Vaš ActiveRegistration model

@Entity(tableName = "registrations")
data class RegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registrationPlace: String,
    val total: Int
)

fun RegistrationEntity.toDomain(): ActiveRegistration {
    return ActiveRegistration(
        registrationPlace = registrationPlace,
        total = total
    )
}

fun ActiveRegistration.toEntity(): RegistrationEntity {
    return RegistrationEntity(
        registrationPlace = this.registrationPlace,
        total = this.total
    )
}
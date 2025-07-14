package com.example.appodp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appodp.data.local.dao.RegistrationDao
import com.example.appodp.data.local.dao.RegisteredVehicleDao
import com.example.appodp.data.local.dao.VehicleRegistrationRequestDao // NOVO: Importujte DAO za zahtjeve
import com.example.appodp.data.local.entity.RegistrationEntity
import com.example.appodp.data.local.entity.RegisteredVehicleEntity
import com.example.appodp.data.local.entity.VehicleRegistrationRequestEntity // NOVO: Importujte entitet za zahtjeve

@Database(
    entities = [
        RegisteredVehicleEntity::class,
        RegistrationEntity::class,
        VehicleRegistrationRequestEntity::class,
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registeredVehicleDao(): RegisteredVehicleDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun vehicleRegistrationRequestDao(): VehicleRegistrationRequestDao
}
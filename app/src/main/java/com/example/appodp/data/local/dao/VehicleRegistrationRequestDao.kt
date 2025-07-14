// com.example.appodp.data.local.dao.VehicleRegistrationRequestDao.kt

package com.example.appodp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.appodp.data.local.entity.VehicleRegistrationRequestEntity

@Dao
interface VehicleRegistrationRequestDao {
    // Dohvati sve zahtjeve iz baze, vraća Flow za reaktivne promjene
    @Query("SELECT * FROM vehicle_registration_requests")
    fun getAllVehicleRegistrationRequests(): Flow<List<VehicleRegistrationRequestEntity>>

    // Umetni listu zahtjeva. Ako postoji konflikt, zamijeni postojeće.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<VehicleRegistrationRequestEntity>)

    // Obriši sve zahtjeve iz baze. Korisno za osvježavanje keša.
    @Query("DELETE FROM vehicle_registration_requests")
    suspend fun deleteAllVehicleRegistrationRequests()
}
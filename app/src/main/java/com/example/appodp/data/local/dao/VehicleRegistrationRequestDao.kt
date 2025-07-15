package com.example.appodp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.appodp.data.local.entity.VehicleRegistrationRequestEntity

@Dao
interface VehicleRegistrationRequestDao {
    @Query("SELECT * FROM vehicle_registration_requests")
    fun getAllVehicleRegistrationRequests(): Flow<List<VehicleRegistrationRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<VehicleRegistrationRequestEntity>)

    @Query("DELETE FROM vehicle_registration_requests")
    suspend fun deleteAllVehicleRegistrationRequests()
}
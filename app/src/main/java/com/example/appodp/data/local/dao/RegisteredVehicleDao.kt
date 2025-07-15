package com.example.appodp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appodp.data.local.entity.RegisteredVehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegisteredVehicleDao {
    @Query("SELECT * FROM registered_vehicles")
    fun getAllRegisteredVehicles(): Flow<List<RegisteredVehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vehicles: List<RegisteredVehicleEntity>)

    @Query("DELETE FROM registered_vehicles")
    suspend fun deleteAllRegisteredVehicles()

    @Update
    suspend fun updateRegisteredVehicle(vehicle: RegisteredVehicleEntity)

    @Query("SELECT * FROM registered_vehicles WHERE isFavorite = 1")
    fun getAllFavoriteRegisteredVehicles(): Flow<List<RegisteredVehicleEntity>>

    @Query("SELECT * FROM registered_vehicles WHERE registrationPlace = :registrationPlace AND totalDomestic = :totalDomestic AND totalForeign = :totalForeign AND total = :total LIMIT 1")
    suspend fun getRegisteredVehicleByFields(
        registrationPlace: String,
        totalDomestic: Int,
        totalForeign: Int,
        total: Int
    ): RegisteredVehicleEntity?
}
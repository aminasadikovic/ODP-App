// com.example.appodp.data.local.dao.RegisteredVehicleDao.kt
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

    // Ova metoda je sada manje kritična za glavni ekran jer se isFavorite status dohvaća sa svim vozilima,
    // ali može biti korisna za zaseban ekran "Moji Favoriti" ako ga ikada implementirate.
    @Query("SELECT * FROM registered_vehicles WHERE isFavorite = 1")
    fun getAllFavoriteRegisteredVehicles(): Flow<List<RegisteredVehicleEntity>>

    // Važno: ova metoda sada vraća entitet koji sadrži i ID i isFavorite status.
    @Query("SELECT * FROM registered_vehicles WHERE registrationPlace = :registrationPlace AND totalDomestic = :totalDomestic AND totalForeign = :totalForeign AND total = :total LIMIT 1")
    suspend fun getRegisteredVehicleByFields(
        registrationPlace: String,
        totalDomestic: Int,
        totalForeign: Int,
        total: Int
    ): RegisteredVehicleEntity?
}
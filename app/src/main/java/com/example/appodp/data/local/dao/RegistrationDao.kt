// com.example.appodp.data.local.dao.RegistrationDao.kt
package com.example.appodp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appodp.data.local.entity.RegistrationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Query("SELECT * FROM registrations")
    fun getAllRegistrations(): Flow<List<RegistrationEntity>> // Koristite Flow za reaktivno čitanje

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(registrations: List<RegistrationEntity>)

    @Query("DELETE FROM registrations")
    suspend fun deleteAllRegistrations() // Preimenovano za jasnoću
}
// com.example.appodp.data.repository.RegisteredVehiclesRepository.kt
package com.example.appodp.data.repository

import com.example.appodp.data.model.ApiResponse
import com.example.appodp.data.model.RegisteredVehicle
import com.example.appodp.data.model.RegisteredVehicleRequest
import com.example.appodp.data.api.ApiService
import com.example.appodp.data.api.RetrofitInstance
import com.example.appodp.data.local.dao.RegisteredVehicleDao
import com.example.appodp.data.local.entity.RegisteredVehicleEntity
import com.example.appodp.data.local.entity.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RegisteredVehiclesRepository(
    private val registeredVehicleDao: RegisteredVehicleDao
) {

    fun getCachedRegisteredVehicles(): Flow<List<RegisteredVehicleEntity>> {
        return registeredVehicleDao.getAllRegisteredVehicles()
    }

    // Ova funkcija je još uvijek korisna ako želite poseban ekran s favoritima
    fun getFavoriteRegisteredVehicles(): Flow<List<RegisteredVehicle>> {
        return registeredVehicleDao.getAllFavoriteRegisteredVehicles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Toggle funkcija sada koristi isključivo RegisteredVehicleEntity ID za ažuriranje
    // jer se isFavorite status pohranjuje direktno u entitet
    suspend fun toggleFavoriteStatus(entityId: Int, currentIsFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            // Dohvati entitet po ID-u
            val existingEntity = registeredVehicleDao.getAllRegisteredVehicles().firstOrNull()?.find { it.id == entityId }

            existingEntity?.let { entity ->
                val updatedEntity = entity.copy(isFavorite = !currentIsFavorite)
                registeredVehicleDao.updateRegisteredVehicle(updatedEntity)
            }
        }
    }

    fun fetchAndCacheRegisteredVehicles(
        request: RegisteredVehicleRequest,
        scope: CoroutineScope,
        onSuccess: (List<RegisteredVehicle>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = RetrofitInstance.api.getRegisteredVehicles(request)

        call.enqueue(object : Callback<ApiResponse<List<RegisteredVehicle>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<RegisteredVehicle>>>,
                response: Response<ApiResponse<List<RegisteredVehicle>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.errors.isNullOrEmpty()) {
                        val apiVehicles = body?.result ?: emptyList()
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                // Dohvati trenutno postojeće entitete iz baze
                                val existingEntities = registeredVehicleDao.getAllRegisteredVehicles().firstOrNull() ?: emptyList()
                                // Kreiraj mapu radi brže pretrage favorita
                                val existingFavoriteMap = existingEntities
                                    .filter { it.isFavorite }
                                    .associateBy { "${it.registrationPlace}-${it.totalDomestic}-${it.totalForeign}-${it.total}" }

                                val entitiesToInsert = apiVehicles.map { apiVehicle ->
                                    val key = "${apiVehicle.registrationPlace}-${apiVehicle.totalDomestic}-${apiVehicle.totalForeign}-${apiVehicle.total}"
                                    // Provjeri je li API vozilo već favorit u bazi
                                    val isFav = existingFavoriteMap.containsKey(key)

                                    // Ako postoji, iskoristi ID postojećeg entiteta kako bi Room znao ažurirati, a ne umetnuti novi red.
                                    // Ako ne postoji, ID će biti 0 i Room će ga generisati.
                                    val existingId = existingEntities.find {
                                        it.registrationPlace == apiVehicle.registrationPlace &&
                                                it.totalDomestic == apiVehicle.totalDomestic &&
                                                it.totalForeign == apiVehicle.totalForeign &&
                                                it.total == apiVehicle.total
                                    }?.id ?: 0

                                    RegisteredVehicleEntity(
                                        id = existingId, // Koristi postojeći ID ako entitet već postoji
                                        registrationPlace = apiVehicle.registrationPlace,
                                        totalDomestic = apiVehicle.totalDomestic,
                                        totalForeign = apiVehicle.totalForeign,
                                        total = apiVehicle.total,
                                        isFavorite = isFav // Postavi isFavorite status
                                    )
                                }

                                registeredVehicleDao.deleteAllRegisteredVehicles() // Obriši sve stare podatke
                                registeredVehicleDao.insertAll(entitiesToInsert) // Ubaci ažurirane podatke
                            }
                            onSuccess(apiVehicles)
                        }
                    } else {
                        onError(body?.errors?.joinToString() ?: "Greška u odgovoru API-ja.")
                    }
                } else {
                    onError("Neuspješan odgovor: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<RegisteredVehicle>>>, t: Throwable) {
                onError("Mrežna greška: ${t.message ?: "Nepoznata greška"}")
            }
        })
    }
}
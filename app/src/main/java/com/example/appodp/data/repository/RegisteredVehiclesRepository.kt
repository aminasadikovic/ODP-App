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

    fun getFavoriteRegisteredVehicles(): Flow<List<RegisteredVehicle>> {
        return registeredVehicleDao.getAllFavoriteRegisteredVehicles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun toggleFavoriteStatus(entityId: Int, currentIsFavorite: Boolean) {
        withContext(Dispatchers.IO) {
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
                                val existingEntities = registeredVehicleDao.getAllRegisteredVehicles().firstOrNull() ?: emptyList()
                                val existingFavoriteMap = existingEntities
                                    .filter { it.isFavorite }
                                    .associateBy { "${it.registrationPlace}-${it.totalDomestic}-${it.totalForeign}-${it.total}" }

                                val entitiesToInsert = apiVehicles.map { apiVehicle ->
                                    val key = "${apiVehicle.registrationPlace}-${apiVehicle.totalDomestic}-${apiVehicle.totalForeign}-${apiVehicle.total}"
                                    val isFav = existingFavoriteMap.containsKey(key)

                                    val existingId = existingEntities.find {
                                        it.registrationPlace == apiVehicle.registrationPlace &&
                                                it.totalDomestic == apiVehicle.totalDomestic &&
                                                it.totalForeign == apiVehicle.totalForeign &&
                                                it.total == apiVehicle.total
                                    }?.id ?: 0

                                    RegisteredVehicleEntity(
                                        id = existingId,
                                        registrationPlace = apiVehicle.registrationPlace,
                                        totalDomestic = apiVehicle.totalDomestic,
                                        totalForeign = apiVehicle.totalForeign,
                                        total = apiVehicle.total,
                                        isFavorite = isFav
                                    )
                                }

                                registeredVehicleDao.deleteAllRegisteredVehicles()
                                registeredVehicleDao.insertAll(entitiesToInsert)
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
package com.example.appodp.data.repository

import com.example.appodp.data.api.RetrofitInstance
import com.example.appodp.data.local.dao.VehicleRegistrationRequestDao // Dodano
import com.example.appodp.data.local.entity.toDomain // Dodano
import com.example.appodp.data.local.entity.toEntity // Dodano
import com.example.appodp.data.model.*
import kotlinx.coroutines.CoroutineScope // Dodano
import kotlinx.coroutines.flow.Flow // Dodano
import kotlinx.coroutines.flow.map // Dodano
import kotlinx.coroutines.launch // Dodano
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleRegistrationRequestsRepository(
    private val dao: VehicleRegistrationRequestDao
) {

    fun getCachedVehicleRegistrationRequests(): Flow<List<VehicleRegistrationRequestResponse>> {
        return dao.getAllVehicleRegistrationRequests().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun fetchAndCacheRequests(
        request: VehicleRegistrationRequestRequest,
        scope: CoroutineScope,
        onSuccess: (List<VehicleRegistrationRequestResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = RetrofitInstance.api.getVehicleRegistrationRequests(request)

        call.enqueue(object : Callback<ApiResponse<List<VehicleRegistrationRequestResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<VehicleRegistrationRequestResponse>>>,
                response: Response<ApiResponse<List<VehicleRegistrationRequestResponse>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.errors.isNullOrEmpty()) {
                        val apiRequests = body?.result ?: emptyList()
                        scope.launch {
                            dao.deleteAllVehicleRegistrationRequests()
                            dao.insertAll(apiRequests.map { it.toEntity() })
                            onSuccess(apiRequests)
                        }
                    } else {
                        onError(body?.errors?.joinToString() ?: "Greška u odgovoru API-ja.")
                    }
                } else {
                    onError("Neuspješan odgovor: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<VehicleRegistrationRequestResponse>>>, t: Throwable) {
                onError("Greška: ${t.message}")
            }
        })
    }
}
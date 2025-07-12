package com.example.appodp.data.repository

import com.example.appodp.data.api.RetrofitInstance
import com.example.appodp.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleRegistrationRequestsRepository {

    fun fetchRequests(
        request: VehicleRegistrationRequestRequest,
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
                        onSuccess(body?.result ?: emptyList())
                    } else {
                        onError(body?.errors?.joinToString() ?: "Greška u odgovoru.")
                    }
                } else {
                    onError("Neuspješan odgovor: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<VehicleRegistrationRequestResponse>>>, t: Throwable) {
                onError("Greška: ${t.message}")
            }
        })
    }
}

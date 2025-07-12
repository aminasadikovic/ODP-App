package com.example.appodp.data.repository

import com.example.appodp.data.api.RetrofitInstance
import com.example.appodp.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisteredVehiclesBulletinRepository {

    fun fetchBulletin(
        request: RegisteredVehiclesBulletinRequest,
        onSuccess: (List<RegisteredVehiclesBulletinResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = RetrofitInstance.api.getRegisteredVehiclesBulletin(request)

        call.enqueue(object : Callback<ApiResponse<List<RegisteredVehiclesBulletinResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<RegisteredVehiclesBulletinResponse>>>,
                response: Response<ApiResponse<List<RegisteredVehiclesBulletinResponse>>>
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

            override fun onFailure(call: Call<ApiResponse<List<RegisteredVehiclesBulletinResponse>>>, t: Throwable) {
                onError("Greška: ${t.message}")
            }
        })
    }
}

package com.example.appodp.data.repository

import com.example.appodp.data.model.RegisteredVehicle
import com.example.appodp.data.model.RegisteredVehicleRequest
import com.example.appodp.data.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisteredVehiclesRepository {
    fun fetchRegisteredVehicles(
        request: RegisteredVehicleRequest,
        onSuccess: (List<RegisteredVehicle>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = RetrofitInstance.api.getRegisteredVehicles(request)
        call.enqueue(object : Callback<List<RegisteredVehicle>> {
            override fun onResponse(
                call: Call<List<RegisteredVehicle>>,
                response: Response<List<RegisteredVehicle>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError("Prazan odgovor")
                } else {
                    onError("Greška: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<RegisteredVehicle>>, t: Throwable) {
                onError("Greška: ${t.message}")
            }
        })
    }
}

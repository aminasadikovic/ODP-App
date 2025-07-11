package com.example.appodp.data.repository

import com.example.appodp.data.model.ApiResponse
import com.example.appodp.data.api.RetrofitInstance
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.data.model.ActiveRegistrationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveRegistrationsRepository {

    fun fetchRegistrations(
        request: ActiveRegistrationRequest,
        onSuccess: (List<ActiveRegistration>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = RetrofitInstance.api.getActiveRegistrations(request)

        call.enqueue(object : Callback<ApiResponse<List<ActiveRegistration>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<ActiveRegistration>>>,
                response: Response<ApiResponse<List<ActiveRegistration>>>
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

            override fun onFailure(call: Call<ApiResponse<List<ActiveRegistration>>>, t: Throwable) {
                onError("Greška: ${t.message}")
            }
        })
    }
}

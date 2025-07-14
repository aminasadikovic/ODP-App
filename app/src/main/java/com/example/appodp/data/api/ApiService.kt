package com.example.appodp.data.api

import com.example.appodp.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/NumberOfActiveRegistrations/list")
    fun getActiveRegistrations(
        @Body request: ActiveRegistrationRequest
    ): Call<ApiResponse<List<ActiveRegistration>>>

    @POST("api/RegisteredVehiclesNumbers/list")
    fun getRegisteredVehicles(
        @Body request: RegisteredVehicleRequest
    ): Call<ApiResponse<List<RegisteredVehicle>>>

    @POST("api/VehicleRegistrationRequests/list")
    fun getVehicleRegistrationRequests(
        @Body request: VehicleRegistrationRequestRequest
    ): Call<ApiResponse<List<VehicleRegistrationRequestResponse>>>


}

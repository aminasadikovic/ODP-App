package com.example.appodp.data.api

import com.example.appodp.data.model.*
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call

interface ApiService {

    @Headers(
        "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIyMDg4IiwibmJmIjoxNzUyMTgzNzEwLCJleHAiOjE3NTIyNzAxMTAsImlhdCI6MTc1MjE4MzcxMH0.LTD9lKoH39q5mKtP5ONwuNK1HMeU58g44leStiQ0VW2unyBuPXWRjCBwO1mWqOBLPEz6QIOWaOy1e4k9RXFzlw",
        "Accept: application/json"
    )
    @POST("api/NumberOfActiveRegistrations/list")
    fun getActiveRegistrations(
        @Body request: ActiveRegistrationRequest
    ): Call<ApiResponse<List<ActiveRegistration>>>
}

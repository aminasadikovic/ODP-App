package com.example.appodp.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://odp.iddeea.gov.ba:8096/"

    private val authInterceptor = Interceptor { chain ->
        val request: Request = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIyMDg4IiwibmJmIjoxNzUyNDA5OTAzLCJleHAiOjE3NTI0OTYzMDMsImlhdCI6MTc1MjQwOTkwM30.oLsAjMDlAS6oMO12rVsZ_nBpZXglRGj4kOFjOY8bywdnVUSwtWIoU2FOLgtJ9NoFFjJKndmaiZs-PMh-l8LEcw"
            )
            .addHeader("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

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
                "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIyMDg4IiwibmJmIjoxNzUyNTk0MDcwLCJleHAiOjE3NTI2ODA0NzAsImlhdCI6MTc1MjU5NDA3MH0.zo7z6aqYnxugcv6UlJlu3UsJnc09HgI96qLUk9h_Bu365e0CPRO2ZQ2dxdifECO6MZRpRtJS1fly5trFiMyWuw"
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

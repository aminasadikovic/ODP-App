package com.example.appodp.data.model

data class ApiResponse<T>(
    val errors: List<String>?,
    val result: T?
)
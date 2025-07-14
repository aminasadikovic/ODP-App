package com.example.appodp.data.model

data class ActiveRegistrationRequest(
    val updateDate: String? = null, // Format "YYYY-MM-DD" prema tvom primjeru (npr. "2023-06-29")
    val entityId: Int? = null,
    val cantonId: Int? = null,
    val municipalityId: Int? = null
)
package com.example.appodp.data.model

data class RegisteredVehicleRequest(
    val updateDate: String? = null, // Promijenjeno u nulabilni String sa defaultnom null vrijednosti
    val entityId: Int? = null,      // Promijenjeno u nulabilni Int sa defaultnom null vrijednosti
    val cantonId: Int? = null,      // Promijenjeno u nulabilni Int sa defaultnom null vrijednosti
    val municipalityId: Int? = null,// Promijenjeno u nulabilni Int sa defaultnom null vrijednosti
    val year: String? = null,       // Promijenjeno u nulabilni String sa defaultnom null vrijednosti
    val month: String? = null       // Promijenjeno u nulabilni String sa defaultnom null vrijednosti
)
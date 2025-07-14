// com.example.appodp.data.repository.ActiveRegistrationsRepository.kt
package com.example.appodp.data.repository

import com.example.appodp.data.api.RetrofitInstance
import com.example.appodp.data.local.dao.RegistrationDao
import com.example.appodp.data.local.entity.toDomain
import com.example.appodp.data.local.entity.toEntity // DODANO: Za konverziju u entitet
import com.example.appodp.data.model.ActiveRegistration
import com.example.appodp.data.model.ActiveRegistrationRequest
import com.example.appodp.data.model.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow // DODANO
import kotlinx.coroutines.flow.map // DODANO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveRegistrationsRepository(
    private val registrationDao: RegistrationDao // PROMIJENJENO: Samo DAO, nema Context-a
) {
    // DODANO: Funkcija za dohvaćanje keširanih podataka kao Flow
    fun getCachedActiveRegistrations(): Flow<List<ActiveRegistration>> {
        return registrationDao.getAllRegistrations().map { entities ->
            println("ROOM → Pronađeno ${entities.size} zapisa") // ← Dodaj ovo za provjeru
            entities.map { it.toDomain() }
        }
    }

    // PROMIJENJENO: Funkcija za dohvaćanje podataka s mreže i keširanje
    fun fetchAndCacheRegistrations(
        request: ActiveRegistrationRequest,
        scope: CoroutineScope, // DODANO: Primamo CoroutineScope iz ViewModela
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
                        val apiRegistrations = body?.result ?: emptyList()
                        scope.launch { // Pokrenite Room operacije unutar proslijeđenog scope-a
                            registrationDao.deleteAllRegistrations() // Očisti stari keš
                            registrationDao.insertAll(apiRegistrations.map { it.toEntity() }) // Spremi nove
                            onSuccess(apiRegistrations) // Obavijesti ViewModel
                        }
                    } else {
                        onError(body?.errors?.joinToString() ?: "Greška u odgovoru API-ja.")
                    }
                } else {
                    onError("Neuspješan odgovor: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<ActiveRegistration>>>, t: Throwable) {
                onError("Greška: ${t.message}")
            }
        })
    }
}
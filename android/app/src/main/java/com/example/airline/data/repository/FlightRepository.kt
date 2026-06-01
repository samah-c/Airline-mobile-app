package com.example.airline.data.repository

import com.example.airline.data.retrofit.FlightApiService
import com.example.airline.data.retrofit.FlightLookupResponse
import com.example.airline.network.RetrofitClient

class FlightRepository {

    private val api: FlightApiService = RetrofitClient.retrofit
        .create(FlightApiService::class.java)

    suspend fun getFlightHistory(userId: Int): Result<List<FlightLookupResponse>> {
        return try {
            val response = api.getFlightHistory(userId)
            if (response.isSuccessful) {
                val flights = response.body()
                    ?: return Result.failure(Exception("Empty response"))
                Result.success(flights)
            } else {
                Result.failure(Exception(when (response.code()) {
                    401 -> "Non authentifié"
                    403 -> "Accès refusé"
                    404 -> "Aucun vol trouvé"
                    else -> "Erreur serveur (${response.code()})"
                }))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }
}
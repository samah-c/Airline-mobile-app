package com.example.airline.data.repository

import com.example.airline.data.retrofit.FlightApiService
import com.example.airline.data.retrofit.FlightLookupRequest
import com.example.airline.data.retrofit.FlightLookupResponse
import com.example.airline.network.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(){

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

    suspend fun lookupFlight(pnr: String, lastName: String): Result<FlightLookupResponse> {
        return try {
            val response = api.lookupFlight(FlightLookupRequest(pnr, lastName))
            if (response.isSuccessful) {
                val flight = response.body()
                    ?: return Result.failure(Exception("Réponse vide"))
                Result.success(flight)
            } else {
                Result.failure(Exception(when (response.code()) {
                    400 -> "Référence ou nom manquant"
                    401 -> "Non authentifié"
                    404 -> "Réservation introuvable"
                    else -> "Erreur serveur (${response.code()})"
                }))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }
}
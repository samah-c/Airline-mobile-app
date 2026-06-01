package com.example.airline.data.repository

import com.example.airline.data.retrofit.AuthApiService
import com.example.airline.data.retrofit.UpdateProfileRequest
import com.example.airline.data.retrofit.UserResponse
import com.example.airline.network.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val api = RetrofitClient.retrofit.create(AuthApiService::class.java)

    suspend fun getProfile(): Result<UserResponse> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }

    suspend fun updateProfile(name: String, phoneNumber: String): Result<UserResponse> {
        return try {
            val response = api.updateProfile(UpdateProfileRequest(name, phoneNumber))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }
}
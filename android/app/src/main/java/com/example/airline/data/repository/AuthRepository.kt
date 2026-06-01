package com.example.airline.data.repository

import com.example.airline.data.retrofit.AuthApiService
import com.example.airline.data.retrofit.AuthResponse
import com.example.airline.data.retrofit.LoginRequest
import com.example.airline.data.retrofit.RegisterRequest
import com.example.airline.data.retrofit.ForgotPasswordRequest
import com.example.airline.data.retrofit.GoogleSignInRequest
import com.example.airline.network.RetrofitClient

class AuthRepository {

    private val api: AuthApiService = RetrofitClient.retrofit
        .create(AuthApiService::class.java)

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val authResponse = response.body()
                    ?: return Result.failure(Exception("Empty response"))
                Result.success(authResponse)  // ← retourne tout l'objet
            } else {
                Result.failure(Exception(when (response.code()) {
                    401 -> "Email ou mot de passe incorrect"
                    else -> "Erreur serveur (${response.code()})"
                }))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }
    suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String
    ): Result<String> {
        return try {
            val response = api.register(RegisterRequest(name, email, password, phoneNumber))
            if (response.isSuccessful) {
                val token = response.body()?.token
                    ?: return Result.failure(Exception("Empty response"))
                Result.success(token)
            } else {
                val msg = when (response.code()) {
                    409 -> "Cet email est déjà utilisé"
                    400 -> "Données invalides"
                    else -> "Erreur serveur (${response.code()})"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur serveur (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<String> {
        return try {
            val response = api.googleSignIn(GoogleSignInRequest(idToken = idToken))
            if (response.isSuccessful) {
                val token = response.body()?.token
                    ?: return Result.failure(Exception("Token manquant"))
                Result.success(token)
            } else {
                Result.failure(Exception("Échec Google Sign-In: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Impossible de joindre le serveur"))
        }
    }
}
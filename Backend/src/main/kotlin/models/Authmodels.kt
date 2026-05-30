package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int
)

@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phoneNumber: String
)
@Serializable
data class GoogleSignInRequest(
    val idToken: String
)
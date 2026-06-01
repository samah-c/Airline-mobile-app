package com.example.airline.data.retrofit

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class AuthResponse(
    val token: String,
    val userId: Int
)


data class GoogleSignInRequest(
    val idToken: String
)

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phoneNumber: String
)

data class UpdateProfileRequest(
    val name: String,
    val phoneNumber: String
)
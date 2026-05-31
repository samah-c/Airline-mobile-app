package com.example.airline.ui.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailSent: Boolean = false,
    val emailError: String? = null
)
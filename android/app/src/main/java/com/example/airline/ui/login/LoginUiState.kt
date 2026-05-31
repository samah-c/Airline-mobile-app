package com.example.airline.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val isOfflineMode: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                emailError == null && passwordError == null
}
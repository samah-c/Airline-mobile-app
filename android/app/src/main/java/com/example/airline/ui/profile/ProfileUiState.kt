package com.example.airline.ui.profile

data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val saveFlightHistory: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
}
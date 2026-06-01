package com.example.airline.ui.signup

data class SignUpUiState(
    val firstName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignUpSuccessful: Boolean = false,
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val firstNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val isFormValid: Boolean
        get() = firstName.isNotBlank() &&
                email.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                firstNameError == null &&
                emailError == null &&
                phoneNumberError == null &&
                passwordError == null &&
                confirmPasswordError == null
}
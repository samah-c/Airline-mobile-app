package com.example.airline.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.AuthRepository
import com.example.airline.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(firstName: String) {
        _uiState.update {
            it.copy(
                firstName = firstName,
                firstNameError = if (firstName.isBlank() && firstName.isNotEmpty())
                    "First name is required" else null
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = validateEmail(email))
        }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.update {
            it.copy(
                phoneNumber = phoneNumber,
                phoneNumberError = validatePhoneNumber(phoneNumber)
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, passwordError = validatePassword(password))
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword != it.password && confirmPassword.isNotEmpty())
                    "Passwords do not match" else null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun signUp(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _uiState.value

        val firstNameError = if (s.firstName.isBlank()) "First name is required" else null
        val emailError = validateEmail(s.email)
        val phoneNumberError = validatePhoneNumber(s.phoneNumber)
        val passwordError = validatePassword(s.password)
        val confirmPasswordError = if (s.password != s.confirmPassword) "Passwords do not match" else null

        if (firstNameError != null || emailError != null || phoneNumberError != null ||
            passwordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    firstNameError = firstNameError,
                    emailError = emailError,
                    phoneNumberError = phoneNumberError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.register(
                name = s.firstName,
                email = s.email,
                password = s.password,
                phoneNumber = s.phoneNumber
            )

            result.fold(
                onSuccess = { token ->
                    RetrofitClient.setToken(token)
                    _uiState.update { it.copy(isLoading = false, isSignUpSuccessful = true) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    onError(e.message ?: "Sign up failed")
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
        else -> null
    }

    private fun validatePhoneNumber(phone: String): String? = when {
        phone.isBlank() -> "Phone number is required"
        phone.length < 9 -> "Invalid phone number"
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        !password.any { it.isDigit() } -> "Password must contain at least one number"
        else -> null
    }
}
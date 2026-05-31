package com.example.airline.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = if (password.isBlank() && password.isNotEmpty())
                    "Password is required" else null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleOfflineMode() {
        _uiState.update { it.copy(isOfflineMode = !it.isOfflineMode) }
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value

        val emailError = validateEmail(currentState.email)
        val passwordError = if (currentState.password.isBlank()) "Password is required" else null

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Simulation frontend-only (pas de backend)
                kotlinx.coroutines.delay(1500)

                // Mock: accept any valid format email/password
                val mockSuccess = !currentState.isOfflineMode ||
                        (currentState.email == "test@test.com" && currentState.password == "password")

                if (mockSuccess) {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    onSuccess()
                } else {
                    throw Exception("Invalid credentials")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Login failed"
                    )
                }
                onError(_uiState.value.errorMessage ?: "Login failed")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }
}
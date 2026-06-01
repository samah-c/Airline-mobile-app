package com.example.airline.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = validateEmail(email))
        }
    }

    fun sendResetEmail(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val emailError = validateEmail(_uiState.value.email)
        if (emailError != null) {
            _uiState.update { it.copy(emailError = emailError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.forgotPassword(_uiState.value.email)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isEmailSent = true) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    onError(e.message ?: "Failed to send email")
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
}
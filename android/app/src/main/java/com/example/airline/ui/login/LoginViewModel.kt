package com.example.airline.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.AuthRepository
import com.example.airline.data.repository.GoogleAuthRepository
import com.example.airline.network.RetrofitClient
import com.example.airline.notifications.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

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

        // Validation
        val emailError = validateEmail(currentState.email)
        val passwordError = if (currentState.password.isBlank()) "Password is required" else null

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.login(
                email = currentState.email,
                password = currentState.password
            )

            result.fold(
                onSuccess = { token ->
                    RetrofitClient.setToken(token)
                    TokenRepository.fetchAndSaveToken(getApplication())
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                    onError(e.message ?: "Login failed")
                }
            )
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

    private val googleAuthRepository = GoogleAuthRepository()

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Étape 1 : récupérer l'idToken depuis Google
            val tokenResult = googleAuthRepository.getGoogleIdToken(context)

            tokenResult.fold(
                onSuccess = { idToken ->
                    // Étape 2 : envoyer au backend
                    val loginResult = repository.loginWithGoogle(idToken)

                    loginResult.fold(
                        onSuccess = { jwtToken ->
                            RetrofitClient.setToken(jwtToken)
                            TokenRepository.fetchAndSaveToken(getApplication())
                            _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = e.message)
                            }
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
            )
        }
    }
}
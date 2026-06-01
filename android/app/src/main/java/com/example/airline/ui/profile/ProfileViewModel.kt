package com.example.airline.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadUserProfile() }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getProfile().fold(
                onSuccess = { user ->
                    android.util.Log.d("PROFILE", "Loaded: ${user.name} / ${user.email}")
                    val parts = user.name.trim().split(" ", limit = 2)
                    _uiState.update {
                        it.copy(
                            firstName = parts.getOrElse(0) { "" },
                            lastName = parts.getOrElse(1) { "" },
                            email = user.email,
                            phoneNumber = user.phoneNumber,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    android.util.Log.e("PROFILE", "Error: ${e.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
            )
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun onPhoneNumberChange(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun toggleSaveFlightHistory() {
        _uiState.update { it.copy(saveFlightHistory = !it.saveFlightHistory) }
    }

    fun saveProfile() {
        if (!_uiState.value.isFormValid) return
        val state = _uiState.value
        val fullName = "${state.firstName.trim()} ${state.lastName.trim()}"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.updateProfile(fullName, state.phoneNumber).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    delay(2000)
                    _uiState.update { it.copy(isSuccess = false) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
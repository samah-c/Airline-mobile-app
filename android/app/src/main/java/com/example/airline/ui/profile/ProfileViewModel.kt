package com.example.airline.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Charger les données utilisateur (simulé)
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(500) // Simulation chargement

            // Données simulées - à remplacer par appel API
            _uiState.update {
                it.copy(
                    firstName = "puerto_rico",
                    lastName = "Puerto Rico",
                    email = "puertorico@example.dz",
                    phoneNumber = "",
                    isLoading = false
                )
            }
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
        _uiState.update {
            it.copy(saveFlightHistory = !it.saveFlightHistory)
        }
    }

    fun saveProfile() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Simulation sauvegarde

            // TODO: Appel API pour sauvegarder
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true
                )
            }

            // Reset success state après 2s
            delay(2000)
            _uiState.update { it.copy(isSuccess = false) }
        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
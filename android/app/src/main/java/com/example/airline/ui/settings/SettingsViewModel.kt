package com.example.airline.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserSettings()
    }

    fun loadUserSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Données simulées - à remplacer par appel API/SharedPreferences
            _uiState.update {
                it.copy(
                    userName = "puerto_rico",
                    userEmail = "puertorico@example.dz",
                    notificationsEnabled = true,
                    darkModeEnabled = false,
                    language = "Français",
                    appVersion = "1.0.0",
                    isLoading = false
                )
            }
        }
    }

    fun toggleNotifications() {
        _uiState.update {
            it.copy(notificationsEnabled = !it.notificationsEnabled)
        }
    }

    fun toggleDarkMode() {
        _uiState.update {
            it.copy(darkModeEnabled = !it.darkModeEnabled)
        }
    }

    fun updateLanguage(language: String) {
        _uiState.update { it.copy(language = language) }
    }

    fun logout() {
        // TODO: Clear session, navigate to login
        viewModelScope.launch {
            // Simulation déconnexion
            kotlinx.coroutines.delay(300)
            // Navigation gérée par l'UI via callback
        }
    }
}
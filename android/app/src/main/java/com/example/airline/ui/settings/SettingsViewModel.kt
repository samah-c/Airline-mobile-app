package com.example.airline.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.local.SessionManager
import com.example.airline.data.local.SettingsPreferences
import com.example.airline.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { loadSettings() }

    fun loadSettings() {
        val ctx = getApplication<Application>()

        viewModelScope.launch {
            // Settings locaux
            _uiState.update {
                it.copy(
                    notificationsEnabled = SettingsPreferences.getNotifications(ctx),
                    darkModeEnabled = SettingsPreferences.getDarkMode(ctx),
                    language = SettingsPreferences.getLanguage(ctx),
                    isLoading = true
                )
            }

            // Nom + email depuis API
            val token = SessionManager.getToken(ctx)
            if (token != null) {
                UserRepository().getProfile().fold(
                    onSuccess = { user ->
                        _uiState.update {
                            it.copy(
                                userName = user.name,
                                userEmail = user.email,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                )
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleNotifications() {
        val ctx = getApplication<Application>()
        val new = !_uiState.value.notificationsEnabled
        SettingsPreferences.setNotifications(ctx, new)
        _uiState.update { it.copy(notificationsEnabled = new) }
    }

    fun toggleDarkMode() {
        val ctx = getApplication<Application>()
        val new = !_uiState.value.darkModeEnabled
        SettingsPreferences.setDarkMode(ctx, new)
        _uiState.update { it.copy(darkModeEnabled = new) }
    }

    fun updateLanguage(language: String) {
        val ctx = getApplication<Application>()
        SettingsPreferences.setLanguage(ctx, language)
        _uiState.update { it.copy(language = language) }
    }

    fun logout() {
        SessionManager.clear(getApplication())
    }
}
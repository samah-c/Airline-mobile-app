package com.example.airline.ui.settings

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val language: String = "Français",
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = false
)
package com.example.airline.ui.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfirmationUiState(
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

class ConfirmationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConfirmationUiState())
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()

    private fun setProcessing(processing: Boolean) {
        _uiState.update { it.copy(isProcessing = processing) }
    }

    private fun setError(message: String?) {
        _uiState.update { it.copy(errorMessage = message, isProcessing = false) }
    }

    fun confirm(onSuccess: suspend () -> Unit) {
        viewModelScope.launch {
            setProcessing(true)
            setError(null)
            try {
                onSuccess()
            } catch (e: Exception) {
                setError(e.message ?: "Confirmation failed")
            } finally {
                setProcessing(false)
            }
        }
    }
}

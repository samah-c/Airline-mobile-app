package com.example.airline.ui.baggage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BaggageViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BaggageUiState())
    val uiState: StateFlow<BaggageUiState> = _uiState.asStateFlow()

    // --- Bagage Cabine ---
    fun incrementCabinCount() {
        _uiState.update { it.copy(cabinBagCount = it.cabinBagCount + 1) }
    }

    fun decrementCabinCount() {
        if (_uiState.value.cabinBagCount > 0) {
            _uiState.update { it.copy(cabinBagCount = it.cabinBagCount - 1) }
        }
    }

    fun incrementCabinWeight() {
        _uiState.update { it.copy(cabinWeight = it.cabinWeight + 1) }
    }

    fun decrementCabinWeight() {
        if (_uiState.value.cabinWeight > 0) {
            _uiState.update { it.copy(cabinWeight = it.cabinWeight - 1) }
        }
    }

    // --- Bagage Soute ---
    fun incrementHoldCount() {
        _uiState.update {
            it.copy(
                holdBagCount = it.holdBagCount + 1,
                totalPrice = it.totalPrice + 15 // Prix fictif
            )
        }
    }

    fun decrementHoldCount() {
        if (_uiState.value.holdBagCount > 0) {
            _uiState.update {
                it.copy(
                    holdBagCount = it.holdBagCount - 1,
                    totalPrice = it.totalPrice - 15
                )
            }
        }
    }

    // --- Navigation ---
    fun confirm() {
        // TODO: Appeler le backend ou naviguer vers le paiement
    }
}
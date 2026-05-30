package com.example.airline.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.SeatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SeatSelectionUiState(
    val selectedSeats: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SeatSelectionViewModel(
    private val repository: SeatRepository = SeatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeatSelectionUiState())
    val uiState: StateFlow<SeatSelectionUiState> = _uiState

    fun toggleSeat(seatId: String) {
        val current = _uiState.value.selectedSeats
        _uiState.value = _uiState.value.copy(
            selectedSeats = if (seatId in current) current - seatId else current + seatId
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedSeats = emptySet())
    }

    fun confirmSelection(flightId: String, onSuccess: () -> Unit) {
        val seats = _uiState.value.selectedSeats
        if (seats.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                seats.forEach { seatId -> repository.reserveSeat(flightId, seatId) }
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    class Factory(private val repository: SeatRepository = SeatRepository()) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeatSelectionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SeatSelectionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

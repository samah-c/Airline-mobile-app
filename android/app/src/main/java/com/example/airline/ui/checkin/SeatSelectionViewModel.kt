package com.example.airline.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.model.SeatModel
import com.example.airline.data.model.SeatStateType
import com.example.airline.data.repository.SeatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SeatSelectionUiState(
    val selectedSeats: Set<String> = emptySet(),
    val availableSeats: List<SeatModel> = emptyList(),
    val occupiedFromApi: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isConfirming: Boolean = false,
    val error: String? = null,
    val confirmed: Boolean = false
)

class SeatSelectionViewModel(
    private val repository: SeatRepository = SeatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeatSelectionUiState())
    val uiState: StateFlow<SeatSelectionUiState> = _uiState

    fun loadSeats(flightId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val seats = repository.getSeats(flightId)
                val occupied = seats.filter { it.state == SeatStateType.OCCUPIED }.map { it.id }.toSet()
                _uiState.value = _uiState.value.copy(availableSeats = seats, occupiedFromApi = occupied, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleSeat(seatId: String) {
        val current = _uiState.value.selectedSeats
        _uiState.value = _uiState.value.copy(
            selectedSeats = if (seatId in current) current - seatId else current + seatId
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedSeats = emptySet())
    }

    fun confirmSelection(checkInId: Int, onSuccess: () -> Unit) {
        val seats = _uiState.value.selectedSeats
        if (seats.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConfirming = true, error = null)
            try {
                val seatId = seats.first()
                val success = repository.selectSeat(checkInId, seatId)
                if (success) {
                    _uiState.value = _uiState.value.copy(isConfirming = false, confirmed = true)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(isConfirming = false, error = "Failed to confirm seat")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isConfirming = false, error = e.message)
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

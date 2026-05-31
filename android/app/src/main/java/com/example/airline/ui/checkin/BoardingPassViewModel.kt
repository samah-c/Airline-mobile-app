package com.example.airline.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.model.BoardingPassModel
import com.example.airline.data.repository.BoardingPassRepository
import com.example.airline.data.repository.OfflineBoardingPassCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BoardingPassUiState(
    val boardingPass: BoardingPassModel = BoardingPassModel(),
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null
)

class BoardingPassViewModel(
    private val repository: BoardingPassRepository = BoardingPassRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardingPassUiState())
    val uiState: StateFlow<BoardingPassUiState> = _uiState

    fun loadBoardingPass(checkInId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val bp = repository.getBoardingPass(checkInId)
                if (bp != null) {
                    _uiState.value = _uiState.value.copy(boardingPass = bp, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Boarding pass not found")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun generateBoardingPass(checkInId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)
            try {
                val bp = repository.generateBoardingPass(checkInId)
                if (bp != null) {
                    _uiState.value = _uiState.value.copy(boardingPass = bp, isGenerating = false)
                    OfflineBoardingPassCache.save(bp)
                } else {
                    _uiState.value = _uiState.value.copy(isGenerating = false, error = "Failed to generate boarding pass")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGenerating = false, error = e.message)
            }
        }
    }

    class Factory(private val repository: BoardingPassRepository = BoardingPassRepository()) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BoardingPassViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BoardingPassViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

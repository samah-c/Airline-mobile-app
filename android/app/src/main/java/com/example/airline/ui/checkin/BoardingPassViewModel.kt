package com.example.airline.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.model.BoardingPassModel
import com.example.airline.data.repository.BoardingPassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BoardingPassUiState(
    val boardingPass: BoardingPassModel = BoardingPassModel(),
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val error: String? = null
)

class BoardingPassViewModel(
    private val repository: BoardingPassRepository = BoardingPassRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardingPassUiState())
    val uiState: StateFlow<BoardingPassUiState> = _uiState

    fun loadBoardingPass(flightId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val bp = repository.getBoardingPass(flightId)
                _uiState.value = _uiState.value.copy(boardingPass = bp, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun download(flightId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDownloading = true)
            try {
                repository.downloadBoardingPass(flightId)
            } finally {
                _uiState.value = _uiState.value.copy(isDownloading = false)
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

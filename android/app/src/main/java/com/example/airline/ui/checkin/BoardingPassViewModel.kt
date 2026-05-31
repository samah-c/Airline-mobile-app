package com.example.airline.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.model.BoardingPassModel
import com.example.airline.data.repository.BoardingPassRepository
import com.example.airline.data.repository.OfflineBoardingPassCache
import com.example.airline.local.AppDatabase
import com.example.airline.local.BoardingPassEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BoardingPassUiState(
    val boardingPass: BoardingPassModel = BoardingPassModel(),
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val isDownloadingPdf: Boolean = false,
    val pdfBytes: ByteArray? = null,
    val error: String? = null
)

class BoardingPassViewModel(
    application: Application,
    private val repository: BoardingPassRepository = BoardingPassRepository()
) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).boardingPassDao()

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
                    loadFromRoom()
                }
            } catch (e: Exception) {
                loadFromRoom()
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
                    // Save to Room for persistent offline access
                    dao.insert(bp.toEntity())
                } else {
                    _uiState.value = _uiState.value.copy(isGenerating = false, error = "Failed to generate boarding pass")
                    loadFromRoom()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGenerating = false, error = e.message)
                loadFromRoom()
            }
        }
    }

    fun downloadPdf(checkInId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDownloadingPdf = true)
            try {
                val bytes = repository.downloadPdf(checkInId)
                _uiState.value = _uiState.value.copy(isDownloadingPdf = false, pdfBytes = bytes)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isDownloadingPdf = false, error = e.message)
            }
        }
    }

    private suspend fun loadFromRoom() {
        val saved = dao.getAll().firstOrNull()
        if (saved != null) {
            _uiState.value = _uiState.value.copy(
                boardingPass = saved.toModel(),
                isLoading    = false,
                isGenerating = false,
                error        = null
            )
        } else {
            _uiState.value = _uiState.value.copy(isLoading = false, isGenerating = false)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BoardingPassViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BoardingPassViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

private fun BoardingPassModel.toEntity() = BoardingPassEntity(
    flightNumber  = flightNumber,
    gate          = gate,
    origin        = origin,
    destination   = destination,
    passengerName = passengerName,
    seat          = seat,
    seatClass     = seatClass,
    departureTime = departureTime,
    barcode       = barcode,
    qrCode        = qrCode
)

private fun BoardingPassEntity.toModel() = BoardingPassModel(
    flightNumber    = flightNumber,
    gate            = gate,
    origin          = origin,
    originCity      = origin,
    destination     = destination,
    destinationCity = destination,
    passengerName   = passengerName,
    seat            = seat,
    seatClass       = seatClass,
    boardingTime    = departureTime,
    departureTime   = departureTime,
    barcode         = barcode,
    qrCode          = qrCode
)

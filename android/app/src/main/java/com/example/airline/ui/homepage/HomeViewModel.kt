package com.example.airline.ui.homepage


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.FlightRepository
import com.example.airline.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "Puerto",
    val pnr: String = "",
    val lastName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val flightRepository: FlightRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserName()                               // ← fetch on launch
    }

    private fun loadUserName() {
        viewModelScope.launch {
            userRepository.getProfile()
                .onSuccess { user ->
                    _uiState.update { it.copy(userName = user.name) }
                }
        }
    }


    fun onPnrChange(value: String) {
        _uiState.update { it.copy(pnr = value, errorMessage = null) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value, errorMessage = null) }
    }

    fun validateAndSearch(onSuccess: (pnr: String, lastName: String) -> Unit) {
        val state = _uiState.value
        when {
            state.pnr.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Veuillez entrer votre référence de réservation") }
            }
            state.lastName.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Veuillez entrer votre nom de famille") }
            }
            else -> onSuccess(state.pnr.trim(), state.lastName.trim())
        }
    }
}
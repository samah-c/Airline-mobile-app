package com.example.airline.ui.flighthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.FlightRepository
import com.example.airline.data.retrofit.FlightLookupResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class FlightHistoryViewModel(
    private val userId: Int,
    private val repository: FlightRepository = FlightRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightHistoryUiState())
    val uiState: StateFlow<FlightHistoryUiState> = _uiState.asStateFlow()

    init {
        loadFlightHistory()
    }

    fun loadFlightHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getFlightHistory(userId)
                .onSuccess { flights ->
                    _uiState.update {
                        it.copy(
                            flights = flights.map { it.toFlightItem() },
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // ✅ Custom factory so userId can be injected
    companion object {
        fun factory(userId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FlightHistoryViewModel(userId) as T
            }
    }
}

// ── Mapper ────────────────────────────────────────────────────
private val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private val dateFormatter  = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")
private val timeFormatter  = DateTimeFormatter.ofPattern("HH:mm")

private fun FlightLookupResponse.toFlightItem(): FlightItem {
    val departure = LocalDateTime.parse(flight.departureTime, inputFormatter)
    val arrival   = LocalDateTime.parse(flight.arrivalTime,   inputFormatter)

    val totalMinutes = ChronoUnit.MINUTES.between(departure, arrival)
    val hours   = totalMinutes / 60
    val minutes = totalMinutes % 60
    val duration = if (hours > 0) "${hours}h${minutes.toString().padStart(2, '0')}m" else "${minutes}m"

    return FlightItem(
        id               = booking.id.toString(),
        flightNumber     = flight.flightNumber,
        date             = departure.format(dateFormatter),
        departureTime    = departure.format(timeFormatter),
        departureAirport = flight.origin,
        arrivalTime      = arrival.format(timeFormatter),
        arrivalAirport   = flight.destination,
        duration         = duration,
        cabinClass       = "Economy",
        year             = departure.year
    )
}
package com.example.airline.ui.flightlookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.FlightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

data class FlightLookUpResultUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val checkInStatus: String = "",
    val flightNumber: String = "",
    val departureDate: String = "",
    val departureTime: String = "",
    val departureAirport: String = "",
    val arrivalDate: String = "",
    val arrivalTime: String = "",
    val arrivalAirport: String = "",
    val duration: String = "",
    val cabinClass: String = ""
)

@HiltViewModel
class FlightLookUpResultViewModel @Inject constructor(
    private val flightRepository: FlightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightLookUpResultUiState())
    val uiState: StateFlow<FlightLookUpResultUiState> = _uiState.asStateFlow()

    fun loadFlight(pnr: String, lastName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            flightRepository.lookupFlight(pnr, lastName)
                .onSuccess { result ->
                    val departureDateTime = result.flight.departureTime
                    val arrivalDateTime = result.flight.arrivalTime

                    // Parse using LocalDateTime (no timezone in backend response)
                    val depLocalDateTime = LocalDateTime.parse(departureDateTime)
                    val arrLocalDateTime = LocalDateTime.parse(arrivalDateTime)

                    // Calculate duration in minutes
                    val durationMinutes = ChronoUnit.MINUTES.between(depLocalDateTime, arrLocalDateTime)
                    val hours = durationMinutes / 60
                    val minutes = durationMinutes % 60
                    val durationFormatted = "${hours}h ${minutes}m"

                    // Format dates and times
                    val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

                    _uiState.update {
                        it.copy(
                            isLoading        = false,
                            checkInStatus    = result.booking.checkInStatus,
                            flightNumber     = result.flight.flightNumber,
                            departureDate    = depLocalDateTime.format(formatterDate),
                            departureTime    = depLocalDateTime.format(formatterTime),
                            departureAirport = result.flight.origin,
                            arrivalDate      = arrLocalDateTime.format(formatterDate),
                            arrivalTime      = arrLocalDateTime.format(formatterTime),
                            arrivalAirport   = result.flight.destination,
                            duration         = durationFormatted,
                            cabinClass       = ""
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
}